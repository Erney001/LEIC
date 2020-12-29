#include <unistd.h>
#include <stdio.h>
#include <pthread.h>
#include <stdlib.h>
#include <assert.h>
#include <time.h>

#define N 4


typedef struct {
  int saldo;
  int numMovimentos;
  /* outras variáveis,ex. nome do titular, etc. */
} conta_t;

//Variaveis globais
conta_t c;

pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

pthread_rwlock_t rw_lock;


int depositar_dinheiro(conta_t* conta, int valor) {
  if (valor < 0)
    return -1;

  conta->saldo += valor;
  conta->numMovimentos ++;
  return valor;
}

int levantar_dinheiro(conta_t* conta, int valor) {
  if (valor < 0)
    return -1;

  if (conta->saldo >= valor) {
    conta->saldo -= valor;
    conta->numMovimentos ++;
  }
  else
    valor = -1;

  return valor;
}

void * consultar_conta(void * conta) {
  int s, n;

  conta_t *contaa = ((conta_t *) conta);

  pthread_rwlock_rdlock(&rw_lock);
  //pthread_mutex_lock(&mutex);
  s = contaa->saldo;
  n = contaa->numMovimentos;
  
  printf("Consulta: saldo=%d, #movimentos=%d\n", s, n);
  sleep(0.1);
  pthread_rwlock_unlock(&rw_lock);
  //pthread_mutex_unlock(&mutex);
  return NULL;
}


void *fnAlice(void *arg) {
  int m = *((int*)arg);
  int total = 0;
  int r;
  
  for (int i = 0; i<m; i++) {
    // seccao critica
    pthread_rwlock_wrlock(&rw_lock);
    //pthread_mutex_lock(&mutex);
    r = depositar_dinheiro(&c, 1);
    sleep(0.1);
    pthread_rwlock_unlock(&rw_lock);
    //pthread_mutex_unlock(&mutex);
    if (r != -1)
      total += r;
  }

  printf("Alice depositou no total: %d\n", total);
  return NULL;
}


void *fnBob(void *arg) {
  int m = *((int*)arg);
  int total = 0;
  int r;

  for (int i = 0; i<m; i++) {
    // seccao critica
    pthread_rwlock_wrlock(&rw_lock);
    //pthread_mutex_lock(&mutex);
    r = levantar_dinheiro(&c, 1);
    sleep(0.1);
    pthread_rwlock_unlock(&rw_lock);
    //pthread_mutex_unlock(&mutex);
    if (r != -1)
      total += r;
  }

  printf("Bob gastou no total: %d\n", total);
  return NULL;
}

void print_execution_time(struct timespec begin, struct timespec end){
    
    clock_gettime(CLOCK_REALTIME, &end);

    long seconds = end.tv_sec - begin.tv_sec;
    long nanoseconds = end.tv_nsec - begin.tv_nsec;
    double time_spent = seconds + nanoseconds*1e-9;

    printf("TecnicoFS completed in [%.4lf] seconds.\n", time_spent);
}


int main(int argc, char** argv) {
  pthread_rwlock_init(&rw_lock, NULL);

  struct timespec begin, end;
  clock_gettime(CLOCK_REALTIME, &begin);

  pthread_t tid[20];
  
  int m;

  if (argc > 1)
    m = atoi(argv[1]);
  else
    m = 0;
  
  c.saldo = 0;
  c.numMovimentos = 0;
  
  if (pthread_create (&tid[0], NULL, fnAlice, (void*)&m) != 0)
    exit(EXIT_FAILURE);
  if (pthread_create (&tid[1], NULL, fnBob, (void*)&m) != 0)
    exit(EXIT_FAILURE);

  for(int i = 2; i<20; i++){
    pthread_create (&tid[i], NULL, consultar_conta, (void*)&c);
  }
  
  pthread_join(tid[0], NULL);
  pthread_join(tid[1], NULL);

  for(int j = 2; j<20; j++){
    pthread_join(tid[j], NULL);
  }

  printf("História chegou ao fim\n");
  consultar_conta(&c);

  print_execution_time(begin, end);

  exit(EXIT_SUCCESS);
}

