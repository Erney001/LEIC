#ifndef teams_h
#define teams_h


#include <stdio.h>
#include <string.h>
#include "hash.h"
#include "limits.h"


void add_team(Hash_table HT, int line);
void search_team_name(Hash_table HT, int line);
void print_most_wins(Hash_table HT, int line);


int search_most_wins(Hash_table HT);
int create_teams_array(Hash_table HT, Node_team **head, int most_wins);
void merge_sort(Node_team **array_teams, int left, int right);
void merge(Node_team **array_teams, int left, int mid, int right);


#endif