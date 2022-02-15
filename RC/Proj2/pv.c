/******************************************************************************\
* Distance vector routing protocol without reverse path poisoning.             *
\******************************************************************************/

#include <stdlib.h>
#include <stdio.h>

#include "routing-simulator.h"

typedef struct {
  cost_t cost;
  node_t * path;
} elem_t;

// Message format to send between nodes.
typedef struct {
  elem_t * vector;
} message_t;

// State format.
typedef struct {
  elem_t ** matrix;
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

  state->matrix = (elem_t **) calloc(total_nodes, sizeof(elem_t));

  for(node_t n = first; n < total_nodes; n++){
    state->matrix[n] = (elem_t *)  calloc(total_nodes, sizeof(elem_t));
    for(node_t nn = first; nn < total_nodes; nn++){
      state->matrix[n][nn].cost = COST_INFINITY;
      state->matrix[n][nn].path = (node_t *) calloc(total_nodes, sizeof(node_t));
      for(node_t nnn = first; nnn < total_nodes; nnn++){
        state->matrix[n][nn].path[nnn] = COST_INFINITY;
      }
      if(n == current && nn == current){
        state->matrix[n][nn].cost = 0;
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

  actual_state->matrix[current][neighbor].cost = new_cost;
  actual_state->matrix[current][neighbor].path[0] = neighbor;
  set_route(neighbor, neighbor, new_cost);

  if(new_cost == COST_INFINITY){
    for(node_t i = first; i <= last; i++){
      if(actual_state->matrix[current][i].path[0] != COST_INFINITY && actual_state->matrix[current][i].path[0] == neighbor){
        set_route(i, neighbor, COST_INFINITY);
      }
    }
  } else{

  }

  printf("New cost %d from %d to %d\n", new_cost, current, neighbor);

  bellman_ford(actual_state, first, current, last, total_nodes, neighbor);
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
    actual_state->matrix[sender][i].cost = msg->vector[i].cost;
    for(node_t j = first; j <= last; j++){
      if(msg->vector[i].path[j] == COST_INFINITY){
        break;
      }
      actual_state->matrix[sender][i].path[j] = msg->vector[i].path[j];
    }
  }

  printf("Message received from %d in node %d\n", sender, current);
  
  if(bellman_ford(actual_state, first, current, last, total_nodes, sender)){ // if DV changed after Bellman-Ford algorithm, warn neighbors
    warn_neighbors(actual_state, first, current, last, total_nodes);
  }
}

// Bellman-Ford algorithm to find least distance path to any other node
int bellman_ford(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes, node_t sender){
  int changed = 0;
  int loop = 0;
  elem_t * tmp_vector = (elem_t *) calloc(total_nodes, sizeof(elem_t));

  for(node_t n = first; n <= last; n++){
    tmp_vector[n].cost = COST_INFINITY;
    tmp_vector[n].path = (node_t *) calloc(total_nodes, sizeof(node_t));
    for(node_t nn = first; nn <= last; nn++){
      tmp_vector[n].path[nn] = COST_INFINITY;
    }
    if(n == current){
      tmp_vector[n].cost = 0;
    }
  }

  cost_t tmp_cost;
  for(node_t col = first; col <= last; col++){ // col = destination
    for(node_t lin = first; lin <= last; lin++){ // lin = next hop
      loop = 0;
      if(lin != current){
        if(lin == col){
          tmp_cost = get_link_cost(lin);
        } else{
          tmp_cost = COST_ADD(get_link_cost(lin), actual_state->matrix[lin][col].cost);
        }

        for(node_t n = first; n <= last; n++){ // verificar path
          if(actual_state->matrix[lin][col].path[n] == current){
            loop = 1;
            break;
          }
        }

        if(loop == 0 && tmp_cost < tmp_vector[col].cost){
          tmp_vector[col].cost = tmp_cost;
          tmp_vector[col].path[0] = lin;

          for(node_t n = first+1; n <= last; n++){
            tmp_vector[col].path[n] = actual_state->matrix[lin][col].path[n];
          }
        }
      }
    }
  }

  for(node_t n = first; n <= last; n++){
    if(actual_state->matrix[current][n].cost != tmp_vector[n].cost){
      actual_state->matrix[current][n].cost = tmp_vector[n].cost;
      for(node_t nn = first; nn <= last; nn++){
        actual_state->matrix[current][n].path[nn] = tmp_vector[n].path[nn];
      }
      printf("New route from %d to %d through %d with cost %d\n", current, n, tmp_vector[n].path[0], tmp_vector[n].cost);
      set_route(n, tmp_vector[n].path[0], tmp_vector[n].cost);
      changed = 1;
    }
  }

  return changed;
}

// Warns direct neighbors of node if DV changed
void warn_neighbors(state_t * actual_state, node_t first, node_t current, node_t last, node_t total_nodes){
  message_t * msg = (message_t *) calloc(1, sizeof(message_t));
  msg->vector = (elem_t *) calloc(total_nodes, sizeof(message_t));

  for(node_t i = first; i < total_nodes; i++){
    msg->vector[i].cost = actual_state->matrix[current][i].cost;
    msg->vector[i].path = (node_t *) calloc(total_nodes, sizeof(node_t));
    for(node_t j = first; j < total_nodes; j++){
      msg->vector[i].path[j] = actual_state->matrix[current][i].path[j];
    }
  }

  for(node_t n = first; n <= last; n++){
    if(get_link_cost(n) != COST_INFINITY && n != current){
      send_message(n, msg, sizeof(msg));
      printf("Message sent from %d to %d advertising link change\n", current, n);
    }
  }
}
