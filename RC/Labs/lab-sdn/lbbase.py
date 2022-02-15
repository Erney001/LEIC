from ryu.controller.handler import set_ev_cls
from ryu.controller import ofp_event
from ryu.controller.handler import CONFIG_DISPATCHER, MAIN_DISPATCHER
from ryu.lib.packet import ethernet
from ryu.lib.packet import ether_types
from ryu.lib.packet import arp
from ryu.lib.packet import packet
from ryu.lib.packet.packet import Packet
from itertools import cycle


server_pool = ['10.0.0.7', '10.0.0.8', '10.0.0.9']

# Round-robin algorithm.
iter = cycle(server_pool)
def round_robin(self):
    return next(self.iter)

# During the initial configuration, the controller adds a table miss entry to the switch.
# This flow entry wildcards all fields and is used by Ryu to forward unmatched packets to the controller.
@set_ev_cls(ofp_event.EventOFPSwitchFeatures, CONFIG_DISPATCHER)
def switch_features_handler(self, ev):
    datapath = ev.msg.datapath
    ofproto = datapath.ofproto
    parser = datapath.ofproto_parser

    # install the table-miss flow entry.
    match = parser.OFPMatch()
    actions = [parser.OFPActionOutput(ofproto.OFPP_CONTROLLER, ofproto.OFPCML_NO_BUFFER)]
    self.add_flow_miss(datapath, 0, match, actions)


def add_flow_miss(self, datapath, priority, match, actions):
    ofproto = datapath.ofproto
    parser = datapath.ofproto_parser

    # Install table miss entry.
    inst = [parser.OFPInstructionActions(ofproto.OFPIT_APPLY_ACTIONS, actions)]
    mod = parser.OFPFlowMod(datapath=datapath, priority=priority, match=match, instructions=inst)
    datapath.send_msg(mod)


# Event executed whenever a packet arrives from the switch after the initial configuration.
@set_ev_cls(ofp_event.EventOFPPacketIn, MAIN_DISPATCHER)
def _packet_in_handler(self, ev):
    msg = ev.msg
    datapath = msg.datapath
    ofproto = datapath.ofproto
    parser = datapath.ofproto_parser

    # analyse the received packets using the packet library.
    pkt = packet.Packet(msg.data)
    eth_pkt = pkt.get_protocol(ethernet.ethernet)

    # get the received port number from packet_in message.
    port_in = msg.match['in_port']

    # Check if the packet_in is an ARP packet, in order to install the necessary flow rules and prepare the ARP reply.
    if eth_pkt.ethertype == ether_types.ETH_TYPE_ARP:

        arp_request_ip_src = pkt.get_protocol(arp.arp).src_ip
        arp_request_ip_dst = pkt.get_protocol(arp.arp).dst_ip

        # Don't install forwarding rules if the ARP request is either received from a server or sent directly to a server.
        if not arp_request_ip_src in self.server_pool and not arp_request_ip_dst in self.server_pool:
            self.add_flow(datapath, arp_request_ip_src, arp_request_ip_dst, parser, ofproto, port_in)
            
        # If the ARP request is coming from a client host, then we use round robin to preemptively select the server for the next connection.
        if arp_request_ip_src not in self.server_pool:
            self.next_server = self.round_robin()
            self.logger.info('The next server will be %s\n', self.next_server)       

        self.arp_reply(datapath, arp_request_ip_src, arp_request_ip_dst, eth_pkt, parser, ofproto, port_in)
        return
    else:
        return


# Sends an ARP reply to the contacting host.
def arp_reply(self, datapath, arp_request_ip_src, arp_request_ip_dst, ether_frame, ofp_parser, ofp, port_in):
    # The source IP of the ARP request is the destination IP of the ARP reply.
    arp_reply_ip_dst = arp_request_ip_src
    # The destination IP of the ARP request is the source IP of the ARP reply.
    arp_reply_ip_src = arp_request_ip_dst
    # The source MAC of the ARP request is the destination MAC of the ARP reply.
    arp_reply_mac_dst = ether_frame.src

    # If the ARP request is coming from a client host, select one of the servers as the source for the ARP reply using round robin.
    # Else, set the ARP reply source MAC as the destination of the ARP request.
    if arp_reply_ip_dst not in self.server_pool:
        arp_reply_mac_src = self.ip_to_mac[self.current_server]
        self.current_server = self.next_server
    else:
        arp_reply_mac_src = self.ip_to_mac[arp_reply_ip_src]

    # Assemble the ARP reply packet.
    e = ethernet.ethernet(arp_reply_mac_dst, arp_reply_mac_src, ether_types.ETH_TYPE_ARP)
    a = arp.arp(1, 0x0800, 6, 4, 2, arp_reply_mac_src, arp_reply_ip_src, arp_reply_mac_dst, arp_reply_ip_dst)
    p = Packet()
    p.add_protocol(e)
    p.add_protocol(a)
    p.serialize()

    # ARP action list.
    actions = [ofp_parser.OFPActionOutput(ofp.OFPP_IN_PORT)]
    # ARP output message.
    out = ofp_parser.OFPPacketOut(datapath=datapath, buffer_id=ofp.OFP_NO_BUFFER, in_port=port_in, actions=actions, data=p.data)
    # Send the packet_out with the ARP reply.
    datapath.send_msg(out)
