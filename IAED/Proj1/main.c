/*  Projeto 1 - IAED - 19/20
    By: Afonso Ferreira - 96832 */


#include <stdio.h>
#include <string.h>


#define MAX_PRODUCTS 10000
#define MAX_ORDERS 500
#define MAX_DESC_LEN 63
#define MAX_PRODUCTS_IN_ORDER 200
#define MAX_ORDER_WEIGHT 200
#define NOTHING_HERE -1 /* define a nao existencia de produtos ou encomendas */


typedef struct {
    int id;
    char description[MAX_DESC_LEN];
    int price;
    int weight;
    int stock;
} Product;


/* Cada composicao guarda o id do produto e a quantidade desse produto presente na encomenda */
typedef struct {
    int product_id_in_order;
    int amount_of_product_in_order;  
} Composition;


/* Cada encomenda e constituida por um id e uma composicao de produtos */
typedef struct {
    int id;
    Composition order_composition_list[MAX_PRODUCTS_IN_ORDER];
} Order;


void start_products_list(Product products_list[MAX_PRODUCTS], int dim, int num);
void start_orders_list(Order orders_list[MAX_ORDERS], int dim, int num);
int weight(Product products_list[MAX_PRODUCTS], Composition order_composition_list[MAX_PRODUCTS_IN_ORDER]);
int find_product_in_order(Composition order_composition_list[MAX_PRODUCTS_IN_ORDER], int product_id);
int find_free_spot(Composition order_composition_list[MAX_PRODUCTS_IN_ORDER]);
int find_free_spot_products_list(Product products_list[MAX_PRODUCTS]);
void merge_sort(Product products_list[MAX_PRODUCTS], int left, int right);
void merge(Product products_list[MAX_PRODUCTS], int left, int mid, int right);
void merge_sort_2(Product products_list[MAX_PRODUCTS], Composition order_composition_list[MAX_PRODUCTS_IN_ORDER], int left, int right);
void merge_2(Product products_list[MAX_PRODUCTS], Composition order_composition_list[MAX_PRODUCTS_IN_ORDER], int left, int mid, int right);


void add_product(Product products_list[MAX_PRODUCTS]);
void add_stock(Product products_list[MAX_PRODUCTS]);
void add_order(Order orders_list[MAX_ORDERS]);
void add_product_to_order(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]);
void remove_stock(Product products_list[MAX_PRODUCTS]);
void remove_product_in_order(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]);
void order_cost(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]);
void change_price(Product products_list[MAX_PRODUCTS]);
void list_product_in_order(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]);
void list_order_id_with_max_product(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]);
void sort_by_price(Product products_list[MAX_PRODUCTS], int left, int right);
void sort_alphabetically(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]);


/* Ciclo principal - inicia vetores de produtos e encomendas,
   le um char do stdin e chama a funcao correspondente ate terminar */
int main(){
    Product products_list[MAX_PRODUCTS];
    Order orders_list[MAX_ORDERS];
    char cmd;

    start_products_list(products_list, MAX_PRODUCTS, NOTHING_HERE);
    start_orders_list(orders_list, MAX_ORDERS, NOTHING_HERE);

    while((cmd = getchar()) != 'x'){
        switch(cmd){
            case 'a':
                add_product(products_list);
                break;
            case 'q':
                add_stock(products_list);
                break;
            case 'N':
                add_order(orders_list);
                break;
            case 'A':
                add_product_to_order(products_list, orders_list);
                break;
            case 'r':
                remove_stock(products_list);
                break;
            case 'R':
                remove_product_in_order(products_list, orders_list);
                break;
            case 'C':
                order_cost(products_list, orders_list);
                break;
            case 'p':
                change_price(products_list);
                break;
            case 'E':
                list_product_in_order(products_list, orders_list);
                break;
            case 'm':
                list_order_id_with_max_product(products_list, orders_list);
                break;
            case 'l':
                sort_by_price(products_list, 0, find_free_spot_products_list(products_list));
                break;
            case 'L':
                sort_alphabetically(products_list, orders_list);
                break;
            default:
            break;
        }
    }

    return 0;
}


/*  *   *   *   *   Funcoes auxiliares  *   *   *   *   */


/* Inicia a lista de produtos com os id's a NOTHING_HERE = -1 */
void start_products_list(Product products_list[MAX_PRODUCTS], int dim, int num){
    int i;
    
    for(i=0;i<dim;i++){
        products_list[i].id = num;
    }
}


/* Inicia a lista de encomendas com os id's a NOTHING_HERE = -1
   e os id's dos produtos em cada encomenda tambem a NOTHING_HERE = -1 */
void start_orders_list(Order orders_list[MAX_PRODUCTS], int dim, int num){
    int i, j;
    
    for(i=0;i<dim;i++){
        orders_list[i].id = num;
        for(j=0;j<MAX_PRODUCTS_IN_ORDER;j++){
            orders_list[i].order_composition_list[j].product_id_in_order = num;
        }
    }
}


/* Calcula o peso de uma encomenda e retorna-o */
int weight(Product products_list[MAX_PRODUCTS], Composition order_composition_list[MAX_PRODUCTS_IN_ORDER]){
    int i, product_id_aux, weight = 0;

    /* Percorre a encomenda e adiciona ao peso total o peso dos produtos na encomenda
        que realmente existem multiplicados pela sua quantidade na encomenda */
    for(i=0;i<MAX_PRODUCTS_IN_ORDER;i++){
        product_id_aux = order_composition_list[i].product_id_in_order;
        if(product_id_aux != NOTHING_HERE){
            weight += order_composition_list[i].amount_of_product_in_order * products_list[product_id_aux].weight;
        }
    }

    return weight;
}


/* Devolve o indice de um produto numa encomenda, se existir, ou NOTHING_HERE = -1, caso contrario */
int find_product_in_order(Composition order_composition_list[MAX_PRODUCTS_IN_ORDER], int product_id){
    int i;

    for(i=0;i<MAX_PRODUCTS_IN_ORDER;i++){
        if(order_composition_list[i].product_id_in_order == product_id){
            return i;
        }
    }

    return NOTHING_HERE;
}


/* Se existir, devolve a primeira posicao livre, id do produto = NOTHING_HERE = -1,
   na lista de produtos de uma encomenda ou NOTHING_HERE, caso contrario */
int find_free_spot(Composition order_composition_list[MAX_PRODUCTS_IN_ORDER]){
    int i;

    for(i=0;i<MAX_PRODUCTS_IN_ORDER;i++){
        if(order_composition_list[i].product_id_in_order == NOTHING_HERE){
            return i;
        }
    }

    return NOTHING_HERE;
}


/* Se existir, devolve a primeira posicao livre, id do produto = NOTHING_HERE = -1
   numa lista de produtos ou NOTHING_HERE, caso contrario */
int find_free_spot_products_list(Product products_list[MAX_PRODUCTS]){
    int i;

    for(i=0;i<MAX_PRODUCTS;i++){
        if(products_list[i].id == NOTHING_HERE){
            return i;
        }
    }

    return NOTHING_HERE;
}


/*  *   *   *   *   Funcoes do projeto  *   *   *   *   */


/* Adiciona um produto a lista de produtos, se cumprir os requisitos e ainda houver espaco */
void add_product(Product products_list[MAX_PRODUCTS]){
    static int product_id = 0; /* variavel estatica que define o id do produto */
    char desc[MAX_DESC_LEN];
    int price, weight, stock;

    scanf(" %[^:]:%d:%d:%d", desc, &price, &weight, &stock);

    if(product_id < MAX_PRODUCTS && strcmp(desc, "") != 0 && strlen(desc) <= MAX_DESC_LEN && price >= 0 && weight >= 0 && stock >= 0){
        products_list[product_id].id = product_id;
        strcpy(products_list[product_id].description, desc);
        products_list[product_id].price = price;
        products_list[product_id].weight = weight;
        products_list[product_id].stock = stock;

        printf("Novo produto %d.\n", product_id++);
    }
}


/* Adiciona stock de um produto, caso ele ja exista */
void add_stock(Product products_list[MAX_PRODUCTS]){
    int id, stock;

    scanf(" %d:%d", &id, &stock);

    if(products_list[id].id == NOTHING_HERE){
        printf("Impossivel adicionar produto %d ao stock. Produto inexistente.\n", id);
    } else{
        products_list[id].stock += stock;
    }
}


/* Cria uma nova encomenda, se ainda houver espaco */
void add_order(Order orders_list[MAX_ORDERS]){
    static int order_id = 0; /* variavel estatica que define o id da encomenda */

    if(order_id < MAX_ORDERS){
        orders_list[order_id].id = order_id;

        printf("Nova encomenda %d.\n", order_id++);
    }
}


/* Adiciona um produto a uma encomenda na quantidade pretendida e
   retira essa quantidade da lista de produtos, caso nao haja erros */
void add_product_to_order(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]){
    int product_id, order_id, amount, index;

    scanf(" %d:%d:%d", &order_id, &product_id, &amount);

    if(orders_list[order_id].id == NOTHING_HERE){
        printf("Impossivel adicionar produto %d a encomenda %d. Encomenda inexistente.\n", product_id, order_id);
    } else if(products_list[product_id].id == NOTHING_HERE){
        printf("Impossivel adicionar produto %d a encomenda %d. Produto inexistente.\n", product_id, order_id);
    } else if(products_list[product_id].stock < amount){
        printf("Impossivel adicionar produto %d a encomenda %d. Quantidade em stock insuficiente.\n",  product_id, order_id);
    } else if((weight(products_list, orders_list[order_id].order_composition_list) + amount * products_list[product_id].weight) > MAX_ORDER_WEIGHT){
        printf("Impossivel adicionar produto %d a encomenda %d. Peso da encomenda excede o maximo de 200.\n",  product_id, order_id);
    } else if((index = find_product_in_order(orders_list[order_id].order_composition_list, product_id)) != NOTHING_HERE){
        /* Se o produto ja existe na encomenda, adicionamos mais stock a encomenda */
        orders_list[order_id].order_composition_list[index].amount_of_product_in_order += amount;
        products_list[product_id].stock -= amount;
    } else if((index = find_free_spot(orders_list[order_id].order_composition_list)) != NOTHING_HERE){
        /* Se o produto ainda nao existe na encomenda, criamos-o na encomenda */
        orders_list[order_id].order_composition_list[index].product_id_in_order = product_id;
        orders_list[order_id].order_composition_list[index].amount_of_product_in_order = amount;
        products_list[product_id].stock -= amount;
    }   
}


/* Retira stock a um produto, se ele existir em quantidade suficiente na lista de produtos */
void remove_stock(Product products_list[MAX_PRODUCTS]){
    int product_id, amount;

    scanf(" %d:%d", &product_id, &amount);

    if(products_list[product_id].id == NOTHING_HERE){
        printf("Impossivel remover stock do produto %d. Produto inexistente.\n", product_id);
    } else if(products_list[product_id].stock < amount){
        printf("Impossivel remover %d unidades do produto %d do stock. Quantidade insuficiente.\n", amount, product_id);
    } else{
        products_list[product_id].stock -= amount;
    }
}


/* Remove um produto de uma encomenda, caso isso seja possivel */
void remove_product_in_order(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]){
    int order_id, product_id, index, index_aux;

    scanf(" %d:%d", &order_id, &product_id);

    if(orders_list[order_id].id == NOTHING_HERE){
        printf("Impossivel remover produto %d a encomenda %d. Encomenda inexistente.\n", product_id, order_id);
    } else if(products_list[product_id].id == NOTHING_HERE){
        printf("Impossivel remover produto %d a encomenda %d. Produto inexistente.\n", product_id, order_id);
    } else if((index = find_product_in_order(orders_list[order_id].order_composition_list, product_id)) != NOTHING_HERE){
        /* Para eliminar o produto pretendido, passamos o ultimo produto da encomenda para a posicao
           onde esta o produto que vai ser eliminado e colocamos a posicao onde estava o ultimo
           produto como inexistente, ou seja, id do produto nessa posicao a NOTHING_HERE = -1 */
        products_list[product_id].stock += orders_list[order_id].order_composition_list[index].amount_of_product_in_order;
        index_aux = find_free_spot(orders_list[order_id].order_composition_list);
        orders_list[order_id].order_composition_list[index].product_id_in_order = orders_list[order_id].order_composition_list[index_aux-1].product_id_in_order;
        orders_list[order_id].order_composition_list[index].amount_of_product_in_order = orders_list[order_id].order_composition_list[index_aux-1].amount_of_product_in_order;
        orders_list[order_id].order_composition_list[index_aux-1].product_id_in_order = NOTHING_HERE;
    }
}


/* Calcula o custo de uma encomenda, se ela existir */
void order_cost(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]){
    int order_id, i, aux, stock, product_id, price, cost = 0;

    scanf(" %d", &order_id);

    if(orders_list[order_id].id == NOTHING_HERE){
        printf("Impossivel calcular custo da encomenda %d. Encomenda inexistente.\n", order_id);
    } else{
        /* aux = indice da primeira posicao livre */
        aux = find_free_spot(orders_list[order_id].order_composition_list);
        for(i=0;i<aux;i++){
            product_id = orders_list[order_id].order_composition_list[i].product_id_in_order;
            if(product_id != NOTHING_HERE){
                price = products_list[product_id].price;
                stock = orders_list[order_id].order_composition_list[i].amount_of_product_in_order;
                cost += price * stock;
            }
        }
        printf("Custo da encomenda %d %d.\n", order_id, cost);
    }
}


/* Altera o preco de um produto dado o seu id, se ele existir */
void change_price(Product products_list[MAX_PRODUCTS]){
    int product_id, price;

    scanf(" %d:%d", &product_id, &price);

    if(products_list[product_id].id == NOTHING_HERE){
        printf("Impossivel alterar preco do produto %d. Produto inexistente.\n", product_id);
    } else{
        products_list[product_id].price = price;
    }
}


/* Lista a quantidade de um produto numa encomenda, caso ambos existam;
   Se o produto nao extiver na encomenda, printa 0 */
void list_product_in_order(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]){
    int order_id, product_id, amount;
    char desc[MAX_DESC_LEN];

    scanf(" %d:%d", &order_id, &product_id);

    if(orders_list[order_id].id == NOTHING_HERE){
        printf("Impossivel listar encomenda %d. Encomenda inexistente.\n", order_id);
    } else if(products_list[product_id].id == NOTHING_HERE){
        printf("Impossivel listar produto %d. Produto inexistente.\n", product_id);
    } else{
        strcpy(desc, products_list[product_id].description);
        if(find_product_in_order(orders_list[order_id].order_composition_list, product_id) == NOTHING_HERE){
            printf("%s %d.\n", desc, 0);
        } else{
            amount = orders_list[order_id].order_composition_list[find_product_in_order(orders_list[order_id].order_composition_list, product_id)].amount_of_product_in_order;
            printf("%s %d.\n", desc, amount);
        }
    }
}


/* Lista o id da encomenda que possui maior quantidade do produto com id dado */
void list_order_id_with_max_product(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]){
    int product_id, i, j, max_stock, max_order_id = NOTHING_HERE, first = 1;
    Composition temp;

    scanf(" %d", &product_id);

    if(products_list[product_id].id == NOTHING_HERE){
        printf("Impossivel listar maximo do produto %d. Produto inexistente.\n", product_id);
    } else{
        /* Percorre as encomendas que existem */
        for(i=0;i<MAX_ORDERS;i++){
            if(orders_list[i].id != NOTHING_HERE){
                /* E todos os produtos nessas encomendas que existam e tenham o id pretendido */
                for(j=0;j<MAX_PRODUCTS_IN_ORDER;j++){
                    temp = orders_list[i].order_composition_list[j];
                    if(temp.product_id_in_order != NOTHING_HERE && temp.product_id_in_order == product_id){
                        /* Se for a primeira occorencia do produto numa encomenda, guarda-se os valores */
                        if(first == 1){
                            max_stock = temp.amount_of_product_in_order;
                            max_order_id = i;
                            first = 0;
                        } else if(temp.amount_of_product_in_order > max_stock){
                            max_stock = temp.amount_of_product_in_order;
                            max_order_id = i;
                        }
                    }
                }
            }
        }
        /* Caso exista uma encomenda com maior quantidade desse produto, printa-se o resultado */
        if(max_order_id != NOTHING_HERE){
            printf("Maximo produto %d %d %d.\n", product_id, max_order_id, max_stock);
        }
    }
}


/* Printa os produtos ordenados por preco, apos criar um vetor
   auxiliar igual ao original e aplicar-lhe merge sort */
void sort_by_price(Product products_list[MAX_PRODUCTS], int left, int right){
    int i;
    Product product, products_list_aux[MAX_PRODUCTS];

    for(i=left;i<right;i++){
        products_list_aux[i] = products_list[i];
    }

    merge_sort(products_list_aux, left, right-1);

    printf("Produtos\n");
    for(i=left;i<right;i++){
        product = products_list_aux[i];
        printf("* %s %d %d\n", product.description, product.price, product.stock);
    }
}


/* Merge sort, funcao principal que parte recursivamente o vetor
   e depois aplica uma funcao para os juntar na ordem esperada */
void merge_sort(Product products_list[MAX_PRODUCTS], int left, int right){
    int mid = (left + right) / 2;

    if(right <= left){
        return;
    } else{
        merge_sort(products_list, left, mid);
        merge_sort(products_list, mid+1, right);
        merge(products_list, left, mid, right);
    }
}


/* Funcao que funde os vetores auxiliares por ordem crescente de preco, ou caso
   os precos sejam iguais, por ordem crescente de id's */
void merge(Product products_list[MAX_PRODUCTS], int left, int mid, int right){
    int i, j, k;
    Product products_list_aux[MAX_PRODUCTS];

    for(i=mid+1;i>left;i--){
        products_list_aux[i-1] = products_list[i-1];
    }
    for(j=mid;j<right;j++){
        products_list_aux[right+mid-j] = products_list[j+1];
    }
    for(k=left;k<=right;k++){
        if(products_list_aux[j].price < products_list_aux[i].price){
            products_list[k] = products_list_aux[j--];
        } else if(products_list_aux[j].price == products_list_aux[i].price && products_list_aux[j].id < products_list_aux[i].id){
            products_list[k] = products_list_aux[j--];
        } else if(products_list_aux[j].price == products_list_aux[i].price && products_list_aux[j].id > products_list_aux[i].id){
            products_list[k] = products_list_aux[i++];
        } else{
            products_list[k] = products_list_aux[i++];
        }
    }
}


/* Ordena alfabeticamente os produtos numa encomenda, se ela existir */
void sort_alphabetically(Product products_list[MAX_PRODUCTS], Order orders_list[MAX_ORDERS]){
    int order_id, i, left = 0, right;
    Composition order_composition_list_aux[MAX_PRODUCTS_IN_ORDER], aux;

    scanf(" %d", &order_id);
    
    if(orders_list[order_id].id == NOTHING_HERE){
        printf("Impossivel listar encomenda %d. Encomenda inexistente.\n", order_id);
    } else{
        /* Calcula a primeira posicao livre = posicao que limita o vetor pela direita */
        right = find_free_spot(orders_list[order_id].order_composition_list);

        /* Cria um vetor auxiliar */
        for(i=left;i<right;i++){
            order_composition_list_aux[i] = orders_list[order_id].order_composition_list[i];
        }

        /* Aplica merge_sort ao vetor auxiliar */
        merge_sort_2(products_list, order_composition_list_aux, left, right-1);

        printf("Encomenda %d\n", order_id);
        for(i=left;i<right;i++){
            aux = order_composition_list_aux[i];
            printf("* %s %d %d\n", products_list[aux.product_id_in_order].description, products_list[aux.product_id_in_order].price, aux.amount_of_product_in_order);
        }
    }
}


/* Merge sort 2, funcao principal que parte recursivamente o
   vetor e depois junta-os denovo na ordem esperada */
void merge_sort_2(Product products_list[MAX_PRODUCTS], Composition order_composition_list[MAX_PRODUCTS_IN_ORDER], int left, int right){
    int mid = (left + right) / 2;

    if(right <= left){
        return;
    } else{
        merge_sort_2(products_list, order_composition_list, left, mid);
        merge_sort_2(products_list, order_composition_list, mid+1, right);
        merge_2(products_list, order_composition_list, left, mid, right);
    }
}


/* Une os vetores auxiliares por ordem alfabetica */
void merge_2(Product products_list[MAX_PRODUCTS], Composition order_composition_list[MAX_PRODUCTS_IN_ORDER], int left, int mid, int right){
    int i, j, k, product_id_1, product_id_2;
    Composition order_composition_list_aux[MAX_PRODUCTS_IN_ORDER];

    for(i=mid+1;i>left;i--){
        order_composition_list_aux[i-1] = order_composition_list[i-1];
    }
    for(j=mid;j<right;j++){
        order_composition_list_aux[right+mid-j] = order_composition_list[j+1];
    }
    for(k=left;k<=right;k++){
        product_id_1 = order_composition_list_aux[j].product_id_in_order;
        product_id_2 = order_composition_list_aux[i].product_id_in_order;
        /* strcmp(a, b) devolve < 0, se a for alfabeticamente menor que b */
        if(strcmp(products_list[product_id_1].description, products_list[product_id_2].description) < 0){
            order_composition_list[k] = order_composition_list_aux[j--];
        } else{
            order_composition_list[k] = order_composition_list_aux[i++];
        }
    }
}