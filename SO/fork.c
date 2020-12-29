#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int filho(){
    int i;
    for(i=0; i<50; i++){
        printf("(%d) -> %d\n", getpid(), i);
    }
    return i;
}

int pai(){
    int i;
    for(i=1000; i<1050; i++){
        printf("(%d) -> %d\n", getpid(), i);
    }
    return i;
}

int main(){
    int pid, aux;

    pid = fork();

    if(pid == 0){
        pid = filho();
        exit(pid);
    } else{
        pid = pai();
        wait(&aux);;
        if(WIFEXITED(aux)){
            printf("Soma: %d\n", pid+WEXITSTATUS(aux));
        }
    }

    return 0;
}