#include "state.h"
#include "operations.h"
#include "sync.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>




/* This function locks a single inode with inumber received on argument */
void unlockNode(int inumber) {
	union Data pdata;
	type nType;
	pthread_rwlock_t * rwlock;

	inode_get(inumber, &nType, &pdata, &rwlock);
	
	if (pthread_rwlock_unlock(rwlock) != 0) {
		perror("Error unlocking rwlock.\n");
		exit(EXIT_FAILURE);
	}	
}




/* This function unlocks all threads locked to execute the operation */
void unlockThreads(int * inumber_table, int number_ofDirs) {
	int i = 0;

	while (i < number_ofDirs) {
		unlockNode(inumber_table[i++]);
	}

	free(inumber_table);
}


/* This function is used to free all the resources allocated on move operation */
void free_move_resources(char ** paths, char * pathCopy, char * dest_child) {

	free(dest_child);
	free(pathCopy);
	if (paths != NULL) {
		free(paths[PATH1]);
		free(paths[PATH2]);
		free(paths);
	}
}




/* This function is responsible of locking a node (corresponding to a directory). */
/* mode -> WRITE or READ */
void lockNode(int mode, int inumber) {
	union Data pdata;
	type nType;
	pthread_rwlock_t * rwlock;

	/* Get the inode corresponding to the inumber received in the argument to access rwlock */
	inode_get(inumber, &nType, &pdata, &rwlock);

	/* lock according to the chosen (Read / Write) mode */ 
	if (mode == READ) {
		if (pthread_rwlock_rdlock(rwlock) != 0)
			perror("Error locking rwlock for reading.\n");
	}

	else if (mode == WRITE) {
		if (pthread_rwlock_wrlock(rwlock) != 0)
			perror("Error locking rwlock for writing.\n");
	}
}





/* This function is used to add an inumber to the inumber_table */
/* It will only add the inumber, if it hadn't been previously added there. */
int addto_table(int ** inumber_table, int current_inumber, int callingFunc, int * number_ofDirs) {
	int n = 0;

	if (callingFunc == MOVE) {
		for (n = 0; n < *number_ofDirs; n++){
			if ((*inumber_table)[n] == current_inumber) {
				return FAIL;
			}
		}
	}

	(*inumber_table)[(*number_ofDirs)++] = current_inumber;
	return SUCCESS;
}





/* This functions returns the number of Directorys on a given path */

int get_numberOfDirs(char * path){
	char * saveptr, delim[] = "/", *pathCopy = malloc(sizeof(char) * strlen(path));
	int number_ofDirs = 0;
	strcpy(pathCopy, path);

	char * pathName = strtok_r(pathCopy, delim, &saveptr);

	while (pathName != NULL){
		pathName = strtok_r(NULL, delim, &saveptr);
		number_ofDirs++;
	}

	free(pathCopy);
	return number_ofDirs;
}




/* This function is used to choose what path to lock firs, on move operation */
void lock_paths(int ** inumber_table, int * number_ofDirs, int * path1_inumber,
			  int * path2_inumber, char ** paths){

	int path_locked_first;
	int numDirs_path1 = get_numberOfDirs(paths[PATH1]);
	int numDirs_path2 = get_numberOfDirs(paths[PATH2]);

	/* This block of code chooses what path to lock first:
	 * paths with different number of dirs -> first lock the one with less: Securing WrL.
	 * paths with the same number of dirs -> lock by strcmp value avoiding later deadlocks.
	*/
	if (numDirs_path1 < numDirs_path2){
		/* Check if the parent of file/dir path to move exists. */
		*path1_inumber = lookup(paths[PATH1], inumber_table, number_ofDirs, MOVE);
		path_locked_first = PATH1;
	}

	else if (numDirs_path1 > numDirs_path2){
		/* Check if the destination's parent path exists (PATH2) */
		*path2_inumber = lookup(paths[PATH2], inumber_table, number_ofDirs, MOVE);
		path_locked_first = PATH2;
	}

	else {
		int strcmp_result = strcmp(paths[PATH1], paths[PATH2]);

		if (strcmp_result >= 0){
			/* Check if the parent of file/dir path to move exists. */
			*path1_inumber = lookup(paths[PATH1], inumber_table, number_ofDirs, MOVE);
			path_locked_first = PATH1;
		}

		else if (strcmp_result < 0){
			/* Check if the destination's parent path exists (PATH2) */
			*path2_inumber = lookup(paths[PATH2], inumber_table, number_ofDirs, MOVE);
			path_locked_first = PATH2;
		}
	}

	if (path_locked_first == PATH1){
		/* Check if the destination's parent path exists (PATH2) */
		*path2_inumber = lookup(paths[PATH2], inumber_table, number_ofDirs, MOVE);
	}

	/* Check if the parent of file/dir path to move exists. */
	else *path1_inumber = lookup(paths[PATH1], inumber_table, number_ofDirs, MOVE);
}