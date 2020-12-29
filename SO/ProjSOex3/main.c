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
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <sys/stat.h>

#define MAX_COMMANDS 10
#define MAX_INPUT_SIZE 100
#define TRUE 1
#define FALSE 0



int numberThreads = 0;
int server_sockfd;

int setSockAddrUn(char *path, struct sockaddr_un *addr);
void startSocket(char * serverName);
void threads_pool(char * argv[]);
void errorParse();
void verify_args(int argc, char * argv[]);
void * getCommunication(void * ptr);
int apply_command(char * command);




/*************************************************************************************/
/*                                                                                   */
/*                               Main Function                                       */
/*                                                                                   */
/*************************************************************************************/




int main(int argc, char* argv[]) {

    /* init filesystem */
    init_fs();

    /* verifies arguments before using them */
    verify_args(argc, argv);

    /* choose what strategy to use for syncronizing threads */
    threads_pool(argv);

    /* release allocated memory */
    destroy_fs();

    exit(EXIT_SUCCESS);
}




/*************************************************************************************/
/*                                                                                   */
/*                                Threads Pool                                       */
/*                                                                                   */
/*************************************************************************************/


void threads_pool(char * argv[]) {

    numberThreads = atoi(argv[1]);
    /* Allocate memmory to initialize 'numberThreads' threads. */
    pthread_t * thread_id = malloc(sizeof(pthread_t) * numberThreads);

    startSocket(argv[2]);

    /* Start 10 threads with the function getCommunication to apply the commands read on input. */
    for (int n = 0; n < numberThreads; n++) {
        if (pthread_create(&thread_id[n], NULL, getCommunication, NULL) != 0){
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




/*************************************************************************************/
/*                                                                                   */
/*                          Functions related to socket                              */
/*                                                                                   */
/*************************************************************************************/



int setSockAddrUn(char *path, struct sockaddr_un *addr) {

  if (addr == NULL)
    return 0;

  bzero((char *)addr, sizeof(struct sockaddr_un));
  addr->sun_family = AF_UNIX;
  strcpy(addr->sun_path, path);

  return SUN_LEN(addr);
}



void startSocket(char * serverName) {
    struct sockaddr_un server_addr;
    socklen_t addrlen;

    if ((server_sockfd = socket(AF_UNIX, SOCK_DGRAM, 0)) < 0) {
        perror("server: can't open socket");
        exit(EXIT_FAILURE);
    }

    addrlen = setSockAddrUn(serverName, &server_addr);
    if (addrlen <= 0) {
        perror("Error setting socket address");
        exit(EXIT_FAILURE);
    }


    if (bind(server_sockfd, (struct sockaddr *) &server_addr, addrlen) < 0) {
        perror("server: bind error");
        exit(EXIT_FAILURE);
    }

    if (chmod(serverName, 00222) == -1) {
        perror("server:: can't change permissions of socket");
        exit(EXIT_FAILURE);
    }
}




/*************************************************************************************/
/*                                                                                   */
/*                     Functions related to error verification                       */
/*                                                                                   */
/*************************************************************************************/




void errorParse() {
    fprintf(stderr, "Error: command invalid\n");
    exit(EXIT_FAILURE);
}




void verify_args(int argc, char * argv[]) {

    if (argc != 3) {
        perror("ERROR IN INPUT: Number of arguments given is incorrect.\n");
        exit(EXIT_FAILURE);
    }

    else if (atoi(argv[1]) <= 0) {
        perror("ERROR IN INPUT: Number of threads given is incorrect.\n");
        exit(EXIT_FAILURE);
    }
}



/*************************************************************************************/
/*                                                                                   */
/*                   Functions related to execution of commands                      */
/*                                                                                   */
/*************************************************************************************/




int apply_command(char * command) {
    
    char token, type[MAX_INPUT_SIZE], name[MAX_INPUT_SIZE];
    int numTokens = sscanf(command, "%c %s %s", &token, name, type);
    if (numTokens < 2) {
        fprintf(stderr, "Error: invalid command in Queue\n");
        exit(EXIT_FAILURE);
    }

    int searchResult;
    int number_ofDirs = 0;
    int * inumber_table;

    switch (token) {
        case 'c':
            switch (type[0]) {
                case 'f':
                    printf("Create file: %s\n", name);
                    return create(name, T_FILE);;
                
                case 'd':
                    printf("Create directory: %s\n", name);
                    return create(name, T_DIRECTORY);;
                    
                default:
                    fprintf(stderr, "Error: invalid node type\n");
                    return FAIL;
            }
            break;

        case 'l':
            inumber_table = malloc(sizeof(int) * INODE_TABLE_SIZE);

            searchResult = lookup(name, &inumber_table, &number_ofDirs, LOOKUP);

            if (searchResult >= 0)
                printf("Search: %s found\n", name);
            else
                printf("Search: %s not found\n", name);

            return searchResult;

        case 'd':
            printf("Delete: %s\n", name);
            return delete(name);

        case 'm':
            printf("Move: %s to %s\n", name, type);
            return move(name, type);

        case 'p':
            printf("Print content to %s\n", name);
            return print(name);

        default: {
            fprintf(stderr, "Error: command to apply\n");
            return FAIL;
        }
    }
}




void * getCommunication(void * ptr) {

    while (TRUE) {
        struct sockaddr_un client_addr;
        socklen_t addrlen;
        char buffer[MAX_INPUT_SIZE];
        int c, res;

        addrlen = sizeof(struct sockaddr_un);

        c = recvfrom(server_sockfd, buffer, sizeof(buffer)-1, 0,
            (struct sockaddr *) &client_addr, &addrlen);
        
        if (c <= 0){
            perror("Error receiving message\n");
            continue;
        }

        buffer[c]='\0';

        res = apply_command(buffer);

        if (sendto(server_sockfd, &res, sizeof(int), 0, 
               (struct sockaddr *) &client_addr, addrlen) < 0) {

            perror("Error sending message\n");
            continue;
        }
    }

    return NULL;
}
