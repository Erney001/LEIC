#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include <string.h>
#include <ctype.h>
#include "fs/operations.h"
#include <time.h>
#include <pthread.h>
#include <unistd.h>


#define MAX_COMMANDS 150000
#define MAX_INPUT_SIZE 100

int numberThreads = 0;

char inputCommands[MAX_COMMANDS][MAX_INPUT_SIZE];
int numberCommands = 0;
int headQueue = 0;

int insertCommand(char* data) {
    if(numberCommands != MAX_COMMANDS) {
        strcpy(inputCommands[numberCommands++], data);
        return 1;
    }
    return 0;
}

char * removeCommand() {

    if (pthread_mutex_lock(&mutexCommands) != 0)
        perror("Error locking mutex.\n");

    if(numberCommands > 0){
        numberCommands--;

        if (pthread_mutex_unlock(&mutexCommands) != 0)
            perror("Error unlocking mutex.\n");

        return inputCommands[headQueue++];  
    }

    if (pthread_mutex_unlock(&mutexCommands) != 0)
        perror("Error unlocking mutex.\n");
    return NULL;
}


void errorParse(){
    fprintf(stderr, "Error: command invalid\n");
    exit(EXIT_FAILURE);
}


void processInput(FILE *inFile){
    char line[MAX_INPUT_SIZE];

    /* break loop with ^Z or ^D */
    while (fgets(line, sizeof(line)/sizeof(char), inFile)) {
        char token, type;
        char name[MAX_INPUT_SIZE];

        int numTokens = sscanf(line, "%c %s %c", &token, name, &type);

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
            
            case '#':
                break;
            
            default: { /* error */
                errorParse();
            }
        }
    }
}

void * applyCommands(void * arg){
    
    while (numberCommands > 0){

        const char* command = removeCommand();

        if (command == NULL) break;

        char token, type;
        char name[MAX_INPUT_SIZE];
        int numTokens = sscanf(command, "%c %s %c", &token, name, &type);
        if (numTokens < 2) {
            fprintf(stderr, "Error: invalid command in Queue\n");
            exit(EXIT_FAILURE);
        }

        int searchResult;

        switch (token) {
            case 'c':
                switch (type) {
                    case 'f':
                        printf("Create file: %s\n", name);
                        create(name, T_FILE);
                        break;

                    case 'd':
                        printf("Create directory: %s\n", name);
                        create(name, T_DIRECTORY);
                        break;
                    
                    default:
                        fprintf(stderr, "Error: invalid node type\n");
                        exit(EXIT_FAILURE);
                }
                break;

            case 'l':
                lock(syncStrategy, READ);

                searchResult = lookup(name);
                if (searchResult >= 0)
                    printf("Search: %s found\n", name);
                else
                    printf("Search: %s not found\n", name);

                unlock(syncStrategy);
                break;

            case 'd':
                printf("Delete: %s\n", name);
                delete(name);
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
        perror("ERROR OPENING FILE: Input file doesn't exist or is in the wrong directory.\n");
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
        perror("ERROR OPENING FILE: It wasn't possible to open output file.\n");
        exit(EXIT_FAILURE);
    }

    /* Print output on the output file and close it. */
    print_tecnicofs_tree(outputFile);
    fclose(outputFile);
}



void threads_pool(int numberThreads) {

    /* Initialize mutex : always needed to protect vector of commands. */
    pthread_mutex_init(&mutexCommands, NULL);

    /* Allocate memmory to initialize 'numberThreads' threads. */
    pthread_t * thread_id = malloc(sizeof(pthread_t) * numberThreads);


    /* Start 10 threads with the function applyCommands to apply the commands read on input. */
    for (int n = 0; n < numberThreads; n++) {
        if (pthread_create(&thread_id[n], NULL, applyCommands, NULL) != 0){
            printf("Error creating thread number: %d.\n", n);
            return;
        }
    }

    /* Wait for 10 threads to finnish their task of applying commands. */
    for (int j = 0; j < numberThreads; j++) {
        if (pthread_join(thread_id[j], NULL) != 0){
            printf("Error joining thread number: %d.\n", j);
            return;
        }
    }
    
    free(thread_id);
}


void get_syncStrategy(char * argv[]) {

    /* get the number of threads */
    numberThreads = atoi(argv[3]);
    

    /* No strategy selected -> applyCommands sequentialy */
    if (strcmp(argv[4], "nosync") == 0 && numberThreads == 1){
        applyCommands(NULL);
    }

    /* Activate rwlock strategy initializing rwlock and calling threadsPool to start threads. */
    else if (strcmp(argv[4], "rwlock") == 0 && numberThreads > 1) {
        syncStrategy = RWLOCK;
        pthread_rwlock_init(&rwl, NULL);
        threads_pool(numberThreads);

        /* destroy rwlock and mutex that protects the vector of commands */
        pthread_rwlock_destroy(&rwl);
        pthread_mutex_destroy(&mutexCommands);
    }

    /* Activate mutex strategy initializing mutex and calling threadsPool to start threads. */
    else if (strcmp(argv[4], "mutex") == 0 && numberThreads > 1){
        syncStrategy = MUTEX;
        pthread_mutex_init(&mutex, NULL);
        threads_pool(numberThreads);

        /* destroy both mutexs that were initialized */
        pthread_mutex_destroy(&mutex);
        pthread_mutex_destroy(&mutexCommands);
    }
    else perror("Wrong format given for numberThreads + syncStrategy.\n");
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
    if (argc != 5){
        perror("ERROR IN INPUT: Number of arguments given is incorrect.\n");
        exit(EXIT_FAILURE);
    }

    /* If number of threads received in output is not an integer or an integer less than 1. */
    else if (atoi(argv[3]) <= 0){
        perror("ERROR IN INPUT: Number of threads given is incorrect.\n");
        exit(EXIT_FAILURE);
    }    

    /* If none of the correct syncronization strategys were received in the input. */
    else if (strcmp(argv[4], "nosync") && strcmp(argv[4], "mutex") && strcmp(argv[4], "rwlock")){
        perror("ERROR IN INPUT: SyncStrategy given is incorrect.\n");
        exit(EXIT_FAILURE);
    }

}


int main(int argc, char* argv[]) {

    /* init clock : starts execution time -> (Wall clock time) */
    struct timespec begin, end;
    clock_gettime(CLOCK_REALTIME, &begin);

    /* init filesystem */
    init_fs();

    /* verifies arguments before using them */
    verifyArgs(argc, argv);

    /* here argv[1] is verified */
    read_input_file(argv[1]);

    /* choose what strategy to use for syncronizing threads */
    get_syncStrategy(argv);

    /* stop measuring time and calculate the total time of execution -> (Wall clock time). */
    print_execution_time(begin, end);

    /* send output to a file -> here argv[2] is verified */
    generate_output(argv[2]);

    /* release allocated memory */
    destroy_fs();

    exit(EXIT_SUCCESS);
}