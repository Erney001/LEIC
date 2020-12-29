/* ======================================
Sistemas Operativos 17/18
Aula teórica 1: Exemplo simples de programação 
multi-tarefa com pthreads construído durante a
aula

Este ficheiro contém a solução base, com 
tarefa única, logo sequencial.

Para compilar:

gcc -c main-inicial.c
gcc -o main-inicial main-inicial.o

No final do ficheiro encontram-se excertos
de código que devem ser inseridos no código
original e devidamente completados para se
construir a versão multi-tarefa.

=============================== */

#include <unistd.h>
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>

#define N 4

int computacaoDemorada(int x) {
  sleep(5);
  return x*100+1;
}



int main() {

  int i;
  int arg;
  int result;
  
  for (i=0; i<N; i++) {
    arg = i;
    result = computacaoDemorada(arg);
    printf("Resultado = %d\n", result);
  }

  return 0;
  
}


/*
  for (i=0; i<N; i++) {
    if (pthread_create ( ... ) != 0){
      printf("Erro ao criar tarefa.\n");
      return 1;
    }
    printf("Lancou uma tarefa\n");
  }
*/



/*
void *fnThread(void *arg) {
  int *x, *r;
  x = (int*)arg;
  r = (int*)malloc(sizeof(int));

  *r = computacaoDemorada(*x);
  return r;
}
*/


/*
  for (i=0; i<N; i++) {
    if (pthread_join ( ... ) != 0) {
      printf("Erro ao esperar por tarefa.\n");
      return 2;
    }
    printf("Tarefa retornou com resultado = %d\n", ...);
  }
*/
