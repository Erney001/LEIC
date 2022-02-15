from ryu.base import app_manager
from ryu.controller import ofp_event
from ryu.controller.handler import CONFIG_DISPATCHER, MAIN_DISPATCHER
from ryu.controller.handler import set_ev_cls
from ryu.ofproto import ofproto_v1_3
from ryu.lib.packet import packet
from ryu.lib.packet.packet import Packet
from ryu.lib.packet import ethernet
from ryu.lib.packet import ether_types
from ryu.lib.packet import arp
from itertools import cycle


class LoadBalancer(app_manager.RyuApp):
    # OpenFlow version.
    OFP_VERSIONS = [ofproto_v1_3.OFP_VERSION]

    # Virtual server IP.
    ip_virtual = '10.0.10.10'
    # Current server IP.
    current_server = ''
    # Next server IP.
    next_server = ''
    # List of servers to be used by the round-robin algorithm.
    server_pool = ['10.0.0.7', '10.0.0.8', '10.0.0.9']
    # Round-robin algorithm.
    iter = cycle(server_pool)

    # Translation from host IP address to switch port.
    ip_to_port = {'10.0.0.1': 1,
                 '10.0.0.2': 2,
                 '10.0.0.3': 3,
                 '10.0.0.4': 4,
                 '10.0.0.5': 5,
                 '10.0.0.6': 6,
                 '10.0.0.7': 7,
                 '10.0.0.8': 8,
                 '10.0.0.9': 9}
    # Translation from host IP adress to host MAC address.
    ip_to_mac = {'10.0.0.1': '00:00:00:00:00:01',
                 '10.0.0.2': '00:00:00:00:00:02',
                 '10.0.0.3': '00:00:00:00:00:03',
                 '10.0.0.4': '00:00:00:00:00:04',
                 '10.0.0.5': '00:00:00:00:00:05',
                 '10.0.0.6': '00:00:00:00:00:06',
                 '10.0.0.7': '00:00:00:00:00:07',
                 '10.0.0.8': '00:00:00:00:00:08',
                 '10.0.0.9': '00:00:00:00:00:09'}
                 
    from lbbase import _packet_in_handler, switch_features_handler, add_flow_miss, arp_reply, round_robin


    def __init__(self, *args, **kwargs):
        super(LoadBalancer, self).__init__(*args, **kwargs)

        # Select the first server using round robin.
        self.current_server = self.round_robin()


    # Sets up the flow table in the switch to map a host to the current server.
    # Responsible for installing two flow rules, host->server and the reverse server->host.
    # ip_src: current client host IP.
    # ip_dst: virtual server to which the client is attempting to connect.
    def add_flow(self, datapath, ip_src, ip_dst, ofp_parser, ofp, port_in):
        # Install flow from host to server.

	# -------------
        # TO-DO: Configure the following parameters for the host->server flow entry.
        # -------------
        # Destination IP to match for the current connection.
        # flow_match_ip_dst = [...]
        # Destination IP to insert in the flow rule action.
        # flow_action_ip_dst = [...]

	# Flow match parameters.
        match = ofp_parser.OFPMatch(in_port=port_in, ipv4_dst=flow_match_ip_dst, eth_type=0x0800)
        # Flow action parameters.
        actions = [ofp_parser.OFPActionSetField(ipv4_dst=flow_action_ip_dst), ofp_parser.OFPActionOutput(self.ip_to_port[self.current_server])]
        inst = [ofp_parser.OFPInstructionActions(ofp.OFPIT_APPLY_ACTIONS, actions)]
        # Flow modification rule.
        mod = ofp_parser.OFPFlowMod( datapath=datapath, priority=1, buffer_id=ofp.OFP_NO_BUFFER, match=match, instructions=inst)

        # Send flow modification rule to the switch.
        datapath.send_msg(mod)

        self.logger.info('Installed flow from host %s to server %s', ip_src, self.current_server)

        # Install reverse flow from server to host.

	# -------------
        # TO-DO: Configure the following parameters for the server->host flow entry.
        # -------------
        # Port to match for the current connection.
        # flow_match_port_in = [...]
        # Source IP to match for the current connection.
        # flow_match_ip_src = [...]
        # Destination IP to match for the current connection.
        # flow_match_ip_dst = [...]
        # Source IP to insert in the flow rule action.
        # flow_action_ip_src = [...]

	# Flow match parameters.
        match = ofp_parser.OFPMatch(in_port=flow_match_port_in, ipv4_src=flow_match_ip_src, ipv4_dst=flow_match_ip_dst, eth_type=ether_types.ETH_TYPE_IP)
        # Flow action parameters.
        actions = [ofp_parser.OFPActionSetField(ipv4_src=flow_action_ip_src), ofp_parser.OFPActionOutput(port_in)]
        inst = [ofp_parser.OFPInstructionActions(ofp.OFPIT_APPLY_ACTIONS, actions)]
        # Flow modification rule.
        mod = ofp_parser.OFPFlowMod( datapath=datapath, priority=1, buffer_id=ofp.OFP_NO_BUFFER, match=match, instructions=inst)

        # Send flow modification rule to the switch.
        datapath.send_msg(mod)

        self.logger.info('Installed flow from server %s to host %s', self.current_server, ip_src)

