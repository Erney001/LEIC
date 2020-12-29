/* ======================================
Sistemas Operativos 17/18
Aula teórica 1: Exemplo simples de programação 
multi-tarefa com pthreads construído durante a
aula

Este ficheiro contém a solução multi-tarefa
final.

Para compilar:

gcc -c main-par.c
gcc -pthread -o main-par main-par.o


=============================== */


#include <unistd.h>
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

#define N 4

int computacaoDemorada(int x) {
  sleep(10);
  return x*100+1;
}

void *fnThread(void *arg) {
  int *x, *r;
  x = (int*)arg;
  r = (int*)malloc(sizeof(int));

  *r = computacaoDemorada(*x);
  return r;
}

//Variavel global
int args[N];

int main() {

  int i;
  int *result;
  
  pthread_t tid[N];

  for (i=0; i<N; i++) {
    args[i] = i;
    if (pthread_create (&tid[i], NULL, fnThread, &args[i]) != 0){
      printf("Erro ao criar tarefa.\n");
      return 1;
    }
    printf("Lancou uma tarefa\n");
  }

  for (i=0; i<N; i++) {
    if (pthread_join (tid[i], (void**)&result) != 0) {
      printf("Erro ao esperar por tarefa.\n");
      return 2;
    }
    printf("Tarefa retornou com resultado = %d\n", *result);
  }

  return 0;
}
