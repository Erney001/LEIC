#include "lista.h"


/*  Funcao para inserir uma equipa numa lista ligada de equipas */
Node_team *insert_begin(Node_team *head, char *nome){
    Node_team *new = malloc(sizeof(Node_team));
    
    new->nome = malloc(sizeof(char) * (strlen(nome)+1));
    strcpy(new->nome, nome);
    new->vitorias = 0;
    new->next = head;

    return new;
}


/*  Para procurar uma equipa numa lista ligada, vamos percorrendo todos
    os elementos ate encontrar a equipa ou ate chegar ao fim */
Node_team *search_team(Node_team *head, char *nome){
    for(; head != NULL; head = head->next){
        if(strcmp(head->nome, nome) == 0){
            return head;
        }
    }

    return NULL;
}


/*  Limpa a memoria ocupada por um ponteiro para Node_team */
void free_node_team(Node_team *node){
    free(node->nome);
    free(node);
}


/*  Funcao para limpar toda a lista ligada de equipas */
Node_team *clear_node_team(Node_team *head){
    Node_team *aux;

    while(head){
        aux = head->next;
        free_node_team(head);
        head = aux;
    }

    return NULL;
}


/*************************************************************************************************/


/*  Funcao para adicionar um jogo no fim de uma lista duplamente ligada */
Node_game *insert_end(Node_game_lst *Lst_jogos, char *nome_jogo, char *equipa1, char *equipa2,
    int golos_equipa1, int golos_equipa2){
    Node_game *new = malloc(sizeof(Node_game));

    new->jogo = malloc(sizeof(Jogo));
    new->jogo->nome = malloc(sizeof(char) * (strlen(nome_jogo)+1));
    strcpy(new->jogo->nome, nome_jogo);
    new->jogo->equipa1 = malloc(sizeof(char) * (strlen(equipa1)+1));
    strcpy(new->jogo->equipa1, equipa1);
    new->jogo->equipa2 = malloc(sizeof(char) * (strlen(equipa2)+1));
    strcpy(new->jogo->equipa2, equipa2);
    new->jogo->golos_equipa1 = golos_equipa1;
    new->jogo->golos_equipa2 = golos_equipa2;
    new->next = NULL;

    if(Lst_jogos->head == NULL){
        new->prev = NULL;
        Lst_jogos->head = new;
    } else{
        Lst_jogos->last->next = new;
        new->prev = Lst_jogos->last;
    }
    Lst_jogos->last = new;

    return Lst_jogos->head;
}


/*  Funcao para limpar toda a memoria associada a um ponteiro para Node_game */
void clear_node_game(Node_game *node){
    free(node->jogo->nome);
    free(node->jogo->equipa1);
    free(node->jogo->equipa2);
    free(node->jogo);
    free(node);
}


/*  Funcao para limpar uma lista ligada de jogos */
Node_game *clear_lst_node_game(Node_game *head){
    Node_game *aux;

    while(head){
        aux = head->next;
        clear_node_game(head);
        head = aux;
    }

    return NULL;
}


/*************************************************************************************************/


/*  Funcao para iniciar a estrutura que contem a lista de todos os jogos introduzidos por ordem */
Node_game_lst *init_node_game_lst(){
    Node_game_lst *Lst_jogos = malloc(sizeof(Node_game_lst));

    Lst_jogos->head = NULL;
    Lst_jogos->last = NULL;

    return Lst_jogos;
}


/*  Funcao para limpar a memoria associada a Lst_jogos */
void clear_node_game_lst(Node_game_lst *Lst_jogos){
    clear_lst_node_game(Lst_jogos->head);
    free(Lst_jogos);
}


/*************************************************************************************************/


/*  Funcao para inserir um ponteiro para um jogo numa lista ligada de Node_g_ht, estruturas
    que contem ponteiros para jogos na lista ligada com todos os jogos */
Node_g_ht *insert_begin_games(Node_g_ht *head, Node_game *node_game_ptr){
    Node_g_ht *new = malloc(sizeof(Node_g_ht));

    new->node_game_ptr = node_game_ptr;
    new->next = head;
    new->prev = NULL;

    if(head != NULL){
        head->prev = new;
    }

    return new;
}


/*  Para encontrar um jogo, percorre-se a lista ligada de estruturas que contem ponteiros
    para jogos ate encontrar o jogo com o nome que queriamos ou ate chegar ao fim da lista */
Node_g_ht *search_game_lst(Node_g_ht *head, char *nome){
    for(; head != NULL; head = head->next){
        if(strcmp(head->node_game_ptr->jogo->nome, nome) == 0){
            return head;
        }
    }

    return NULL;
}


/*  Limpa um Node_g_ht */
void clear_node_g_ht(Node_g_ht *node){
    free(node);
}


/*  Funcao para limpar uma lista de estruturas Node_g_ht */
Node_g_ht *clear_lst_node_g_ht(Node_g_ht *head){
    Node_g_ht *aux;

    while(head){
        aux = head->next;
        clear_node_g_ht(head);
        head = aux;
    }

    return NULL;
}