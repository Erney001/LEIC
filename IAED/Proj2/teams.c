#include "teams.h"


/*  Adiciona uma nova equipa se ainda nao existir */
void add_team(Hash_table HT, int line){
    char nome[MAX_NAME_LEN];

    scanf("  %[^:\n]", nome);

    if(!search_HT(HT, nome)){
        HT_insert(HT, nome);
    } else{
        printf("%d Equipa existente.\n", line);
    }
}


/*  Procura uma equipa pelo seu nome */
void search_team_name(Hash_table HT, int line){
    char nome[MAX_NAME_LEN];
    Node_team *p;

    scanf("  %[^:\n]", nome);

    p = search_HT(HT, nome);

    if(!p){
        printf("%d Equipa inexistente.\n", line);
    } else{
        printf("%d %s %d\n", line, p->nome, p->vitorias);
    }
}


/*  Funcao para printar as equipas com o maior numero de vitorias
    Para isso calculamos o maior numero de vitorias, criamos um vetor auxiliar com todas
    as equipas com esse numero de vitorias, fazemos merge sort nesse vetor e, por ultimo,
    printamos o vetor por ordem */
void print_most_wins(Hash_table HT, int line){
    int most_wins = search_most_wins(HT), i, last;
    Node_team *array_teams[MAX_BEST_TEAMS];

    for(i=0;i<MAX_BEST_TEAMS;i++){
        array_teams[i] = NULL;
    }

    last = create_teams_array(HT, array_teams, most_wins);

    if(array_teams[0] != NULL){
        if(last > 0){
            merge_sort(array_teams, 0, last-1);
        }

        printf("%d Melhores %d\n", line, most_wins);

        for(i=0; i<last; i++){
            printf("%d * %s\n", line, array_teams[i]->nome);
        }
    }
}


/*  Calcula o maior numero de vitorias percorrendo todo o conjunto de
    equipas presentes na hash table */
int search_most_wins(Hash_table HT){
    int most_wins = 0, i;
    Node_team *aux;

    for(i=0; i<MAX_HASH_TABLE; i++){
        aux = HT.heads[i];

        for(; aux != NULL; aux = aux->next){
            if(aux->vitorias > most_wins){
                most_wins = aux->vitorias;
            }
        }
    }

    return most_wins;
}


/*  Percorre a hash table com as equipas todas e cria um vetor com todas
    as equipas que tem o maior numero de vitorias */
int create_teams_array(Hash_table HT, Node_team **head, int most_wins){
    int i, u = 0;
    Node_team *aux;

    for(i=0; i<MAX_HASH_TABLE; i++){
        aux = HT.heads[i];

        for(; aux != NULL; aux = aux->next){
            if(aux->vitorias == most_wins){
                head[u++] = aux;
            }
        }
    }
    
    return u;
}


/*  Funcao para fazer merge sort do vetor de ponteiros para equipas */
void merge_sort(Node_team **array_teams, int left, int right){
    int mid = (left + right) / 2;

    if(right <= left){
        return;
    } else{
        merge_sort(array_teams, left, mid);
        merge_sort(array_teams, mid+1, right);
        merge(array_teams, left, mid, right);
    }
}


/*  Funcao para fazer merge dos vetores de ponteiros para equipas
    Comecamos por criar um vetor auxiliar igual ao inicial e a partir dai
    vamos comparando os elementos e pondo-os no lugar certo no vetor inicial */
void merge(Node_team **array_teams, int left, int mid, int right){
    int i, j, k;
    Node_team *array_teams_aux[MAX_BEST_TEAMS];

    for(i=0;i<MAX_BEST_TEAMS;i++){
        array_teams_aux[i] = NULL;
    }

    for(i=mid+1;i>left;i--){
        array_teams_aux[i-1] = array_teams[i-1];
    }
    for(j=mid;j<right;j++){
        array_teams_aux[right+mid-j] = array_teams[j+1];
    }

    for(k=left;k<=right;k++){
        /* strcmp(a, b) devolve < 0, se a for alfabeticamente menor que b */
        if(strcmp(array_teams_aux[j]->nome, array_teams_aux[i]->nome) < 0){
            array_teams[k] = array_teams_aux[j--];
        } else{
            array_teams[k] = array_teams_aux[i++];
        }
    }
}