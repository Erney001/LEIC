/******************************************************************************\
* Link state routing protocol.                                                 *
\******************************************************************************/

#include <stdlib.h>
#include <stdio.h>

#include "routing-simulator.h"

typedef struct {
  cost_t link_cost[MAX_NODES];
  int version;
} link_state_t;

// Message format to send between nodes.
typedef struct {
  link_state_t ls[MAX_NODES];
} message_t;

// State format.
typedef struct {
  link_state_t * cost;
  node_t * next_hop;
} state_t;

int dijkstra(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes, node_t sender);
void warn_neighbors(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes);

// Handler for the node to allocate and initialize its state.
void * init_state() {
  state_t * state = (state_t *) calloc(1, sizeof(state_t));

  node_t first = get_first_node();
  node_t current = get_current_node();
  node_t last = get_last_node();
  node_t total_nodes = last - first + 1;

  state->cost = (link_state_t *) calloc(total_nodes, sizeof(link_state_t));
  state->next_hop = (node_t *) calloc(total_nodes, sizeof(node_t));

  for(node_t n = first; n < total_nodes; n++){
    state->next_hop[n] = COST_INFINITY;
    state->cost[n].version = 0;
    for(node_t nn = first; nn <= last; nn++){
      state->cost[n].link_cost[nn] = COST_INFINITY;
      if(n == current && nn == current){
        state->next_hop[n] = n;
        state->cost[n].link_cost[nn] = 0;
      }
    }
  }

  return state;
}

// Notify a node that a neighboring link has changed cost.
void notify_link_change(node_t neighbor, cost_t new_cost) {
  state_t * actual_state = (state_t *) get_state();

  node_t first = get_first_node();
  node_t current = get_current_node();
  node_t last = get_last_node();
  node_t total_nodes = last - first + 1;

  actual_state->cost[current].link_cost[neighbor] = new_cost;
  actual_state->cost[current].version++;
  actual_state->next_hop[neighbor] = neighbor;
  set_route(neighbor, neighbor, new_cost);

  if(new_cost == COST_INFINITY){
    for(node_t i = first; i <= last; i++){
      if(actual_state->next_hop[i] == neighbor){
        set_route(i, neighbor, COST_INFINITY);
      }
    }
  }else{

  }

  printf("New cost %d from %d to %d\n", new_cost, current, neighbor);

  dijkstra(actual_state, first, current, last, total_nodes, neighbor);
  warn_neighbors(actual_state, first, current, last, total_nodes); // always warns neighbors after link change
}

// Receive a message sent by a neighboring node.
void notify_receive_message(node_t sender, void * message, int size) {
  state_t * actual_state = (state_t *) get_state();
  message_t * msg = (message_t *) message;
  int new_version = 0;

  node_t first = get_first_node();
  node_t current = get_current_node();
  node_t last = get_last_node();
  node_t total_nodes = last - first + 1;

  printf("Message received from %d in node %d\n", sender, current);

  for(node_t i = first; i <= last; i++){
    printf("New version: %d vs old %d, regarding node %d in %d\n", msg->ls[i].version, actual_state->cost[i].version, i, current);
    if(msg->ls[i].version > actual_state->cost[i].version){
      actual_state->cost[i].version = msg->ls[i].version;
      for(node_t j = first; j <= last; j++){
        actual_state->cost[i].link_cost[j] = msg->ls[i].link_cost[j];
      }
      new_version = 1;
    }
  }

  if(new_version){
    dijkstra(actual_state, first, current, last, total_nodes, sender);
    warn_neighbors(actual_state, first, current, last, total_nodes);
  }
}

// Dijkstra's algorithm to find least distance path to any other node
int dijkstra(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes, node_t sender){
  int changed = 0;
  link_state_t * tmp_costs = (link_state_t *) calloc(total_nodes, sizeof(link_state_t));
  node_t tmp_hops[total_nodes];

  for(node_t n = first; n < total_nodes; n++){
    tmp_hops[n] = COST_INFINITY;
    for(node_t nn = first; nn <= last; nn++){
      tmp_costs[n].link_cost[nn] = COST_INFINITY;
      if(n == current && nn == current){
        tmp_hops[n] = n;
        tmp_costs[n].link_cost[nn] = 0;
      }
    }
  }

  cost_t current_cost, tmp_cost;
  for(node_t n = first; n < total_nodes; n++){
    if(n != current){
      current_cost = COST_INFINITY;
      for(node_t nn = first; nn < total_nodes; nn++){
        tmp_cost = COST_ADD(actual_state->cost[current].link_cost[nn], actual_state->cost[nn].link_cost[n]);
        cost_t t;
        for(node_t nnn = first; nnn < total_nodes; nnn++){
          if(nn != nnn && nn != current){
            t = COST_ADD(actual_state->cost[current].link_cost[nn] + actual_state->cost[nn].link_cost[nnn], actual_state->cost[nnn].link_cost[n]);
            if(t < tmp_cost){
              tmp_cost = t;
            
            }
          }
        }
        printf(": %d -> %d through %d, cost: %d, tmp_cost: %d\n", current, n, nn, current_cost, tmp_cost);
        node_t tmp_hop = (nn == actual_state->next_hop[nn]) ? nn : actual_state->next_hop[nn];
        printf("%d\n", tmp_hop);
        if(get_link_cost(n) == COST_INFINITY && tmp_hop == current){ // if not a neighbor and itself is hop, cancel route
          continue;
        }
        if(get_link_cost(n) == COST_INFINITY && actual_state->cost[tmp_hop].link_cost[n] == COST_INFINITY){
          continue;
        }
        if(tmp_cost < current_cost && get_link_cost(tmp_hop) != COST_INFINITY){
          printf("Update to node %d: hop %d, cost %d*******************\n", n, tmp_hop, tmp_cost);
          current_cost = tmp_cost;
          tmp_costs[current].link_cost[n] = current_cost;
          tmp_hops[n] = tmp_hop;
        }
      }
    }
  }

  for(node_t n = first; n < total_nodes; n++){
    if(tmp_costs[current].link_cost[n] != actual_state->cost[current].link_cost[n]){
      actual_state->cost[current].link_cost[n] = tmp_costs[current].link_cost[n];
      actual_state->next_hop[n] = tmp_hops[n];
      printf("New route from %d to %d through %d with cost %d\n", current, n, tmp_hops[n], tmp_costs[current].link_cost[n]);
      set_route(n, tmp_hops[n], tmp_costs[current].link_cost[n]);
      changed = 1;
    }
  }

  return changed;
}

// Warns direct neighbors of node if paths changed
void warn_neighbors(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes){
  message_t * msg = (message_t *) calloc(MAX_NODES*(MAX_NODES+1), sizeof(int));

  for(node_t n = first; n <= last; n++){
    msg->ls[n].version = actual_state->cost[n].version;
    for(node_t nn = first; nn <= last; nn++){
      msg->ls[n].link_cost[nn] = actual_state->cost[n].link_cost[nn];
    }
  }

  for(node_t n = first; n <= last; n++){
    if(get_link_cost(n) != COST_INFINITY && n != current){
      send_message(n, msg, (MAX_NODES*(MAX_NODES+1))*sizeof(int));
      printf("Message sent from %d to %d advertising link change\n", current, n);
    }
  }
}
