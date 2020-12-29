#include "tecnicofs-client-api.h"
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <stdio.h>
#include <sys/stat.h>
#include <errno.h>
#include "../tecnicofs-api-constants.h"

#define CLIENT_PATH "/tmp/clientTFS_socket"
#define MAX_INPUT_SIZE 100

int client_sockfd;
socklen_t server_addrlen;
struct sockaddr_un server_addr;
char client_socket_name[MAX_INPUT_SIZE];


int setSockAddrUn(char *path, struct sockaddr_un *addr) {

  if (addr == NULL)
    return 0;

  bzero((char *)addr, sizeof(struct sockaddr_un));
  addr->sun_family = AF_UNIX;
  strcpy(addr->sun_path, path);

  return SUN_LEN(addr);
}


int tfsCommunicate(char * message, int * res) {

  
  if (sendto(client_sockfd, message, strlen(message)+1, 0,
            (struct sockaddr *) &server_addr, server_addrlen) < 0) {

    return -1;
  }

  if (recvfrom(client_sockfd, res, sizeof(int), 0, 0, 0) < 0) return -1;

  return 0;
}



int tfsCreate(char *filename, char nodeType) {
  char command[MAX_INPUT_SIZE];
  char message[MAX_INPUT_SIZE];
  int res;

  if (sprintf(command, "c %s %c", filename, nodeType) < 0) return -1;
  strcpy(message, command);

  if (tfsCommunicate(message, &res) < 0) return -1;

	return res;
}


int tfsDelete(char *path) {
  char command[MAX_INPUT_SIZE];
  char message[MAX_INPUT_SIZE];
  int res;

  if (sprintf(command, "d %s", path) < 0) return -1;
  strcpy(message, command);

  if (tfsCommunicate(message, &res) < 0) return -1;

  return res;
}



int tfsMove(char *from, char *to) {
  char command[MAX_INPUT_SIZE];
  char message[MAX_INPUT_SIZE];
  int res;

  if (sprintf(command, "m %s %s", from, to) < 0) return -1;
  strcpy(message, command);

  if (tfsCommunicate(message, &res) < 0) return -1;

  return res;
}




int tfsLookup(char *path) {
  char command[MAX_INPUT_SIZE];
  char message[MAX_INPUT_SIZE];
  int res;

  if (sprintf(command, "l %s", path) < 0) return -1;
  strcpy(message, command);

  if (tfsCommunicate(message, &res) < 0) return -1;

  return res;
}



int tfsPrint(char *filename) {
  char command[MAX_INPUT_SIZE];
  char message[MAX_INPUT_SIZE];
  int res;

  if (sprintf(command, "p %s", filename) < 0) return -1;
  strcpy(message, command);

  if (tfsCommunicate(message, &res) < 0) return -1;

  return res;
}





int tfsMount(char * serverName) {

	struct sockaddr_un client_addr;
  socklen_t addrlen;

  /* Create client socket and associate a constant defined name to it */

  if ((client_sockfd = socket(AF_UNIX, SOCK_DGRAM, 0)) < 0) return -1;

  if (sprintf(client_socket_name, "%s-%d", CLIENT_PATH, getpid()) < 0) return -1;

	if ((addrlen = setSockAddrUn(client_socket_name, &client_addr)) <= 0) return -1;

  if (bind(client_sockfd, (struct sockaddr *) &client_addr, addrlen) < 0) return -1;

  if (chmod(client_socket_name, 00222) == -1) return -1;
  

  /* Save server socket name and length */
  if ((server_addrlen = setSockAddrUn(serverName, &server_addr)) <= 0) return -1;

	return 0;
}



int tfsUnmount() {

	if (close(client_sockfd) < 0) {
    perror("Error closing socket.");
    exit(EXIT_FAILURE);
  }
  
  if (unlink(client_socket_name) < 0) return -1;

  return 0;
}
