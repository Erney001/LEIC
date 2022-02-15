/******************************************************************************\
* Distance vector routing protocol without reverse path poisoning.             *
\******************************************************************************/

#include <stdlib.h>
#include <stdio.h>

#include "routing-simulator.h"

// Message format to send between nodes.
typedef struct {
  cost_t * cost;
} message_t;

// State format.
typedef struct {
  cost_t ** cost;
  node_t * next_hop;
} state_t;

int bellman_ford(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes, node_t sender);
void warn_neighbors(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes);

// Handler for the node to allocate and initialize its state.
void * init_state() {
  state_t * state = (state_t *) calloc(1, sizeof(state_t));

  node_t first = get_first_node();
  node_t current = get_current_node();
  node_t last = get_last_node();
  node_t total_nodes = last - first + 1;

  state->cost = (cost_t **) calloc(total_nodes, sizeof(cost_t));
  state->next_hop = (node_t *) calloc(total_nodes, sizeof(node_t));

  for(node_t n = first; n < total_nodes; n++){
    state->next_hop[n] = COST_INFINITY;
    state->cost[n] = (cost_t *) calloc(total_nodes, sizeof(cost_t));
    for(node_t nn = first; nn < total_nodes; nn++){
      state->cost[n][nn] = COST_INFINITY;
      if(n == current && nn == current){
        state->next_hop[n] = n;
        state->cost[n][nn] = 0;
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

  actual_state->cost[current][neighbor] = new_cost;
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

  bellman_ford(actual_state, first, current, last, total_nodes, neighbor); // atualizar custo dentro do bellman ford (HD)
  warn_neighbors(actual_state, first, current, last, total_nodes); // always warns neighbors after link change
}

// Receive a message sent by a neighboring node.
void notify_receive_message(node_t sender, void * message, int size) {
  state_t * actual_state = (state_t *) get_state();
  message_t * msg = (message_t *) message;

  node_t first = get_first_node();
  node_t current = get_current_node();
  node_t last = get_last_node();
  node_t total_nodes = last - first + 1;

  for(node_t i = first; i <= last; i++){
    actual_state->cost[sender][i] = msg->cost[i];
  }

  printf("Message received from %d in node %d\n", sender, current);
  
  if(bellman_ford(actual_state, first, current, last, total_nodes, sender)){ // if DV changed after Bellman-Ford algorithm, warn neighbors
    warn_neighbors(actual_state, first, current, last, total_nodes);
  }
}

// Bellman-Ford algorithm to find least distance path to any other node
int bellman_ford(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes, node_t sender){
  // vou ter de ter um campo para indicar se o novo custo e infinito ou nao para saber o que fazer
  // trabalhar com copias (hops tb?) no bellman ford com tudo inicilizado a cost infinity
  // comparar no fim com o actual state e alterar se forem diferentes (HD)
  // e so faco set route se se tiverem alterado (HD)
  // iterar por hops tb se route agr tem um hop diferente?

  int changed  = 0;
  cost_t tmp_costs[total_nodes];
  node_t tmp_hops[total_nodes];

  for(node_t n = first; n <= last; n++){
    tmp_costs[n] = COST_INFINITY;
    tmp_hops[n] = COST_INFINITY;
    if(n == current){
      tmp_costs[n] = 0;
      tmp_hops[n] = n;
    }
  }

  printf("1*********************************************************************\n");
  for(node_t n = first; n <= last; n++){
    printf("%d: ", n);
    for(node_t nn = first; nn <= last; nn++){
      printf("%d ", actual_state->cost[n][nn]);
    }
    printf("\n");
  }
  printf("**********************************************************************\n");

  cost_t tmp_cost;
  /*for(node_t n = first; n <= last; n++){
    if(actual_state->next_hop[n] == sender){
      printf("From node %d to %d through %d costs %d\n", current, n, sender, actual_state->cost[current][n]);
      for(node_t lin = first; lin <= last; lin++){ // lin = next hop
        if(lin == current){
          continue;
        }
        tmp_cost = COST_ADD(actual_state->cost[current][lin], actual_state->cost[lin][n]);
        printf("tmp cost %d\n", tmp_cost);
        if(tmp_cost < tmp_costs[n]){
          printf("Updated, cost %d, hop %d\n", tmp_cost, lin);
          tmp_costs[n] = tmp_cost;
          tmp_hops[n] = lin;
        }
      }
    }
  }*/

  for(node_t n = first; n <= last; n++){
    printf("From %d to %d through %d, cost: %d\n", current, n, tmp_hops[n], tmp_costs[n]);
  }

  for(node_t col = first; col <= last; col++){ // col = destination
    for(node_t lin = first; lin <= last; lin++){ // lin = next hop
      if(lin != current){
        if(lin == col){
          tmp_cost = COST_ADD(get_link_cost(lin), 0);
        } else{
          tmp_cost = COST_ADD(get_link_cost(lin), actual_state->cost[lin][col]);
        }
        printf("Cost from %d to hop %d: %d, From hop to dest %d: %d, total: %d\n", current, lin, get_link_cost(lin), col, actual_state->cost[lin][col], tmp_cost);
        if(tmp_cost < tmp_costs[col]){
          printf("Updated\n");
          tmp_costs[col] = tmp_cost;
          tmp_hops[col] = lin;
        }
      }
    }
  }

  for(node_t n = first; n <= last; n++){
    printf("From %d to %d through %d, cost: %d\n", current, n, tmp_hops[n], tmp_costs[n]);
  }

  for(node_t n = first; n <= last; n++){
    if(actual_state->cost[current][n] != tmp_costs[n]){
      actual_state->cost[current][n] = tmp_costs[n];
      //tmp_hops[n] = (tmp_hops[n] == COST_INFINITY) ? n : tmp_costs[n];
      actual_state->next_hop[n] = tmp_hops[n];
      set_route(n, tmp_hops[n], tmp_costs[n]);
      printf("New route from %d to %d through %d with cost %d\n", current, n, tmp_hops[n], tmp_costs[n]);
      changed = 1;
    }
  }

  printf("2*********************************************************************\n");
  for(node_t n = first; n <= last; n++){
    printf("%d: ", n);
    for(node_t nn = first; nn <= last; nn++){
      printf("%d ", actual_state->cost[n][nn]);
    }
    printf("\n");
  }
  printf("**********************************************************************\n");

  return changed; // returns 1 if DV changed
}

// Warns direct neighbors of node if DV changed
void warn_neighbors(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes){
  message_t * msg = (message_t *) calloc(1, sizeof(message_t));
  msg->cost = (cost_t *) calloc(total_nodes, sizeof(cost_t));

  for(node_t i = first; i < total_nodes; i++){
    msg->cost[i] = actual_state->cost[current][i];
  }

  for(node_t n = first; n <= last; n++){
    if(get_link_cost(n) != COST_INFINITY && n != current){
      send_message(n, msg, sizeof(msg));
      printf("Message sent from %d to %d advertising link change\n", current, n);
    }
  }
}
