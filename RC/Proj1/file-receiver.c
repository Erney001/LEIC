#include "packet-format.h"
#include <arpa/inet.h>
#include <limits.h>
#include <netinet/in.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <unistd.h>
#include <string.h> // for memset

int main(int argc, char *argv[]) {
  char *file_name = argv[1];
  int port = atoi(argv[2]);
  int wnd_size = atoi(argv[3]); // window size

  FILE *file = fopen(file_name, "w");
  if (!file) {
    perror("fopen");
    exit(EXIT_FAILURE);
  }

  // Prepare server socket.
  int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
  if (sockfd == -1) {
    perror("socket");
    exit(EXIT_FAILURE);
  }

  // Allow address reuse so we can rebind to the same port,
  // after restarting the server.
  if (setsockopt(sockfd, SOL_SOCKET, SO_REUSEADDR, &(int){1}, sizeof(int)) < 0) {
    perror("setsockopt");
    exit(EXIT_FAILURE);
  }

  struct sockaddr_in srv_addr = {
      .sin_family = AF_INET,
      .sin_addr.s_addr = htonl(INADDR_ANY),
      .sin_port = htons(port),
  };

  if (bind(sockfd, (struct sockaddr *)&srv_addr, sizeof(srv_addr))) {
    perror("bind");
    exit(EXIT_FAILURE);
  }
  fprintf(stderr, "Receiving on port: %d\n", port);

  if(wnd_size <= 0 || wnd_size > 32){
    perror("windowsize");
    exit(EXIT_FAILURE);
  }

  // configure for timeouts
  struct timeval tv;
  tv.tv_sec = 4;
  tv.tv_usec = 0;
  setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));

  struct sockaddr_in src_addr_original;

  int continuar = 1;
  int base = 0;
  int recebidos = 0;
  int recebido = 0;
  int timeout = 0;
  int last_rounds = 0;
  int final_pkt = -1;
  int first = 1;

  while(continuar == 1 || last_rounds > 0){
    printf("S new cycle\n");
    recebido = 0;
    struct sockaddr_in src_addr;
    data_pkt_t data_pkt;
    ssize_t len;

    len = recvfrom(sockfd, &data_pkt, sizeof(data_pkt), 0, (struct sockaddr *)&src_addr, &(socklen_t){sizeof(src_addr)});

    if(first == 1){
      src_addr_original = src_addr;
      first = 0;
    } else{ // compare addresses and ports
      if( (src_addr_original.sin_addr.s_addr != src_addr.sin_addr.s_addr) || (src_addr_original.sin_port != src_addr.sin_port) ){
        printf("S different addr or port\n");
        break;
      }
    }

    if(len < 0){
      last_rounds = 0;
      timeout++;
      printf("S timeout %d\n", timeout);

    } else{
      int num = (int) ntohl(data_pkt.seq_num);
      recebido = 1;
      timeout = 0;
      continuar = 1;
      printf("Received segment %d, size %ld.\n", ntohl(data_pkt.seq_num), len);

      if(last_rounds > 0){
        last_rounds = 0;
        tv.tv_sec = 4;
        setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));
      }

      if(num < base+wnd_size && num >= base){ // se estiver dentro da janela
        printf("S inside receiver window\n");
        if(!(recebidos & (1<<num))){ // write to file
          recebidos = recebidos | (1<<num);
          fseek(file, ntohl(data_pkt.seq_num)*1000, SEEK_SET);
          fwrite(data_pkt.data, 1, len - offsetof(data_pkt_t, data), file);
          printf("S wrote to file, %ld\n", len-offsetof(data_pkt_t, data));

          if(len-offsetof(data_pkt_t, data) < 1000){
            printf("S %d it's final packet\n", num);
            final_pkt = num;
          }
        }

        if(num == base){
          base++;
        }

        printf("S old base: %d\n", base);

        for(int i=base; i<base+wnd_size; i++){ // finds new base
          printf("S recebidos: %d, test: %d, res: %d\n", recebidos, (1<<i), !(recebidos & (1<<i)));
          if(!(recebidos & (1<<i))){
            printf("S i:%d\n", i);
            base = i; 
            break;
          }
        }

        printf("S new base: %d\n", base);

        int dist;
        int sel_acks = 0;
        for(int u=base; u<base+wnd_size; u++){ // marcar sel acks
          printf("S check sel acks\n");
          if(recebidos & (1<<u)){
            printf("S marked sel ack %d\n", u);
            dist = u - base - 1;
            sel_acks = sel_acks | (1<<dist);
          }
        }

        printf("S sel acks: %d\n", sel_acks);

        ack_pkt_t ack_pkt;
        ack_pkt.seq_num = htonl(base);
        ack_pkt.selective_acks = sel_acks;
        ssize_t sent_len = sendto(sockfd, &ack_pkt, sizeof(ack_pkt), 0, (struct sockaddr *)&src_addr, sizeof(src_addr));
        printf("Sending ACK %d, size %ld.\n", ntohl(ack_pkt.seq_num), sent_len);

      } else{ // se estiver fora da janela
        printf("S packet outside receiver window\n");

        int dist;
        int sel_acks = 0;
        for(int u=base; u<base+wnd_size; u++){ // marcar sel acks
          printf("S check sel acks\n");
          if(recebidos & (1<<u)){
            printf("S marked sel ack %d\n", u);
            dist = u - base - 1;
            sel_acks = sel_acks | (1<<dist);
          }
        }

        ack_pkt_t ack_pkt;
        ack_pkt.seq_num = htonl(base);
        ack_pkt.selective_acks = sel_acks;
        ssize_t sent_len = sendto(sockfd, &ack_pkt, sizeof(ack_pkt), 0, (struct sockaddr *)&src_addr, sizeof(src_addr));
        printf("Sending ACK %d, size %ld.\n", ntohl(ack_pkt.seq_num), sent_len);
      }

    } // end if-else

    if(final_pkt != -1){ // checks if everything received
      continuar = 0;
      for(int i=0; i<final_pkt; i++){
        if(!(recebidos & (1<<i))){
          continuar = 1;
          break;
        }
      }
    }

    if(continuar == 0 && recebido == 0){
      printf("S final round\n");
      continuar = 0;
    } else if (continuar == 0 && recebido == 1){
      printf("S going for last rounds\n");
      last_rounds = 1;
      tv.tv_sec = 2;
      setsockopt(sockfd, SOL_SOCKET, SO_RCVTIMEO, &tv, sizeof(tv));
    } else if(continuar == 1 && timeout >= 1){
      printf("S max iters\n");
      remove(file_name);
      exit(EXIT_FAILURE);
    }

    printf("S continuar: %d\n", continuar);

  } // end while

  printf("S server ended\n");

  close(sockfd);
  fclose(file);

  return EXIT_SUCCESS;
}
