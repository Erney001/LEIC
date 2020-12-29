#ifndef FS_H
#define FS_H
#include "state.h"

#define CREATE 0
#define DELETE 1
#define LOOKUP 2
#define MOVE 3


void init_fs();
void destroy_fs();
int is_dir_empty(DirEntry *dirEntries);
int create(char *name, type nodeType);
int delete(char *name);
int lookup(char *name, int ** inumber_table, int * number_ofDirs, int callingFunc);
int move(char *name, char *name2);
void print_tecnicofs_tree(FILE *fp);
void split_parent_child_from_path(char * path, char ** parent, char ** child);


#endif /* FS_H */
