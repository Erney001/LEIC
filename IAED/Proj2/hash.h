#ifndef hash_h
#define hash_h


#include <stdio.h>
#include <stdlib.h>
#include "lista.h"
#include "limits.h"


typedef struct{
    Node_team **heads;
} Hash_table;


typedef struct{
    Node_g_ht **heads;
} Hash_table_games;


Hash_table init_HT();
int hash(char *nome);
void HT_insert(Hash_table HT, char *nome);
Node_team *search_HT(Hash_table HT, char *nome);
void clear_ht(Hash_table HT);


Hash_table_games init_HT_games();
void HT_games_insert(Hash_table_games HTG, Node_game *head, char *nome_jogo);
Node_g_ht *search_HTG(Hash_table_games HTG, char *nome);
void clear_ht_games(Hash_table_games HTG);


#endif