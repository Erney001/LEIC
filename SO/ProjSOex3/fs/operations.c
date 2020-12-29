#include "operations.h"
#include "state.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

/* Given a path, fills pointers with strings for the parent path and child
 * file name
 * Input:
 *  - path: the path to split. ATENTION: the function may alter this parameter
 *  - parent: reference to a char*, to store parent path
 *  - child: reference to a char*, to store child file name
 */
void split_parent_child_from_path(char * path, char ** parent, char ** child) {

	int n_slashes = 0, last_slash_location = 0;
	int len = strlen(path);

	// deal with trailing slash ( a/x vs a/x/ )
	if (path[len-1] == '/') {
		path[len-1] = '\0';
	}

	for (int i=0; i < len; ++i) {
		if (path[i] == '/' && path[i+1] != '\0') {
			last_slash_location = i;
			n_slashes++;
		}
	}

	if (n_slashes == 0) { // root directory
		*parent = "";
		*child = path;
		return;
	}

	path[last_slash_location] = '\0';
	*parent = path;
	*child = path + last_slash_location + 1;
}


/*
 * Initializes tecnicofs and creates root node.
 */
void init_fs() {
	inode_table_init();
	
	/* create root inode */
	int root = inode_create(T_DIRECTORY);

	if (root != FS_ROOT) {
		printf("failed to create node for tecnicofs root\n");
		exit(EXIT_FAILURE);
	}
}


/*
 * Destroy tecnicofs and inode table.
 */
void destroy_fs() {
	inode_table_destroy();
}


/*
 * Checks if content of directory is not empty.
 * Input:
 *  - entries: entries of directory
 * Returns: SUCCESS or FAIL
 */

int is_dir_empty(DirEntry *dirEntries) {

	if (dirEntries == NULL) {
		return FAIL;
	}

	for (int i = 0; i < MAX_DIR_ENTRIES; i++) {
		if (dirEntries[i].inumber != FREE_INODE) {
			return FAIL;
		}
	}
	return SUCCESS;
}


/*
 * Looks for node in directory entry from name.
 * Input:
 *  - name: path of node
 *  - entries: entries of directory
 * Returns:
 *  - inumber: found node's inumber
 *  - FAIL: if not found
 */
int lookup_sub_node(char *name, DirEntry *entries) {

	if (entries == NULL) {
		return FAIL;
	}
	for (int i = 0; i < MAX_DIR_ENTRIES; i++) {
        if (entries[i].inumber != FREE_INODE && strcmp(entries[i].name, name) == 0) {
            return entries[i].inumber;
        }
    }
	return FAIL;
}


/*
 * Creates a new node given a path.
 * Input:
 *  - name: path of node
 *  - nodeType: type of node
 * Returns: SUCCESS or FAIL
 */
int create(char *name, type nodeType){

	int parent_inumber, child_inumber;
	char *parent_name, *child_name, name_copy[MAX_FILE_NAME];
	type pType;
	union Data pdata;
	int * inumber_table = malloc(sizeof(int) * INODE_TABLE_SIZE);
	int number_ofDirs = 0;

	strcpy(name_copy, name);
	split_parent_child_from_path(name_copy, &parent_name, &child_name);

	parent_inumber = lookup(parent_name, &inumber_table, &number_ofDirs, CREATE);

	if (parent_inumber == FAIL) {
		printf("failed to create %s, invalid parent dir %s\n", name, parent_name);

		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	inode_get(parent_inumber, &pType, &pdata, NULL);

	if(pType != T_DIRECTORY) {
		printf("failed to create %s, parent %s is not a dir\n", name, parent_name);

		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	if (lookup_sub_node(child_name, pdata.dirEntries) != FAIL) {
		printf("failed to create %s, already exists in dir %s\n", child_name, parent_name);

		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	/* create node and add entry to folder that contains new node */
	child_inumber = inode_create(nodeType);

	if (child_inumber == FAIL) {
		printf("failed to create %s in  %s, couldn't allocate inode\n", child_name, parent_name);

		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	if (dir_add_entry(parent_inumber, child_inumber, child_name) == FAIL) {
		printf("could not add entry %s in dir %s\n", child_name, parent_name);
		
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	unlockThreads(inumber_table, number_ofDirs);
	return SUCCESS;
}





/*
 * Deletes a node given a path.
 * Input:
 *  - name: path of node
 * Returns: SUCCESS or FAIL
 */
int delete(char *name) {

	int parent_inumber, child_inumber;
	char *parent_name, *child_name, name_copy[MAX_FILE_NAME];
	type pType, cType;
	union Data pdata, cdata;
	int * inumber_table = malloc(sizeof(int) * INODE_TABLE_SIZE);
	int number_ofDirs = 0;

	strcpy(name_copy, name);
	split_parent_child_from_path(name_copy, &parent_name, &child_name);

	parent_inumber = lookup(parent_name, &inumber_table, &number_ofDirs, DELETE);

	if (parent_inumber == FAIL) {
		printf("failed to delete %s, invalid parent dir %s\n",
		        child_name, parent_name);
		
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	inode_get(parent_inumber, &pType, &pdata, NULL);

	if (pType != T_DIRECTORY) {
		printf("failed to delete %s, parent %s is not a dir\n",
		        child_name, parent_name);

		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	child_inumber = lookup_sub_node(child_name, pdata.dirEntries);

	if (child_inumber == FAIL) {
		printf("could not delete %s, does not exist in dir %s\n",
		       name, parent_name);

		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	inode_get(child_inumber, &cType, &cdata, NULL);

	if (cType == T_DIRECTORY && is_dir_empty(cdata.dirEntries) == FAIL) {
		printf("could not delete %s: is a directory and not empty\n",
		       name);
		
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	/* remove entry from folder that contained deleted node */
	if (dir_reset_entry(parent_inumber, child_inumber) == FAIL) {
		printf("failed to delete %s from dir %s\n",
		       child_name, parent_name);
		
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	if (inode_delete(child_inumber) == FAIL) {
		printf("could not delete inode number %d from dir %s\n",
		       child_inumber, parent_name);
		
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	unlockThreads(inumber_table, number_ofDirs);
	return SUCCESS;
}



/*
 * Lookup for a given path.
 * Input:
 *  - name: path of node
 * Returns:
 *  inumber: identifier of the i-node, if found
 *     FAIL: otherwise
 */
int lookup(char *name, int ** inumber_table, int * number_ofDirs, int callingFunc) {

	/* Initializing and preparing fields */
	char full_path[MAX_FILE_NAME], pathCopy[MAX_FILE_NAME], delim[] = "/";
	char *savepointer, *parent_name, *child_name;
	int current_inumber = FS_ROOT;
	type nType;
	union Data data;
	
	strcpy(full_path, name);
	strcpy(pathCopy, full_path);

	split_parent_child_from_path(pathCopy, &parent_name, &child_name);	

	/* Start the lookup, lock and save inumbers in a vector while doing it */

	/* get root inode data */
	inode_get(current_inumber, &nType, &data, NULL);

	/* If creating directory/file on root, WRITE LOCK root. */
	if (callingFunc != LOOKUP && (strcmp(name, "") == 0)) {
		if (addto_table(inumber_table, current_inumber, callingFunc, number_ofDirs) == SUCCESS)	
			lockNode(WRITE, current_inumber);
	}

	/* Creating after root -> READ LOCK root in all OPERATIONS : LOOKUP/CREATE/DELETE */
	else {
		if (addto_table(inumber_table, current_inumber, callingFunc, number_ofDirs) == SUCCESS)
			lockNode(READ, current_inumber);
	}
	

	char *path = strtok_r(full_path, delim, &savepointer);

	/* search for all sub nodes */
	while (path != NULL && (current_inumber = lookup_sub_node(path, data.dirEntries)) != FAIL) {

		inode_get(current_inumber, &nType, &data, NULL);

		/* If path is second child (parent directory) : Modifying there -> WRITE LOCK */
		if (callingFunc != LOOKUP && strcmp(path, child_name) == 0) {
			/* Prevent the file/dir being created not to be added to the vector */
			if (addto_table(inumber_table, current_inumber, callingFunc, number_ofDirs) == SUCCESS)
				lockNode(WRITE, current_inumber);
		}
		
		/* If we are modifying read lock all directorys -> path only has parent Directorys */
		else {
			if (addto_table(inumber_table, current_inumber, callingFunc, number_ofDirs) == SUCCESS)
				lockNode(READ, current_inumber);
		}
		
		path = strtok_r(NULL, delim, &savepointer);
	}

	if (callingFunc == LOOKUP) unlockThreads(*inumber_table, *number_ofDirs);

	return current_inumber;
}



/* This function is the move operation asked to released on requisite 3 of ex2 */
int move(char *name1, char *name2) {
	type nodeType;
	union Data pdata;
	int path1_inumber, path2_inumber, path1_parent_inumber, path2_parent_inumber;
	char * parent_name, * child_name, * pathCopy;
	int number_ofDirs = 0;
	int * inumber_table = malloc(sizeof(int) * INODE_TABLE_SIZE);
	char ** paths = malloc(sizeof(char *) * 2);
	paths[PATH1] = malloc(sizeof(char) * (strlen(name1)+1));
	paths[PATH2] = malloc(sizeof(char) * (strlen(name2)+1));

	pathCopy = malloc(sizeof(char) * (strlen(name2)+1));
	strcpy(pathCopy, name2);
	split_parent_child_from_path(pathCopy, &parent_name, &child_name);
	char * dest_child = malloc(sizeof(char) * (strlen(child_name)+1));
	strcpy(dest_child, child_name);
	strcpy(paths[PATH2], parent_name);
	free(pathCopy);

	pathCopy = malloc(sizeof(char) * (strlen(name1)+1));
	strcpy(pathCopy, name1);
	split_parent_child_from_path(pathCopy, &parent_name, &child_name);
	strcpy(paths[PATH1], parent_name);



	/* Lock both parent paths */
	lock_paths(&inumber_table, &number_ofDirs, &path1_parent_inumber, 
			   &path2_parent_inumber, paths);


	/* Check if both parent directorys were found by lookup (and locked) */
	if (path1_parent_inumber == FAIL || path2_parent_inumber == FAIL) {
		free_move_resources(paths, pathCopy, dest_child);
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}


	inode_get(path2_parent_inumber, &nodeType, &pdata, NULL);
	path2_inumber = lookup_sub_node(dest_child, pdata.dirEntries);
	type path2_parent_nodeType = nodeType;

	inode_get(path1_parent_inumber, &nodeType, &pdata, NULL);
	path1_inumber = lookup_sub_node(child_name, pdata.dirEntries);


	/* Check if there is not an existing destination path before moving. */
	/* And that the file/dir to move exists. */
	if (path2_inumber != FAIL || path1_inumber == FAIL || path1_inumber == path2_parent_inumber) {
		free_move_resources(paths, pathCopy, dest_child);
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}


	if (path2_parent_nodeType != T_DIRECTORY) {
		printf("failed to create %s, parent %s is not a dir\n", child_name, paths[PATH2]);

		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}


	/* Move node from a dir to another without deleting. */
	if (dir_add_entry(path2_parent_inumber, path1_inumber, child_name) == FAIL) {
		printf("could not add entry %s in dir %s\n", child_name, parent_name);
		free_move_resources(paths, pathCopy, dest_child);
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	/* Remove entry from folder that contained deleted node. */
	if (dir_reset_entry(path1_parent_inumber, path1_inumber) == FAIL) {
		printf("failed to delete %s from dir %s\n", child_name, parent_name);
		free_move_resources(paths, pathCopy, dest_child);
		unlockThreads(inumber_table, number_ofDirs);
		return FAIL;
	}

	free_move_resources(paths, pathCopy, dest_child);
	unlockThreads(inumber_table, number_ofDirs);
	return SUCCESS;
}




/*
 * Prints tecnicofs tree.
 * Input:
 *  - fp: pointer to output file
 */
void print_tecnicofs_tree(FILE *fp){
	inode_print_tree(fp, FS_ROOT, "");
}



int print(char * name) {
	pthread_rwlock_t * rwlock;

	/* Open output file on write mode. */
    FILE *outputFile = fopen(name, "w");

    /* If theres is an error opening file */
    if (outputFile == NULL) {
    	perror("Error opening file \n");
    	return FAIL;
    }

	inode_get(FS_ROOT, NULL, NULL, &rwlock);


	if (pthread_rwlock_wrlock(rwlock) != 0) {
		perror("Error locking rwlock.\n");
		return FAIL;
	}

	/* Print output on the output file and close it. */
    print_tecnicofs_tree(outputFile);

    if (fclose(outputFile) < 0) {
    	perror("Error closing file in print command");
    	return FAIL;
    }

    /* exit to avoid deadlocks */
    if (pthread_rwlock_unlock(rwlock) != 0) {
		perror("Error locking rwlock.\n");
		exit(EXIT_FAILURE);
    }

    return 0;
}