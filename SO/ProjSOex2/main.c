#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include <string.h>
#include <ctype.h>
#include "fs/operations.h"
#include "fs/state.h"
#include <time.h>
#include <pthread.h>
#include <unistd.h>

#define MAX_COMMANDS 10
#define MAX_INPUT_SIZE 100
#define TRUE 1
#define FALSE 0

int numberThreads = 0;

char inputCommands[MAX_COMMANDS][MAX_INPUT_SIZE];

int numberCommands = 0;
int removeHere = 0;
int insertHere = 0;

int endOfFile = FALSE;

int insertCommand(char* data) {

    if (pthread_mutex_lock(&mutexCommands) != 0)
        perror("Error locking mutex.\n");

    while (numberCommands == MAX_COMMANDS){
        if (pthread_cond_wait(&insertCond, &mutexCommands) != 0)
            perror("Error waiting for condVar\n");
    }

    strcpy(inputCommands[insertHere++], data);

    if (insertHere == MAX_COMMANDS) insertHere = 0;
    numberCommands++;

    if (pthread_cond_signal(&removeCond) != 0)
        perror("Error signaling remove condVar\n");

    if (pthread_mutex_unlock(&mutexCommands) != 0)
        perror("Error unlocking mutex.\n");
    
    return 1;
}


char * removeCommand() {

    if (pthread_mutex_lock(&mutexCommands) != 0)
        perror("Error locking mutexCommands in removeCommand");
    

    while (endOfFile == FALSE && numberCommands == 0){
        if (pthread_cond_wait(&removeCond, &mutexCommands) != 0)
            perror("Error waiting for remove condVar\n");
    }

    if (endOfFile == TRUE && numberCommands == 0){
        if (pthread_mutex_unlock(&mutexCommands) != 0)
            perror("Error unlocking mutexCommands\n");
        
        pthread_exit(NULL);
    }

    char* command = inputCommands[removeHere++];

    if (removeHere == MAX_COMMANDS) removeHere = 0;
    if (numberCommands > 0) numberCommands--;

    if (pthread_cond_signal(&insertCond) != 0)
        perror("Error signaling insert condVar\n");

    return command;
}


void errorParse(){
    fprintf(stderr, "Error: command invalid\n");
    exit(EXIT_FAILURE);
}


void processInput(FILE *inFile){
    char line[MAX_INPUT_SIZE];

    /* break loop with ^Z or ^D */
    while (fgets(line, sizeof(line)/sizeof(char), inFile)) {
        char token, type[MAX_INPUT_SIZE], name[MAX_INPUT_SIZE];

        int numTokens = sscanf(line, "%c %s %s", &token, name, type);

        /* perform minimal validation */
        if (numTokens < 1) {
            continue;
        }
        switch (token) {
            case 'c':
                if(numTokens != 3)
                    errorParse();
                if(insertCommand(line))
                    break;
                return;
            
            case 'l':
                if(numTokens != 2)
                    errorParse();
                if(insertCommand(line))
                    break;
                return;
            
            case 'd':
                if(numTokens != 2)
                    errorParse();
                if(insertCommand(line))
                    break;
                return;

            case 'm':
                if(numTokens != 3)
                    errorParse();
                if (insertCommand(line))
                    break;
                return;
            
            case '#':
                break;
            
            default: { /* error */
                errorParse();
            }
        }
    }
     
    if (pthread_mutex_lock(&mutexCommands) != 0)
        perror("Error unlocking mutexCommands\n");

    endOfFile = TRUE;
    if (pthread_cond_broadcast(&removeCond) != 0)
        perror("Error signaling all threads with remove condition. \n");

    if (pthread_mutex_unlock(&mutexCommands) != 0)
        perror("Error unlocking mutexCommands\n");
}   




void * applyCommands(void * arg){

    while(TRUE) {

        const char* command = removeCommand();

        if (command == NULL) {
            if (pthread_mutex_unlock(&mutexCommands) != 0)
                perror("Error unlocking mutexCommands\n");
            pthread_exit(NULL);
        }

        char token, type[MAX_INPUT_SIZE], name[MAX_INPUT_SIZE];
        int numTokens = sscanf(command, "%c %s %s", &token, name, type);
        if (numTokens < 2) {
            fprintf(stderr, "Error: invalid command in Queue\n");
            exit(EXIT_FAILURE);
        }

        int searchResult;
        int number_ofDirs = 0;
        int * inumber_table;

        if (pthread_mutex_unlock(&mutexCommands) != 0)
            perror("Error unlocking mutexCommands\n");

        switch (token) {
            case 'c':
                switch (type[0]) {
                    case 'f':
                        printf("Create file: %s\n", name);
                        create(name, T_FILE);
                        break;

                    case 'd':
                        printf("Create directory: %s\n", name);
                        create(name, T_DIRECTORY);;
                        break;
                    
                    default:
                        fprintf(stderr, "Error: invalid node type\n");
                        exit(EXIT_FAILURE);
                }
                break;

            case 'l':
                inumber_table = malloc(sizeof(int) * INODE_TABLE_SIZE);

                searchResult = lookup(name, &inumber_table, &number_ofDirs, LOOKUP);
                if (searchResult >= 0)
                    printf("Search: %s found\n", name);
                else
                    printf("Search: %s not found\n", name);
                break;

            case 'd':
                printf("Delete: %s\n", name);
                delete(name);
                break;

            case 'm':
                printf("Move: %s to %s\n", name, type);
                move(name, type);
                break;

            default: {
                fprintf(stderr, "Error: command to apply\n");
                exit(EXIT_FAILURE);
            }
        }
    }

    return NULL;
}


void read_input_file(char *name){
    /* Open input file on read mode. */
    FILE *inputFile = fopen(name, "r");

    /* If the input file name read on input (argv[1]) is not an existing file 
        or is not on the current directory. */
    if(inputFile == NULL){
        perror("Error: Input file doesn't exist or is in the wrong directory.\n");
        exit(EXIT_FAILURE);
    }

    /* Process the input of the file and close it. */
    processInput(inputFile);
    fclose(inputFile);
}


void generate_output(char *name){
    /* Open output file on write mode. */
    FILE *outputFile = fopen(name, "w");

    /* If theres is an error opening file */
    if (outputFile == NULL){
        perror("Error: It wasn't possible to open output file.\n");
        exit(EXIT_FAILURE);
    }

    /* Print output on the output file and close it. */
    print_tecnicofs_tree(outputFile);
    fclose(outputFile);
}



void threads_pool(int numberThreads, char * argv[], struct timespec * begin) {

    /* Initialize mutex : always needed to protect vector of commands. */
    pthread_mutex_init(&mutexCommands, NULL);
    
    
    /* Initialize condVar */
    pthread_cond_init(&insertCond, NULL);
    pthread_cond_init(&removeCond, NULL);

    /* Allocate memmory to initialize 'numberThreads' threads. */
    pthread_t * thread_id = malloc(sizeof(pthread_t) * numberThreads);


    /* Start 10 threads with the function applyCommands to apply the commands read on input. */
    for (int n = 0; n < numberThreads; n++) {
        if (pthread_create(&thread_id[n], NULL, applyCommands, NULL) != 0){
            printf("Error creating thread number: %d.\n", n);
            return;
        }
    }

    clock_gettime(CLOCK_REALTIME, begin);

    /* Keep reading input file, processing and inserting commande while != EOF : argv[1] verified.*/
    read_input_file(argv[1]);

    /* Wait for 10 threads to finnish their task of applying commands. */
    for (int j = 0; j < numberThreads; j++) {
        if (pthread_join(thread_id[j], NULL) != 0){
            printf("Error joining thread number: %d.\n", j);
            return;
        }
    }



    pthread_mutex_destroy(&mutexCommands);
    pthread_cond_destroy(&insertCond);
    pthread_cond_destroy(&removeCond);
    free(thread_id);
}


void get_syncStrategy(char * argv[], struct timespec * begin) {

    /* get the number of threads */
    numberThreads = atoi(argv[3]);
    threads_pool(numberThreads, argv, begin);
}


/* This function measures the ammout of real time spent since the program began working. */

void print_execution_time(struct timespec begin, struct timespec end){
    
    clock_gettime(CLOCK_REALTIME, &end);

    long seconds = end.tv_sec - begin.tv_sec;
    long nanoseconds = end.tv_nsec - begin.tv_nsec;
    double time_spent = seconds + nanoseconds*1e-9;

    printf("TecnicoFS completed in [%.4lf] seconds.\n", time_spent);
}


void verifyArgs(int argc, char * argv[]) {

    /* If 5 arguments aren't read on the command line. */
    if (argc != 4){
        perror("ERROR IN INPUT: Number of arguments given is incorrect.\n");
        exit(EXIT_FAILURE);
    }

    /* If number of threads received in output is not an integer or an integer less than 1. */
    else if (atoi(argv[3]) <= 0){
        perror("ERROR IN INPUT: Number of threads given is incorrect.\n");
        exit(EXIT_FAILURE);
    }

}


int main(int argc, char* argv[]) {

    /* init clock : starts execution time -> (Wall clock time) */
    struct timespec begin, end;

    /* init filesystem */
    init_fs();

    /* verifies arguments before using them */
    verifyArgs(argc, argv);

    /* choose what strategy to use for syncronizing threads */
    get_syncStrategy(argv, &begin);

    /* stop measuring time and calculate the total time of execution -> (Wall clock time). */
    print_execution_time(begin, end);

    /* send output to a file -> here argv[2] is verified */
    generate_output(argv[2]);

    /* release allocated memory */
    destroy_fs();

    exit(EXIT_SUCCESS);
}