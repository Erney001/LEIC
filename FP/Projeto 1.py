# Nome: Afonso da Silva Ferreira / Numero: 96832

def eh_labirinto(t):  # funcao que verifica se e labirinto
    '''universal -> booleano
    Recebe um argumento de qualquer tipo e devolve True se for um labirinto e False caso contrário'''
    if type(t) == tuple and len(t) >= 3 and len(
            t[0]) >= 3:  # ou seja se e um tuplo e se tem no minimo uma dimensao de 3 por 3
        ver = True  # condicao de verificacao
        for i in range(len(t)):
            if type(t[i]) != tuple or len(t[i]) != len(
                    t[0]):  # verifica se todos os elementos do tuplo sao iguais em comprimento
                ver = False
            for e in t[i]:
                if type(e) != int and ver == True:  # e se todos sao numeros inteiros
                    ver = False
        if ver == True:  # caso a condicao de verificao ainda seja verdadeira, executa as instrucoes seguintes
            for i in range(len(t)):  # verifica todos os elementos do tuplo
                if i == 0 or i == (
                        len(t) - 1):  # no primeiro e no ultimo elementos verifica se so tem paredes ou seja so uns
                    for e in t[i]:
                        if e != 1 and ver == True:
                            ver = False
                else:  # nos restantes
                    for e in range(len(t[i])):
                        if e == 0 or e == (len(t[
                                                   i]) - 1):  # verifica se tem um na primeira e ultima posicao que representam a parede exterior
                            if t[i][e] != 1 and ver == True:
                                ver = False
                        else:  # e se so tem zeros e uns nas restantes posicoes
                            if t[i][e] != 1 and t[i][e] != 0 and ver == True:
                                ver = False
        return ver  # devolve um boolean
    else:
        return False


def eh_posicao(
        p):  # funcao utilizada para verificar se e posicao ou seja se e um tuplo com 2 elementos ambos inteiros e maiores ou iguais a zero
    '''universal -> booleano
    Recebe um argumento de qualquer tipo e devolve True se for uma posicao e False caso contrario'''
    if type(p) == tuple and len(p) == 2 and type(p[0]) == int and type(p[1]) == int and p[0] >= 0 and p[
        1] >= 0:  # ou seja
        return True
    else:
        return False


def eh_conj_posicoes(t):  # funcao de verificacao de um conjunto de posicoes
    '''universal -> booleano
    Recebe um argumento de qualquer tipo e devolve True se for um conjunto de posicoes unicas e False caso contrario'''
    if type(t) == tuple and len(t) >= 0:  # um conjunto de posicoes e um tuplo com zero ou mais elementos
        if len(t) == 0:  # quando tem zero elementos e um conjunto de posicoes
            return True
        else:  # quando tem mais
            ver = True
            pos_unicas = ()
            for i in t:  # apenas e um conjunto de posicoes se todos os seus elementos forem posicoes e se nao houver posicoes repetidas
                if eh_posicao(i) == True and (i not in pos_unicas) and ver == True:
                    pos_unicas += (i,)
                else:
                    ver = False
            return ver
    else:
        return False


def tamanho_labirinto(t):  # tamanho do labirinto
    '''labirinto -> tuplo
    Recebe um labirinto e devolve um tuplo de dois valores correspondentes aos tamanho do labirinto'''
    if eh_labirinto(t) == True:  # apenas pode obter o tamanho de labirinto validos
        return (len(t), len(t[0]))  # coordenadas x representam as colunas e coordenadas y representam as linhas
    else:
        raise ValueError("tamanho_labirinto: argumento invalido")


def eh_mapa_valido(t_maze, t_posicoes):  # verifica se um mapa e valido
    '''labirinto & conjunto de posicoes -> booleano
    Recebe um labirinto e um conjunto de posicoes
    Retorna True caso o conjunto de posicoes dadas nao sejam ocupadas por paredes dentro do labirinto
    E False caso contrario'''
    if eh_labirinto(t_maze) == True and eh_conj_posicoes(
            t_posicoes) == True:  # para isso tem que receber um labirinto e um conjunto de posicoes validos
        col = (tamanho_labirinto(t_maze)[0])
        lin = (tamanho_labirinto(t_maze)[1])
        res = True
        for i in t_posicoes:
            if i[0] > col or i[1] > lin and res == True:  # cada uma das posicoes deve estar dentro do labirinto
                res = False
            else:  # e nao deve ser uma parede
                if t_maze[i[0]][i[1]] == 1 and res == True:
                    res = False
        return res
    else:
        raise ValueError("eh_mapa_valido: algum dos argumentos e invalido")


def eh_posicao_livre(maze, conj_posicoes,
                     posicao):  # verifica se a posicao esta livre ou seja se nao e parede e nao esta ocupada por outra unidade
    '''labirinto & conjunto de posicoes & posicao -> booleano
    Recebe um labirinto, um conjunto de posicoes e uma posicao e devolve True se a posicao dada corresponde a uma posicao livre, ou seja, nao ocupada por paredes, nem por unidades'''
    # para isso tem que receber um labirinto, um conjunto de posicoes validos que representam um mapa valido e tambem uma posicao valida
    if eh_labirinto(maze) == True and eh_conj_posicoes(conj_posicoes) == True and eh_mapa_valido(maze,
                                                                                                 conj_posicoes) == True and eh_posicao(
            posicao) == True:
        if (posicao not in conj_posicoes) and maze[posicao[0]][posicao[
            1]] != 1:  # a posicao esta livre se nao for ocupada por uma unidade, representada no conjunto de posicoes
            return True
        else:
            return False
    else:
        raise ValueError("eh_posicao_livre: algum dos argumentos e invalido")


def posicoes_adjacentes(pos):  # funcao para obter todas as posicoes adjacentes a uma outra posicao
    '''posicao -> conjunto de posicoes
    Recebe um posicao e retorna o conjunto de posicoes adjacentes a essa posicao pela ordem de leitura do labirinto'''
    if eh_posicao(pos) == True:  # para isso tem que receber uma posicao valida
        x = pos[0]  # representa as colunas
        y = pos[1]  # representa as linhas
        pos_adj = ()
        if x == 0 and y == 0:  # se a posicao for do genero (0, 0) ou seja o canto superior esquerdo
            pos_adj = ((x + 1, y), (x, y + 1))
        elif x == 0 and y != 0:  # se a posicao for do genero (0, y) ou seja qualquer posicao na primeira coluna que e uma parede
            pos_adj = ((x, y - 1), (x + 1, y), (x, y + 1))
        elif x != 0 and y == 0:  # se a posicao for do genero (x, 0) ou seja qualquer posicao na primeira linha que e uma parede
            pos_adj = ((x - 1, y), (x + 1, y), (x, y + 1))
        else:  # se a posicao for do genero (x, y) ou seja qualquer posicao no interior do labirinto
            pos_adj = ((x, y - 1), (x - 1, y), (x + 1, y), (x, y + 1))
        return pos_adj
    else:
        raise ValueError("posicoes_adjacentes: argumento invalido")


def mapa_str(maze, conj_pos):  # funcao que transforma o tuplo que representa o labirinto numa representacao visual
    '''labirinto & conjunto de posicoes -> cadeira de caracteres
    Recebe um labirinto e um conjunto de posicoes
    Retorna a representacao visual do labirinto'''

    def tup_to_list(t):  # funcao auxiliar que transforma os elementos de um tuplo em listas
        i = -1
        comp = (len(t) - 2 * len(t))
        new_t = list(t)
        while i >= comp:
            if type(new_t[i]) == tuple:
                new_t[i] = list(new_t[i])
                i -= 1
            else:
                i -= 1
        return new_t

    # a funcao mapa_str tem que receber um labirinto e um conjunto de posicoes validos que representem um mapa valido
    if eh_labirinto(maze) == True and eh_conj_posicoes(conj_pos) == True and eh_mapa_valido(maze, conj_pos) == True:
        i = -1
        new_maze = tup_to_list(maze)
        comp = len(new_maze)  # representa colunas
        comp_0 = len(new_maze[0])  # representa as linhas
        while i >= (comp - 2 * comp):  # le cada um dos elementos do tuplo de tras para a frente
            u = -1
            while u >= (comp_0 - 2 * comp_0):  # e tudo o que esta dentro desses elementos tambem
                a = new_maze[i][u]
                if new_maze[i][u] == 1:  # se o elemento for uma parede
                    new_maze[i][u] = '#'  # entao passa a ser representado pelo simbolo #
                    u -= 1
                else:
                    new_maze[i][u] = '.'  # se nao tem '.' como representacao visual
                    u -= 1
            i -= 1
        for e in conj_pos:  # as posicoes do labirinto sao lidas uma a uma
            col = e[0]
            lin = e[1]
            if new_maze[col][lin] == '.':  # e sao representadas no labirinto com um 'O' caso nao sejam paredes
                new_maze[col][lin] = 'O'
        c = ''
        for i in range(len(new_maze[0])):  # por cada linha
            a = ''
            u = 0
            while u < len(new_maze):  # le os elementos de todas as colunas dessa linha
                a = a + str(new_maze[u][i])  # e acrescenta os a representacao visual
                u += 1
            c += a + '\n'  # quando acaba a representacao de uma linha passasse para a linha seguinte
        return c[:-1]  # devolve-se a representacao visual sem adicionar uma nova linha no fim
    else:
        raise ValueError("mapa_str: algum dos argumentos e invalido")


def obter_objetivos(m, conj_pos,
                    pos):  # funcao para obter os objetivos de uma posicao ou seja as posicoes no labirinto adjacentes a outras posicoes e que nao sejam ocupadas por unidades ou paredes
    '''labirinto & conjunto de posicoes & posicao -> conjunto de posicoes
    Recebe um labirinto, um conjunto de posicoes(unidades) e uma posicao correspondente a uma das unidades
    Devolve o conjunto de posicoes nao ocupadas dentro do labirinto correspondentes a todos os possiveis objetivos da unidade dada'''
    # para isso tem que receber um labirinto e um conjunto de posicoes validos e que representem um mapa valido e uma posicao correspondente a uma unidade que esteja no conjunto de posicoes
    if eh_labirinto(m) == True and eh_conj_posicoes(conj_pos) == True and eh_posicao(pos) == True and (
            pos in conj_pos) and eh_mapa_valido(m, conj_pos) == True:
        new_conj_pos = ()
        for i in range(len(conj_pos)):
            if conj_pos[i] != pos:  # cria um novo tuplo das posicoes, mas sem a posicao inicial
                new_conj_pos += (conj_pos[i],)
        posi_adj = ()
        for item in new_conj_pos:  # cria um novo tuplo com todas as posicoes adjacentes as unidades no labirinto
            posi_adj += posicoes_adjacentes(item)
        posi_adj_final = ()
        for i in posi_adj:  # verifica para cada uma das posicoes se nao e parede, se nao e a posicao/unidade inicial ou alguma das outras unidades e se nao e repetida
            if m[i[0]][i[1]] != 1 and i != pos and (i not in conj_pos) and (i not in posi_adj_final):
                posi_adj_final += (i,)
        return posi_adj_final  # retorna o conjunto de posicoes que representam objetivos
    else:
        raise ValueError("obter_objetivos: algum dos argumentos e invalido")


def obter_caminho(m, conj_pos,
                  pos):  # funcao para obter o caminho desde a posicao inicial ate a posicao objetivo com o numero minimo de passos
    '''labirinto & conjunto de posicoes & posicao -> conjunto de posicoes
    Recebe um labirinto, um conjunto de posicoes(unidades) e uma posicao correspondente a uma das unidades
    Devolve o conjunto de posicoes correspondente ao caminho desde a posicao dada ate a posicao objetivo mais proxima'''
    # recebe um labirinto e um conjunto de posicoes validos e que representem um mapa valido e uma posicao valida correspondente a uma unidade que esteja no conjunto de posicoes/unidades
    if eh_labirinto(m) == True and eh_conj_posicoes(conj_pos) == True and eh_posicao(pos) == True and (
            pos in conj_pos) and eh_mapa_valido(m, conj_pos):
        objetivos = obter_objetivos(m, conj_pos, pos)  # em primeiro, obtem-se os objetivos
        caminho = ()
        if len(objetivos) == 0:  # se nao ha objetivos, entao o caminho e vazio
            return caminho
        else:
            lista_expl = [pos,
                          ()]  # lista de esploracao que ira contem as posicoes a serem exploradas e o caminho ate elas
            # inicialmente so tem a posicao inicial
            pos_visitadas = ()  # as posicoes visitadas serao guardadas neste tuplo, para que so sejam visitadas uma vez
            cam_atual = ()  # o caminho atual representa o caminho ate a unidade atual
            while len(lista_expl) != 0:  # enquanto a lista de exploracao nao for vazia
                i = 0
                while i <= (len(lista_expl) - 1):  # le os elementos da lista
                    pos_atual = lista_expl[i]  # atualiza a posicao atual
                    if pos_atual in pos_visitadas:  # se ja foi visitada
                        lista_expl = lista_expl[
                                     2:]  # entao sai da lista de exploracao, em conjunto com o caminho que levou ate la, e explora-se a posicao seguinte
                    else:
                        pos_visitadas += (
                        pos_atual,)  # ao visitar a posicao, adiciona-se ao tuplo das posicoes visitadas
                        cam_atual = lista_expl[i + 1] + (
                        pos_atual,)  # o caminho atual passa a ser o caminho que levou ate a posicao mais a propria posicao
                        if pos_atual in objetivos:  # se a posicao atual for um dos objetivos
                            caminho = cam_atual
                            return caminho  # devolve-se o caminho ate ela
                        else:  # se nao
                            pos_adjacentes = posicoes_adjacentes(pos_atual)  # obtem-se as posicoes a ela adjacentes
                            for item in pos_adjacentes:
                                if m[item[0]][item[
                                    1]] != 1:  # e acrescentam-se a lista de exploracao, em conjunto com o caminho, caso nao sejam paredes
                                    lista_expl += [item, cam_atual]
                        i += 2
            return caminho
    else:
        raise ValueError("obter_caminho: algum dos argumentos e invalido")


def mover_unidade(m, conj_pos,
                  pos):  # funcao que atualiza o conjunto de posicoes tendo em conta o movimento da posicao dada em direcao ao objetivo
    '''labirinto & conjunto de posicoes & posicao -> conjunto de posicoes
    Recebe um labirinto, um conjunto de posicoes(unidades) e uma posicao correspondente a uma das unidades
    Devolve o conjunto de posicoes atualizado apos a unidade dada ter realizado um unico movimento'''
    # recebe um labirinto e um conjunto de posicoes validos e que representem um mapa valido e uma posicao valida correspondente a uma unidade que esteja no conjunto de posicoes/unidades
    if eh_labirinto(m) == True and eh_conj_posicoes(conj_pos) == True and eh_posicao(pos) == True and (
            pos in conj_pos) and eh_mapa_valido(m, conj_pos):
        caminho = obter_caminho(m, conj_pos, pos)  # primeiro obtem-se o caminho da unidade dada ate ao objetivo
        if len(caminho) >= 2:  # se o caminho tiver pelo menos duas posicoes
            indice = 0
            for i in range(len(conj_pos)):  # verifica-se qual o indice da posicao dada no conjunto de posicoes dado
                if conj_pos[i] == pos:
                    indice = i
            pos = caminho[1]  # atualiza-se a posicao para a proxima posicao no caminho em direcao ao objetivo
            if pos in conj_pos:  # se essa posicao ja esta no conjunto de posicoes
                return conj_pos  # retorna o conjunto de posicoes sem alterar o lugar da posicao
            else:  # se nao atualiza a posicao
                conj_pos = list(conj_pos)
                conj_pos[indice] = pos
                conj_pos = tuple(conj_pos)
                return conj_pos  # e retorna o conjunto de posicoes atualizado
        else:
            return conj_pos
    else:
        raise ValueError("mover_unidade: algum dos argumentos e invalido")
