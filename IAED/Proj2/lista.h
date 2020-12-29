#ifndef lista_h
#define lista_h


#include <stdio.h>
#include <stdlib.h>
#include <string.h>


typedef struct node_t{
    char *nome;
    int vitorias;
    struct node_t *next;
} Node_team;


typedef struct{
    char *nome;
    char *equipa1, *equipa2;
    int golos_equipa1, golos_equipa2;
} Jogo;


typedef struct node_g{
    Jogo *jogo;
    struct node_g *next;
    struct node_g *prev;
} Node_game;


typedef struct{
    Node_game *head;
    Node_game *last;
} Node_game_lst;


/* Node_g_ht contem Jogo *jogo, um ponteiro para um jogo na lista ligada com todos os jogos */
typedef struct node_g_ht{
    Node_game *node_game_ptr;
    struct node_g_ht *next;
    struct node_g_ht *prev;
} Node_g_ht;


Node_team *insert_begin(Node_team *head, char *nome);
Node_team *search_team(Node_team *head, char *nome);
Node_team *clear_node_team(Node_team *node);


Node_game *insert_end(Node_game_lst *Lst_jogos, char *nome_jogo, char *equipa1, char *equipa2,
    int golos_equipa1, int golos_equipa2);
void clear_node_game(Node_game *node);
Node_game *clear_lst_node_game(Node_game *head);


Node_game_lst *init_node_game_lst();
void clear_node_game_lst(Node_game_lst *Lst_jogos);


Node_g_ht *insert_begin_games(Node_g_ht *head, Node_game *node_game_ptr);
Node_g_ht *search_game_lst(Node_g_ht *head, char *nome);
void clear_node_g_ht(Node_g_ht *node);
Node_g_ht *clear_lst_node_g_ht(Node_g_ht *head);


#endif