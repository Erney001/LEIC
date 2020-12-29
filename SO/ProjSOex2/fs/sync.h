#ifndef SYNC
#define SYNC

#include <pthread.h>

#define INODE_TABLE_INIT_SIZE 50
#define MUTEX 0
#define RWLOCK 1
#define WRITE 2
#define READ 3

#define DELETED -2
#define PATH1 0
#define PATH2 1


int syncStrategy;
pthread_rwlock_t rwl;
pthread_mutex_t mutexCommands, mutex;
pthread_cond_t insertCond, removeCond;

void mutex_lock();
void mutex_unlock();
void rwl_readlock();
void rwl_unlock();
void rwl_writelock();
void unlock(int syncstrat, pthread_rwlock_t rwlock, pthread_mutex_t mutex);
void lock(int syncstrat, int mode, pthread_rwlock_t rwlock, pthread_mutex_t mutex);


void lockNode(int mode, int inumber);
void unlockNode(int inumber);
void unlockThreads(int * inumber_table, int number_ofDirs);
void unlock_move_resources(char **paths, char * pathCopy, char * dest_child);

int get_numberOfDirs(char * path);

int addto_table (int ** inumber_table, int current_inumber, int callingFunc, int * number_ofDirs);

void lock_paths(int ** inumber_table, int * number_ofDirs, int * path1_inumber,
				int * path2_inumber, char ** paths);

#endif