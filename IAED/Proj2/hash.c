#include "hash.h"


/*  Funcao para iniciar a hash table de equipas: aloca memoria
    para cada uma das posicoes e inicia-as a NULL */
Hash_table init_HT(){
    Hash_table HT;
    int i;

    HT.heads = malloc(sizeof(Node_team) * MAX_HASH_TABLE);

    for(i=0;i<MAX_HASH_TABLE;i++){
        HT.heads[i] = NULL;
    }

    return HT;
}


/*  Funcao de hash: dado um nome devolve a posicao na hash table */
int hash(char *nome){
    int h = 0, a = 127;

    for(;*nome!='\0';nome++){
        h = (a*h + *nome) % MAX_HASH_TABLE;
    }

    return h;
}


/*  Para inserir uma equipa, ve-se a posicao em que deve ser inserida,
    que e uma lista ligada, e adiciona-se ai */
void HT_insert(Hash_table HT, char *nome){
    int i = hash(nome);
    HT.heads[i] = insert_begin(HT.heads[i], nome);
}


/*  Para procurar uma equipa, ve-se a posicao em que deve estar, e percorre-se a lista
    ligada nessa posicao ate encontrar a equipa ou ate chegar ao fim */
Node_team *search_HT(Hash_table HT, char *nome){
    int i = hash(nome);
    return search_team(HT.heads[i], nome);
}


/*  Funcao limpar toda a memoria alocada na hash table de equipas */
void clear_ht(Hash_table HT){
    int i;

    for(i=0;i<MAX_HASH_TABLE;i++){
        HT.heads[i] = clear_node_team(HT.heads[i]);
        free(HT.heads[i]);
    }

    free(HT.heads);
}


/*************************************************************************************************/


/*  Funcao para iniciar a hash table de jogos: aloca memoria
    para cada uma das posicoes e inicia-as a NULL */
Hash_table_games init_HT_games(){
    Hash_table_games HTG;
    int i;

    HTG.heads = malloc(sizeof(Node_g_ht) * MAX_HASH_TABLE);

    for(i=0;i<MAX_HASH_TABLE;i++){
        HTG.heads[i] = NULL;
    }

    return HTG;
}


/*  Para inserir um jogo, ve-se a posicao em que deve ser inserida,
    que e uma lista ligada, e adiciona-se ai */
void HT_games_insert(Hash_table_games HTG, Node_game *head, char *nome_jogo){
    int i = hash(nome_jogo);

    HTG.heads[i] = insert_begin_games(HTG.heads[i], head);
}


/*  Para procurar um jogo, ve-se a posicao em que deve estar, e percorre-se a lista
    ligada nessa posicao ate encontrar a equipa ou ate chegar ao fim */
Node_g_ht *search_HTG(Hash_table_games HTG, char *nome){
    int i = hash(nome);
    return search_game_lst(HTG.heads[i], nome);
}


/*  Funcao limpar toda a memoria alocada na hash table de jogos */
void clear_ht_games(Hash_table_games HTG){
    int i;

    for(i=0;i<MAX_HASH_TABLE;i++){
        HTG.heads[i] = clear_lst_node_g_ht(HTG.heads[i]);
        free(HTG.heads[i]);
    }

    free(HTG.heads);
}