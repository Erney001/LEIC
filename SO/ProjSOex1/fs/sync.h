#ifndef SYNC
#define SYNC

#include <pthread.h>

#define MUTEX 0
#define RWLOCK 1
#define WRITE 2
#define READ 3

int syncStrategy;
pthread_rwlock_t rwl;
pthread_mutex_t mutex, mutexCommands;

void mutex_lock();
void mutex_unlock();

void rw_readlock();
void rw_unlock();
void rw_writelock();

void unlock(int syncstrat);
void lock(int syncstrat, int mode);

#endif