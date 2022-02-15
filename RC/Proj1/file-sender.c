#include "packet-format.h"
#include <limits.h>
#include <netdb.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h> // for memset

int main(int argc, char *argv[]) {
  char *file_name = argv[1];
  char *host = argv[2];
  int port = atoi(argv[3]);
  int wnd_size = atoi(argv[4]); // window size

  FILE *file = fopen(file_name, "r");
  if (!file) {
    perror("fopen");
    exit(EXIT_FAILURE);
  }

  // Prepare server host address.
  struct hostent *he;
  if (!(he = gethostbyname(host))) {
    perror("gethostbyname");
    exit(EXIT_FAILURE);
  }

  struct sockaddr_in srv_addr = {
      .sin_family = AF_INET,
      .sin_port = htons(port),
      .sin_addr = *((struct in_addr *)he->h_addr),
  };

  int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
  if (sockfd == -1) {
    perror("socket");
    exit(EXIT_FAILURE);
  }

  if(wnd_size <= 0 || wnd_size > 32){
    perror("windowsize");
    exit(EXIT_FAILURE);
  }

  // configure for timeouts
  struct timeval tv;
  tv.tv_sec = 1;
  tv.tv_usec = 0;
  setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

  // how many chunks are needed?
  fseek(file, 0, SEEK_END);
  int size = ftell(file)/1000 + 1;

  printf("C size: %d\n", size);

  int ACKed[size];
  memset(ACKed, 0, size*sizeof(int));

  struct sockaddr_in srv_addr_original;

  int continuar = 1;
  int base = 0;
  int nextnum = 0;
  int timeouts = 0;
  int first = 1;

  while(continuar == 1){
    printf("C new cycle\n");
    for(int i=nextnum; i<base+wnd_size && i<size; i++){ // sends everything available to send on window
      data_pkt_t data_pkt;
      size_t data_len;
      
      data_pkt.seq_num = htonl(i);
      fseek(file, ntohl(data_pkt.seq_num)*1000, SEEK_SET);
      data_len = fread(data_pkt.data, 1, sizeof(data_pkt.data), file);
      ssize_t sent_len = sendto(sockfd, &data_pkt, offsetof(data_pkt_t, data) + data_len, 0, (struct sockaddr *)&srv_addr, sizeof(srv_addr));
      printf("Sending segment %d, size %ld.\n", ntohl(data_pkt.seq_num), offsetof(data_pkt_t, data) + data_len);

      nextnum++; 

      if (sent_len != offsetof(data_pkt_t, data) + data_len) {
        fprintf(stderr, "Truncated packet.\n");
        exit(EXIT_FAILURE);
      }

    } // end for

    ack_pkt_t ack_pkt;
    ssize_t len;
    len = recvfrom(sockfd, &ack_pkt, sizeof(ack_pkt), 0, (struct sockaddr *)&srv_addr, &(socklen_t){sizeof(srv_addr)});

    if(first == 1){
      srv_addr_original = srv_addr;
      first = 0;
    } else{ // compare addresses and ports
      if( (srv_addr_original.sin_addr.s_addr != srv_addr.sin_addr.s_addr) || (srv_addr_original.sin_port != srv_addr.sin_port) ){
        printf("C different addr or port\n");
        break;
      }
    }

    if(len < 0){
      timeouts++;
      printf("C timeout\n");

      if(timeouts == 3){
        perror("timeouts");
        exit(EXIT_FAILURE);
      }

      for(int i=base; i<nextnum; i++){ // resend unACKed packets after timeout
        printf("C resending unACKed packets in window\n");
        if(!ACKed[i]){
          data_pkt_t data_pkt;
          size_t data_len;
          
          data_pkt.seq_num = htonl(i);
          fseek(file, ntohl(data_pkt.seq_num)*1000, SEEK_SET);
          data_len = fread(data_pkt.data, 1, sizeof(data_pkt.data), file);
          ssize_t sent_len = sendto(sockfd, &data_pkt, offsetof(data_pkt_t, data) + data_len, 0, (struct sockaddr *)&srv_addr, sizeof(srv_addr));
          printf("Sending segment %d, size %ld.\n", ntohl(data_pkt.seq_num), offsetof(data_pkt_t, data) + data_len);

          if (sent_len != offsetof(data_pkt_t, data) + data_len) {
            fprintf(stderr, "Truncated packet.\n");
            exit(EXIT_FAILURE);
          }
        }
      }

    } else{
      int old_base = base;
      base = (int) ntohl(ack_pkt.seq_num);
      timeouts = 0;

      printf("Received ack %d, size %ld.\n", base, len);

      for(int i=old_base; i<base; i++){ // updates state received from cumulative ACK
        ACKed[i] = 1;
      }

      int ack_num = ack_pkt.selective_acks;
      int var;
      printf("C ack num: %d\n", ack_num);

      if(ack_num != 0){
        for(int u=0; u<size && u<wnd_size-1; u++){ // analyze sel_acks for Selective Repeat
          var = 0;
          var = 1<<u;
          printf("C ack num: %d var: %d\n", ack_num, var);
          if(ack_num & var){
            ACKed[base+1+u] = 1;
            printf("C pkt %d ACKed\n", base+1+u);
          }
        }
      }

    } // end if-else

    if(base == size){
      continuar = 0;
    }

    printf("C continuar: %d\n", continuar);

  } // end while

  printf("C client ended\n");

  // Clean up and exit.
  close(sockfd);
  fclose(file);

  return EXIT_SUCCESS;
}
