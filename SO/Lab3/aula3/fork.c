#include <stdio.h>

int Value = 0;

void thr_func(){
   Value = 1;
   return NULL;
}


int main(){
   int pid, aux;
   pid = fork();
   
   if(pid == 0){
       thr_func();
   } else{
       wait(&aux);
       Value = 2;
   }
	
   printf("Value=%d\n",Value);
   return 0;
}
