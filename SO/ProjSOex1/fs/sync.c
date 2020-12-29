#include "sync.h"
#include <stdio.h>
#include <string.h>


/* Function to lock mutex and verify error. */
void mutex_lock() {
    if (pthread_mutex_lock (&mutex) != 0)
        perror("Error locking mutex.\n");
}


/* Function to unlock mutex and verify error. */
void mutex_unlock() {
    if (pthread_mutex_unlock(&mutex) != 0)
        perror("Error unlocking mutex.\n");
}
 

/* Function to lock rwlock in read mode and verify error. */
void rw_readlock() {
	if (pthread_rwlock_rdlock(&rwl) != 0)
		perror("Error locking rwlock for reading.\n");
}


/* Function to lock rwlock in write mode and verify error. */
void rw_writelock() {
	if (pthread_rwlock_wrlock(&rwl) != 0) {
		perror("Error locking rwlock for writing.\n");
	}
}


/* Function to unlock rwlock and verify error. */
void rw_unlock() {
	if (pthread_rwlock_unlock(&rwl) != 0)
		perror("Error unlocking rwlock for reading.\n");
}


/* Function to choose what strategy will be used to lock threads. */
void lock(int syncstrat, int mode) {

	if (syncstrat == MUTEX)
		mutex_lock();

	else if (syncstrat == RWLOCK && mode == READ)
		rw_readlock();
	
	else if (syncstrat == RWLOCK && mode == WRITE) 
		rw_writelock();
}


/* Function to choose what strategy will be used to unlock threads. */
void unlock(int syncstrat) {
	
	if (syncstrat == MUTEX)
		mutex_unlock();

	else if (syncstrat == RWLOCK)	
		rw_unlock();
}