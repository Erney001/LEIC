/* Projeto 2 ASA by:
   Afonso Ferreira, 96832
   Mario Antunes, 96892
 */


#include <iostream>
#include <vector>
#include <queue>
#include <cstring>
#include <limits.h>


using namespace std;


// X = vertice 0 (source) que repesenta processador X
// Y = vertice N+1 (ultimo/destino) que repesenta processador Y
int X = 0, Y;


class Graph{
    vector<vector<int>> cap;  // representacao matricial do grafo que contem as capacidades dos arcos
    vector<vector<int>> rCap; // rede residual do grafo

    public:
    Graph();
    int BFS(int parent[]);
    void EdmondsKarp();
};


Graph::Graph(){
    int N, K, trash = scanf("%d %d", &N, &K);
    Y = N+1;

    if(!trash){
        exit(EXIT_FAILURE);
    }

    // redimensionar a matriz de capacidades apos se saber o N
    cap.resize(N+2);
    for(int i = 0; i <= Y; i++){
        vector<int> vec(N+2, -1);
        cap[i] = vec;
    }

    // leitura de Xi Yi que representa os custos de execucao em X e Y respetivamente
    // associar a cada arco de X para Pi o custo Xi e de cada arco de Pi para Y o custo Yi
    for(int u=0; u < N; u++){
        int tmp1, tmp2;
        trash = scanf("%d %d", &tmp1, &tmp2);
        cap[0][u+1]= tmp1;
        cap[u+1][N+1] = tmp2;
    }

    // leitura de cada custo de comunicacao e preenchimento da matriz de forma simetrica
    for(int u=0; u < K; u++){
        int i, j, Cij;
        trash = scanf("%d %d %d", &i, &j, &Cij);
        cap[i][j] = Cij;
        cap[j][i] = Cij;
    }  
    
    // a rede residual e igual ao proprio grafo no inicio
    rCap = cap;
}


int Graph::BFS(int parent[]){
    bool visited[Y+1];
    memset(visited, false, sizeof(visited));
  
    // inicio da BFS no vertice 0 que representa o processador X
    queue <int> q;
    q.push(X);
    visited[X] = true;
    parent[X] = -1;

    // continuar a BFS ate nao haver mais vertices na fila de exploraçao
    while (!q.empty()){
        int u = q.front();
        q.pop();
        
        // quando se explora u, percorrer todos os vertices v e adiciona-los a queue se houverem
        // arcos de u para v tal que v ainda nao tenha sido visitado e a sua capacidade residual e maior que 0
        for (int v = 0; v <= Y; v++){
            if (visited[v]==false && rCap[u][v] > 0){
                q.push(v);
                parent[v] = u;
                visited[v] = true;
                
                // quando encontramos o Y durante a BFS, paramos imediatamente
                if(v == Y){
                    return true;  
                }
            }
        }
    }

    // devolver um booleano para saber se o caminho final chegou ao vertice final Y
    return false;
}


void Graph::EdmondsKarp(){
    int u, v;
    int max_flow = 0;
  
    int parent[Y+1];  
    
    // enquanto houver um caminho ate Y conseguido atraves da BFS, vai-se atualizar
    // a rede residual e o fluxo total ate não haver mais caminhos disponiveis
    while(BFS(parent)){
        int path_flow = INT_MAX;

        for (v=Y; v != X; v=parent[v]){
            u = parent[v];
            path_flow = min(path_flow, rCap[u][v]);
        }
  
        for (v=Y; v != X; v=parent[v]){
            u = parent[v];
            rCap[u][v] -= path_flow;
            rCap[v][u] += path_flow;
        }

        max_flow += path_flow;
    }

    // depois temos o fluxo maximo, igual ao corte minimo, que contem os arcos de menor custo,
    // aqueles que estrangulam o fluxo no grafo, printamos o resultado, que e a soluçao pretendida
    printf("%d\n", max_flow);
}  


int main(){
    Graph g;
    g.EdmondsKarp();
}
