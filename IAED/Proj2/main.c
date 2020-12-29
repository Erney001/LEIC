/*  Projeto 2 IAED 2019/2020
    By: Afonso Ferreira 96832 */


#include <stdio.h>
#include "hash.h"
#include "lista.h"
#include "limits.h"
#include "teams.h"
#include "games.h"


/*  Funcao principal do programa que inicia as estruturas necessarias, le os comandos
    e chama as funcoes correspondentes e limpa toda a memoria utilizada no final */
int main(){
    char cmd;
    int line = 1;

    Hash_table HT_teams = init_HT();
    Node_game_lst *Lst_jogos = init_node_game_lst();
    Hash_table_games HT_games = init_HT_games();

    while((cmd = getchar()) != 'x'){
        switch(cmd){
            case 'A':
                add_team(HT_teams, line);
                break;
            case 'P':
                search_team_name(HT_teams, line);
                break;
            case 'a':
                add_new_game(HT_games, Lst_jogos, HT_teams, line);
                break;
            case 'p':
                search_game(HT_games, line);
                break;
            case 'l':
                lst_games(Lst_jogos->head, line);
                break;
            case 'r':
                remove_game(HT_games, Lst_jogos, HT_teams, line);
                break;
            case 's':
                change_score(HT_games, HT_teams, line);
                break;
            case 'g':
                print_most_wins(HT_teams, line);
                break;
            case '\n':
                line++;
                break;
            default:
                break;
        }
    }

    clear_ht(HT_teams);
    clear_ht_games(HT_games);
    clear_node_game_lst(Lst_jogos);

    return 0;
}