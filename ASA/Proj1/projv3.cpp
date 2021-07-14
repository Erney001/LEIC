/* Projeto 1 ASA by:
   Afonso Ferreira, 96832
   Mario Antunes, 96892
 */


#include <iostream>
#include <list>
#include <vector>


using namespace std;


/* Classe Grafo que guarda o numero de vertices, tem uma lista de adjacencias que representa
   o grafo e ainda um vetor para registar o indegree de cada vertice
 */
class Graph{
    int num_vertices;
    list<int> *list_adj;
    vector<int> indegree;

    public:
    Graph();
    void printGraph();
    int topologicalSort(vector<int> &topOrder);
    void longestPath();
};


/* Inicializacao do grafo
   Com registo do numero de vertices, arcos e indegree de cada vertice
 */
Graph::Graph(){
    int N, M, Trash = scanf("%d %d", &N, &M);

    if(!Trash){
        exit(EXIT_FAILURE);
    }

    this->num_vertices = N;
    list_adj = new list<int>[N];
    vector<int> tmp(num_vertices, 0);
    indegree = tmp;

    /* Por cada arco (U, V) lido, adicionar a lista de adjacencias
       e aumentar o indegree do vertice V ao qual se chegou
     */
    for(int i = 0; i<M; i++){
        int u, v;
        Trash = scanf("%d %d", &u, &v);
        list_adj[u-1].push_front(v-1);
        indegree[v-1]++;
    }
}


/* Funcao para mostrar o grafo na forma: A -> B, C, D,
   Que simboliza os arcos que saem de A e chegam ate B, C e D
 */
void Graph::printGraph(){
    printf("Graph:\n");
    for (int i = 0; i<num_vertices ; i++){
        printf("%d %s " , i+1, "->");
        for(list<int>::iterator it = list_adj[i].begin(); it != list_adj[i].end(); ++it ){
            printf("%d,", int(*it) + 1);
        }
        printf("\n");
    }
}


/* Funcao que descobre uma ordem topologica nos vertices do grafo dado
   E devolve o k, numero de dominos a derrubar para derrubar todo o conjunto, ou seja,
   numero de vertices no grafico com indegree = 0
 */
int Graph::topologicalSort(vector<int> &topOrder){
    vector<int> q;
    int k = 0;

    /* Adicionar os vertices que tem indegree = 0 ao vetor q que representa os vertices a visitar
       e aumenta o k ao mesmo tempo, pois esses vertices serao os que terao de ser derrubados
       para que todo o conjunto de dominos caia
     */
    for(int i=0; i<num_vertices; i++){
        if(!indegree[i]){
            q.push_back(i);
            k++;
        }
    }

    /* Enquanto houver vertices para visitar, retiramos um do fim do vetor e adicionamos-o a
       ord. topologica final, sendo que percorremos todos os arcos que saem desse vertice e
       adicionamos ao vetor q os vertices aos quais se chegou a partir desses arcos e que ficaram
       com indegree = 0 depois de eliminar o arco que descobriu o vertice
     */
    while(!q.empty()){
        int n = q.back();
        q.pop_back();

        topOrder.push_back(n);

        for(list<int>::iterator it = list_adj[n].begin(); it != list_adj[n].end(); ++it ){
            indegree[int(*it)]--;

            if(!indegree[int(*it)]){
                q.push_back(int(*it));
            }
        }
    }

    return k;
}


/* Funcao que descobre o maior caminho num DAG, correspondente ao l
 */
void Graph::longestPath(){
    /* Inicialmente, o vetor correspondente as distanxias maximas esta todo preenchido com uns
       O l tambem e iniciado a 1, ou seja, o maximo numero de vertices a cair com
       um toque e apenas o proprio vertice
       De seguida, descobre uma ord. topologica para os vertices do grafo
     */
    vector<int> topOrder;
    vector<int> distances(num_vertices, 1);
    
    int l = 1;
    int k = topologicalSort(topOrder);

    /* Percorre todos os vertices segundo a ord. topologica e em cada um vai a cada vertice
       atingivel a partir desse e atualiza a sua distancia se for maior que a registada anteriormente
       Vai ainda comparando cada nova distancia com a distancia maxima registada essa altura,
       atualizando o valor da distancias maxima (l) se a nova distancia for maior que ela
     */
    for(int i: topOrder){

        for(list<int>::iterator it = list_adj[i].begin(); it != list_adj[i].end(); ++it){
            int tmp = int(*it);

            if(distances[tmp] < distances[i]+1){
                distances[tmp] = distances[i]+1;

                if(distances[tmp] > l){
                    l = distances[tmp];
                }
            }
        }
    }

    // printa os resultados obtidos para o k e para o l
    printf("%d %d\n", k, l);
}


/* Para cumprir o objetivo do projeto, temos apenas que chamar a funcao
   longesthPath() que ira printar o k e o l
 */
int main(){
    Graph g;
    g.longestPath();
}
