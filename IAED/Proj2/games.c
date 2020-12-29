#include "games.h"


/*  Funcao para adicionar um jogo quer a lista duplamente ligada de jogos quer
    a hash table de jogos e vai atualizando o numero de vitorias conforme os dados
    do jogo adicionado */
void add_new_game(Hash_table_games HTG, Node_game_lst *Lst_jogos, Hash_table HT, int line){
    char nome_jogo[MAX_NAME_LEN], equipa1[MAX_NAME_LEN], equipa2[MAX_NAME_LEN];
    int golos_equipa1, golos_equipa2;
    Node_team *aux;

    scanf(" %[^:\n]:%[^:\n]:%[^:\n]:%d:%d", nome_jogo, equipa1, equipa2, &golos_equipa1,
        &golos_equipa2);

    if(!search_HTG(HTG, nome_jogo)){
        if(search_HT(HT, equipa1) != NULL && search_HT(HT, equipa2) != NULL){
            /* Adiciona a lista duplamente ligada de jogos */
            Lst_jogos->head = insert_end(Lst_jogos, nome_jogo, equipa1, equipa2,
                golos_equipa1, golos_equipa2);
            /* E a hash table de jogos */
            HT_games_insert(HTG, Lst_jogos->last, nome_jogo);
            /* Se a equipa 1 ganhou, fica com mais uma vitoria */
            /* Se foi a equipa 2 que ganhou, fica com mais uma vitoria */
            if(golos_equipa1 > golos_equipa2){
                aux = search_HT(HT, equipa1);
                aux->vitorias++;
            } else if(golos_equipa2 > golos_equipa1){
                aux = search_HT(HT, equipa2);
                aux->vitorias++;
            }
        } else{
            printf("%d Equipa inexistente.\n", line);
        }
    } else{
        printf("%d Jogo existente.\n", line);
    }
}


/*  Procura um jogo dado o seu nome
    Se nao existir, da uma mensagem de erro, cado contrario printa os dados do jogo */
void search_game(Hash_table_games HTG, int line){
    char nome_jogo[MAX_NAME_LEN];
    Node_g_ht *p;

    scanf(" %[^:\n]", nome_jogo);

    p = search_HTG(HTG, nome_jogo);

    if(!p){
        printf("%d Jogo inexistente.\n", line);
    } else{
        printf("%d %s %s %s %d %d\n", line, p->node_game_ptr->jogo->nome, p->node_game_ptr->jogo->equipa1, p->node_game_ptr->jogo->equipa2,
        p->node_game_ptr->jogo->golos_equipa1, p->node_game_ptr->jogo->golos_equipa2);
    }
}


/*  Funcao que percorre e printa todos os jogos na lista ligada de jogos pela
    ordem de introducao */
void lst_games(Node_game *head, int line){
    if(head != NULL){
        for(; head != NULL; head = head->next){
        printf("%d %s %s %s %d %d\n", line, head->jogo->nome, head->jogo->equipa1,
            head->jogo->equipa2, head->jogo->golos_equipa1, head->jogo->golos_equipa2);
        }
    }
}


/*  Funcao para remover um jogo dado um nome
    Se ele existir, analisa se alguma das equipas tinha vencido, para agora tirar essa vitoria
    E remove o elemento da lista duplamente ligada de jogos e da hash table de jogos */
void remove_game(Hash_table_games HTG, Node_game_lst *Lst_jogos, Hash_table HT, int line){
    char nome_jogo[MAX_NAME_LEN];
    int i;
    Node_g_ht *p;
    Node_team *aux;

    scanf(" %[^:\n]", nome_jogo);

    p = search_HTG(HTG, nome_jogo);

    if(!p){
        printf("%d Jogo inexistente.\n", line);
    } else{
        if(p->node_game_ptr->jogo->golos_equipa1 > p->node_game_ptr->jogo->golos_equipa2){
            aux = search_HT(HT, p->node_game_ptr->jogo->equipa1);
            aux->vitorias--;
        } else if(p->node_game_ptr->jogo->golos_equipa2 > p->node_game_ptr->jogo->golos_equipa1){
            aux = search_HT(HT, p->node_game_ptr->jogo->equipa2);
            aux->vitorias--;
        }
        if(p->node_game_ptr->prev != NULL && p->node_game_ptr->next != NULL){
            p->node_game_ptr->prev->next = p->node_game_ptr->next;
            p->node_game_ptr->next->prev = p->node_game_ptr->prev;
        } else if(p->node_game_ptr->prev != NULL && p->node_game_ptr->next == NULL){
            p->node_game_ptr->prev->next = NULL;
            Lst_jogos->last = p->node_game_ptr->prev;
        } else if(p->node_game_ptr->prev == NULL && p->node_game_ptr->next != NULL){
            p->node_game_ptr->next->prev = NULL;
            Lst_jogos->head = p->node_game_ptr->next;
        } else{
            Lst_jogos->head = NULL;
        }
        clear_node_game(p->node_game_ptr);
        p->node_game_ptr = NULL;
        i = hash(nome_jogo);
        if(p->prev != NULL && p->next != NULL){
            p->prev->next = p->next;
            p->next->prev = p->prev;
        } else if(p->prev != NULL && p->next == NULL){
            p->prev->next = NULL;
        } else if(p->prev == NULL && p->next != NULL){
            p->next->prev = NULL;
            if(HTG.heads[i] == p){
                HTG.heads[i] = p->next;
            }
        } else{
            HTG.heads[i] = NULL;
        }
        free(p);
        p = NULL;
    }
}


/*  Funcao para mudar o score de um jogo
    Se ele existir, ao mudar o score verifica-se tambem se alguma das equipas tem mais
    uma vitoria ou se deixou de ter uma vitoria */
void change_score(Hash_table_games HTG, Hash_table HT, int line){
    char nome_jogo[MAX_NAME_LEN];
    int new_score1, new_score2;
    Node_g_ht *p;
    Node_team *aux;

    scanf(" %[^:\n]:%d:%d", nome_jogo, &new_score1, &new_score2);

    p = search_HTG(HTG, nome_jogo);

    if(!p){
        printf("%d Jogo inexistente.\n", line);
    } else{
        if(p->node_game_ptr->jogo->golos_equipa1 > p->node_game_ptr->jogo->golos_equipa2){
            if(new_score1 <= new_score2){
                aux = search_HT(HT, p->node_game_ptr->jogo->equipa1);
                aux->vitorias--;
                if(new_score2 > new_score1){
                aux = search_HT(HT, p->node_game_ptr->jogo->equipa2);
                aux->vitorias++;
                }
            }
        } else if(p->node_game_ptr->jogo->golos_equipa2 > p->node_game_ptr->jogo->golos_equipa1){
            if(new_score2 <= new_score1){
                aux = search_HT(HT, p->node_game_ptr->jogo->equipa2);
                aux->vitorias--;
                if(new_score1 > new_score2){
                aux = search_HT(HT, p->node_game_ptr->jogo->equipa1);
                aux->vitorias++;
                }
            }
        } else{
            if(new_score1 > new_score2){
            aux = search_HT(HT, p->node_game_ptr->jogo->equipa1);
            aux->vitorias++;
            } else if(new_score2 > new_score1){
                aux = search_HT(HT, p->node_game_ptr->jogo->equipa2);
                aux->vitorias++;
            }
        }
        p->node_game_ptr->jogo->golos_equipa1 = new_score1;
        p->node_game_ptr->jogo->golos_equipa2 = new_score2;
    }
}