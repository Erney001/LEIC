#ifndef games_h
#define games_h


#include <stdio.h>
#include "hash.h"
#include "lista.h"
#include "limits.h"
#include "teams.h"


void add_new_game(Hash_table_games HTG, Node_game_lst *Lst_jogos, Hash_table HT, int line);
void search_game(Hash_table_games HTG, int line);
void lst_games(Node_game *head, int line);
void remove_game(Hash_table_games HTG, Node_game_lst *Lst_jogos, Hash_table HT, int line);
void change_score(Hash_table_games HTG, Hash_table HT, int line);


#endif