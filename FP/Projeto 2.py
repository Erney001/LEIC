# Nome: Afonso da Silva Ferreira / Numero: 96832


###############
# TAD posicao #
###############


def cria_posicao(x, y):
    """
    2 numeros naturais -> posicao
    Recebe 2 numeros naturais maiores ou iguais a zero
    Devolve a posicao correspondente
    """
    if isinstance(x, int) and isinstance(y, int) and x >= 0 and y >= 0:
        return (x, y)
    else:
        raise ValueError("cria_posicao: argumentos invalidos")


def cria_copia_posicao(p):
    """
    posicao -> posicao
    Recebe uma posicao
    Devolve uma posicao com os mesmos valores, mas com localizacao em memoria diferente
    """
    return cria_posicao(p[0], p[1])


def obter_pos_x(p):
    """
    posicao -> numero natural
    Recebe uma posicao
    Devolve a componente x
    """
    return p[0]


def obter_pos_y(p):
    """
    posicao -> numero natural
    Recebe uma posicao
    Devolve a componente y
    """
    return p[1]


def eh_posicao(arg):
    """
    universal -> booleano
    Recebe um argumento qualquer
    Devolve True se for posicao
    """
    return type(arg) == tuple and len(arg) == 2 and isinstance(obter_pos_x(arg), int) and isinstance(obter_pos_y(arg), int) and obter_pos_x(arg) >= 0 and obter_pos_y(arg) >= 0


def posicoes_iguais(p1, p2):
    """
    posicao x posicao -> booleano
    Recebe 2 posicoes
    Devolve True se forem iguais
    """
    return obter_pos_x(p1) == obter_pos_x(p2) and obter_pos_y(p1) == obter_pos_y(p2)


def posicao_para_str(p):
    """
    posicao -> str
    Recebe uma posicao
    Devolve a string que representa a posicao
    """
    return "(" + str(obter_pos_x(p)) + ", " + str(obter_pos_y(p)) + ")"


# Funcoes de alto nivel para este TAD


def obter_posicoes_adjacentes(p):
    """
    posicao -> tuplo de posicoes
    Recebe uma posicao
    Devolve as posicoes adjacentes a essa posicao por ordem de leitura do labirinto
    """
    return tuple(adj for adj in ((p[0] + dx, p[1] + dy) for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 1))) if (adj[0] >= 0 and adj[1] >= 0))


###############
# TAD unidade #
###############


def cria_unidade(p, v, f, e):
    """
    posicao x numero natural x numero natural x str -> unidade
    Recebe uma posicao, um numero natural que representa a vida e outro que representa a forca e uma string que representa o exercito
    Devolve a unidade criada com esses atributos
    """
    if eh_posicao(p) and isinstance(v, int) and isinstance(f, int) and v > 0 and f > 0 and type(e) == str and len(
            e) != 0:
        return {"p": p, "v": v, "f": f, "e": e}
    else:
        raise ValueError("cria_unidade: argumentos invalidos")


def cria_copia_unidade(u):
    """
    unidade -> unidade
    Recebe uma unidade
    Devolve uma unidade com os mesmos atributos, mas com localizacao em memoria diferente
    """
    return cria_unidade(u["p"], u["v"], u["f"], u["e"])


def obter_posicao(u):
    """
    unidade -> posicao
    Recebe uma unidade
    Devolve a sua posicao
    """
    return u["p"]


def obter_exercito(u):
    """
    unidade -> str
    Recebe uma unidade
    Devolve a string que representa o seu exercito
    """
    return u["e"]


def obter_forca(u):
    """
    unidade -> numero natural
    Recebe uma unidade
    Devolve a sua forca
    """
    return u["f"]


def obter_vida(u):
    """
    unidade -> numero natural
    Recebe uma unidade
    Devolve a sua vida
    """
    return u["v"]


def muda_posicao(u, p):
    """
    unidade x posicao -> unidade
    Recebe uma unidade e uma posicao
    Devolve a unidade com a posicao atualizada
    """
    u["p"] = p
    return u


def remove_vida(u, v):
    """
    unidade x numero natural -> unidade
    Recebe uma unidade e um numero natural
    Devolve a unidade com a vida atualizada
    """
    u["v"] -= v
    return u


def eh_unidade(arg):
    """
    universal -> booleano
    Recebe um argumento qualquer
    Devolve True se for unidade
    """
    return type(arg) == dict and len(arg) == 4 and ("p" or "v" or "f" or "e") in arg and eh_posicao(
        obter_posicao(arg)) and type(obter_vida(arg)) == int and obter_vida(arg) > 0 and type(
        obter_forca(arg)) == int and obter_forca(arg) > 0 and type(obter_exercito(arg)) == str and len(
        obter_exercito(arg)) != 0


def unidades_iguais(u1, u2):
    """
    unidade x unidade -> booleano
    Recebe 2 unidades
    Devolve True se forem iguais
    """
    return u1 == u2


def unidade_para_char(u):
    """
    unidade -> str
    Recebe uma unidade
    Devolve a string correspondente ao primeiro caracter do exercito da unidade
    """
    return obter_exercito(u)[0].upper()  # devolve a string em maiuscula


def unidade_para_str(u):
    """
    unidade -> str
    Recebe uma unidade
    Devolve a string que representa a unidade
    """
    return str(unidade_para_char(u)) + str([obter_vida(u), obter_forca(u)]) + "@" + str(obter_posicao(u))


# Funcoes de alto nivel para este TAD


def unidade_ataca(u1, u2):
    """
    unidade x unidade -> booleano
    Recebe 2 unidades
    Devolve True se a segunda unidade for destruida apos um ataque da primeira
    """
    u2 = remove_vida(u2, obter_forca(u1))
    return obter_vida(u2) <= 0


def ordenar_unidades(t):
    """
    tuplo de unidades -> tuplo de unidades
    Recebe um tuplo de unidades
    Devolve um tuplo de unidades com as unidades ordenadas de acordo com a posicao em ordem de leitura do labirinto
    """
    # ordena as posicoes de acordo com a posicao no labirinto
    return tuple(sorted(list(t), key=lambda u: (obter_pos_y(obter_posicao(u)), obter_pos_x(obter_posicao(u)))))


############
# TAD mapa #
############


def cria_mapa(d, w, e1, e2):
    """
    tuplo x tuplo x tuplo x tuplo -> mapa
    Recebe 4 tuplos
    Devolve o mapa criado com esses atributos
    """
    if type(d) == tuple and len(d) == 2 and type(d[0]) == int and type(d[1]) == int and d[0] >= 3 and d[1] >= 3 and type(w) == tuple and type(e1) == tuple and len(e1) >= 1 and type(e2) == tuple and len(e2) >= 1:
        # confirma se as posicoes em w sao validas
        # confirma se w nao tem posicoes que representam paredes exteriores
        # confirma se as posicoes dos exercitos sao validas
        if all(list(eh_posicao(i) for i in w)) and all(list(0 < i[0] < (d[0] - 1) and 0 < i[1] < (d[1] - 1) for i in w)) and all(list(eh_unidade(i) for i in e1+e2)):
            return {"d": d, "w": w, "e1": e1, "e2": e2}
        else:
            raise ValueError("cria_mapa: argumentos invalidos")
    else:
        raise ValueError("cria_mapa: argumentos invalidos")


def cria_copia_mapa(m):
    """
    mapa -> mapa
    Recebe um mapa
    Devolve um mapa com os mesmos atributos, mas com posicao de memoria diferente
    """
    ex1 = tuple(cria_copia_unidade(u1) for u1 in m["e1"])
    ex2 = tuple(cria_copia_unidade(u1) for u1 in m["e2"])
    return cria_mapa(m["d"], m["w"], ex1, ex2)


def obter_tamanho(m):
    """
    mapa -> tuplo
    Recebe um mapa
    Devolve um tuplo de 2 valores inteiros correspondentes as dimensoes do mapa
    """
    return m["d"]


def obter_nome_exercitos(m):
    """
    mapa -> tuplo
    Recebe um mapa
    Devolve um tuplo ordenado com 2 strings correspondentes aos nomes dos exercitos
    """
    t = (obter_exercito(m["e1"][0]), obter_exercito(m["e2"][0]))
    return tuple(sorted(t, key=lambda t: t[0]))  # ordena os exercitos por ordem alfabetica


def obter_unidades_exercito(m, e):
    """
    mapa x string -> tuplo
    Recebe um mapa e uma string correspondente ao nome dum exercito
    Devolve um tuplo contendo as unidades do exercito indicado na string
    """
    if len(m["e1"]) > 0 and obter_exercito(m["e1"][0]) == e:
        return ordenar_unidades(m["e1"])
    elif len(m["e2"]) > 0 and obter_exercito(m["e2"][0]) == e:
        return ordenar_unidades(m["e2"])
    else:  # quando nao houver unidades desse exercito
        return ()


def obter_todas_unidades(m):
    """
    mapa -> tuplo
    Recebe um mapa
    Devolve um tuplo contendo todas as unidades do mapa ordenadas pela ordem de leitura do labirinto
    """
    t = (m["e1"]) + (m["e2"])
    return ordenar_unidades(t)


def obter_unidade(m, p):
    """
    mapa x posicao -> unidade
    Recebe um mapa e uma posicao
    Devolve a unidade que se encontra nessa posicao
    """
    for i in m["e1"]:
        if i["p"] == p:
            return i
    for i in m["e2"]:
        if i["p"] == p:
            return i


def eliminar_unidade(m, u):
    """
    mapa x unidade -> mapa
    Recebe um mapa e uma unidade
    Devolve o mapa atualizado depois de retirar a unidade
    """
    for i in range(len(m["e1"])):
        if i <= len(m["e1"])-1 and m["e1"][i] == u:
            m["e1"] = (m["e1"][:i]) + (m["e1"][i+1:])
    for i in range(len(m["e2"])):
        if i <= len(m["e2"])-1 and m["e2"][i] == u:
            m["e2"] = (m["e2"][:i]) + (m["e2"][i+1:])
    return m


def mover_unidade(m, u, p):
    """
    mapa x unidade x posicao -> mapa
    Recebe um mapa, uma unidade e uma posicao
    Devolve o mapa atualizado depois de mover a unidade para a nova posicao
    """
    for i in m["e1"]:
        if i == u:
            i["p"] = p
            return m
    for i in m["e2"]:
        if i == u:
            i["p"] = p
            return m


def eh_posicao_unidade(m, p):
    """
    mapa x posicao -> booleano
    Recebe um mapa e uma posicao
    Devolve True se a posicao corresponder a uma unidade
    """
    unis = obter_todas_unidades(m)
    for i in unis:
        if obter_posicao(i) == p:
            return True
    return False


def eh_posicao_corredor(m, p):
    """
    mapa x posicao -> booleano
    Recebe um mapa e uma posicao
    Devolve True se a posicao corresponder a um corredor do labirinto
    """
    x, y = obter_tamanho(m)
    # devolve True se a posicao nao corresponder a paredes externas ou internas (as que estao em w)
    return 0 < obter_pos_x(p) < x - 1 and 0 < obter_pos_y(p) < y - 1 and (True if p not in m["w"] else False)


def eh_posicao_parede(m, p):
    """
    mapa x posicao -> booleano
    Recebe um mapa e uma posicao
    Devolve True se a posicao corresponder a uma parade do labirinto
    """
    max_x, max_y = obter_tamanho(m)
    # devolve True se a posicao corresponder a alguma parede externa ou interna (as que estao em w)
    return (obter_pos_x(p) == 0 or obter_pos_x(p) == max_x - 1) or (obter_pos_y(p) == 0 or obter_pos_y(p) == max_y - 1) or (p in m["w"])


def mapas_iguais(m1, m2):
    """
    mapa x mapa -> booleano
    Recebe 2 mapas
    Devolve True se os mapas forem iguais
    """
    return m1 == m2


def mapa_para_str(m):
    """
    mapa -> str
    Recebe um mapa
    Devolve a string que representa o mapa
    """
    string = ''
    max_x, max_y = obter_tamanho(m)
    for y in range(max_y):
        # para cada posicao acrescenta a string o exercito (em str) se a posicao for unidade
        # se nao acrescenta # caso seja perede ou . caso contrario
        string += ''.join(str(unidade_para_char(obter_unidade(m, (x, y)))) if (eh_posicao_unidade(m, (x, y))) else ('#' if (x == 0 or y == 0 or x == max_x-1 or y == max_y-1 or (x, y) in m["w"]) else '.') for x in range(max_x)) + '\n'
    return string[:-1]


def obter_inimigos_adjacentes(m, u):
    """
    mapa x unidade -> tuplo de unidades
    Recebe um mapa e uma unidade
    Devolve um tuplo contendo as unidades inimigas adjacentes a unidade dada ordenadas de acordo com a ordem de leitura do labirinto
    """
    adj = obter_posicoes_adjacentes(obter_posicao(u))
    t = ()
    for x in adj:
        # inimigos adjacentes = todas as unidades inimigas (de exercito diferente)
        # que tem posicao adjacente a unidade recebida como argumento
        if eh_posicao_unidade(m, x) and obter_exercito(obter_unidade(m, x)) != obter_exercito(u):
            t += (obter_unidade(m, x), )
    return tuple(ordenar_unidades(tuple(t)))


def obter_movimento(mapa, unit):
    '''
    A funcao obter_movimento devolve a posicao seguinte da unidade argumento
    de acordo com as regras de movimento das unidades no labirinto.

    obter_movimento: mapa x unidade -> posicao
    '''

    ######################
    # Funcoes auxiliares #
    ######################
    def pos_to_tuple(pos):
        return obter_pos_x(pos), obter_pos_y(pos)

    def tuple_to_pos(tup):
        return cria_posicao(tup[0], tup[1])

    def tira_repetidos(tup_posicoes):
        conj_tuplos = set(tuple(map(pos_to_tuple, tup_posicoes)))
        return tuple(map(tuple_to_pos, conj_tuplos))

    def obter_objetivos(source):
        enemy_side = tuple(filter(lambda u: u != obter_exercito(source), obter_nome_exercitos(mapa)))[0]
        target_units = obter_unidades_exercito(mapa, enemy_side)
        tup_com_repetidos = \
            tuple(adj
                  for other_unit in target_units
                  for adj in obter_posicoes_adjacentes(obter_posicao(other_unit))
                  if eh_posicao_corredor(mapa, adj) and not eh_posicao_unidade(mapa, adj))
        return tira_repetidos(tup_com_repetidos)

    def backtrack(target):
        result = ()
        while target is not None:
            result = (target,) + result
            target, _ = visited[target]
        return result

    ####################
    # Funcao principal #
    ####################
    # Nao mexer se ja esta' adjacente a inimigo
    if obter_inimigos_adjacentes(mapa, unit):
        return obter_posicao(unit)

    visited = {}
    # posicao a explorar, posicao anterior e distancia
    to_explore = [(pos_to_tuple(obter_posicao(unit)), None, 0)]
    # registro do numero de passos minimo ate primeira posicao objetivo
    min_dist = None
    # estrutura que guarda todas as posicoes objetivo a igual minima distancia
    min_dist_targets = []

    targets = tuple(pos_to_tuple(obj) for obj in obter_objetivos(unit))

    while to_explore:  # enquanto nao esteja vazio
        pos, previous, dist = to_explore.pop(0)

        if pos not in visited:  # posicao foi ja explorada?
            visited[pos] = (previous, dist)  # registro no conjunto de exploracao
            if pos in targets:  # se a posicao atual eh uma dos objetivos
                # se eh primeiro objetivo  ou se esta a  distancia minima
                if min_dist is None or dist == min_dist:
                    # acrescentor 'a lista de posicoes minimas
                    min_dist = dist
                    min_dist_targets.append(pos)
            else:  # nao 'e objetivo, acrescento adjacentes
                for adj in obter_posicoes_adjacentes(tuple_to_pos(pos)):
                    if eh_posicao_corredor(mapa, adj) and not eh_posicao_unidade(mapa, adj):
                        to_explore.append((pos_to_tuple(adj), pos, dist + 1))

        # Parar se estou a visitar posicoes mais distantes que o minimo,
        # ou se ja encontrei todos os objetivos
        if (min_dist is not None and dist > min_dist) or len(min_dist_targets) == len(targets):
            break

    # se encontrei pelo menos uma posicao objetivo,
    # escolhe a de ordem de leitura menor e devolve o primeiro movimento
    if len(min_dist_targets) > 0:
        # primeiro dos objetivos em ordem de leitura
        tar = sorted(min_dist_targets, key=lambda x: (x[1], x[0]))[0]
        path = backtrack(tar)
        return tuple_to_pos(path[1])

    # Caso nenhuma posicao seja alcancavel
    return obter_posicao(unit)


######################
# Funcoes adicionais #
######################


def calcula_pontos(m, e):
    """
    mapa x str -> int
    Recebe um mapa e uma string que representa o nome dum exercito
    Devolve um inteiro correspondente aos pontos desse exercito
    """
    res = 0
    for i in obter_unidades_exercito(m, e):
        res += obter_vida(i)
    return res


def simula_turno(m):
    """
    mapa -> mapa
    Recebe um mapa
    Devolve o mapa atualizado depois de se realizar um turno
    """
    x, y = obter_nome_exercitos(m)
    unis = obter_todas_unidades(m)
    eliminadas = ()
    for i in unis:
        if i not in eliminadas:  # se nao foi eliminada, continua em jogo
            if obter_unidades_exercito(m, x) == () or obter_unidades_exercito(m, y) == ():
                return m
            m = mover_unidade(m, i, obter_movimento(m, i))
            ini = obter_inimigos_adjacentes(m, i)
            if len(ini) != 0:
                enemy_unit = ini[0]  # primeiro inimigo de acordo com a ordem de leitura do labirinto
                pos_enemy_unit = obter_posicao(enemy_unit)
                # se depois do ataque a unidade inimiga continuar viva o mapa mantem-se
                # se nao elimina-se a unidade do mapa
                if unidade_ataca(i, enemy_unit):
                    m = eliminar_unidade(m, obter_unidade(m, pos_enemy_unit))
                    eliminadas += (enemy_unit, )
    return m


def simula_batalha(fic, mod):
    """
    str x booleano -> str
    Recebe uma string que representa o nome dum ficheiro e um booleano que representa se o modo verboso esta ativo
    Devolve o mapa e a pontucao iniciais e finais
    E tambem o mapa e a pontucao intermedios se o modo verboso estiver ativo
    """
    f = open(fic, "r")
    t = f.readlines()
    f.close()
    d = eval(t[0])
    w = eval(t[3])
    e1 = ()
    e2 = ()
    for i in eval(t[4]):
        e, v, f = eval(t[1])
        e1 += (cria_unidade(i, v, f, e), )
    for i in eval(t[5]):
        e, v, f = eval(t[2])
        e2 += (cria_unidade(i, v, f, e), )
    m = cria_mapa(d, w, e1, e2)
    exercitos = obter_nome_exercitos(m)
    n = mapa_para_str(m) + '\n' + '[ ' + str(exercitos[0]) + ':' + str(calcula_pontos(m, exercitos[0])) + ' ' + str(exercitos[1]) + ':' + str(calcula_pontos(m, exercitos[1])) + ' ]'
    print(n)  # printa o mapa e a pontuacao iniciais
    end = False
    while not end:
        m_check = cria_copia_mapa(m)
        m = simula_turno(m)
        end = obter_unidades_exercito(m, exercitos[0]) == () or obter_unidades_exercito(m, exercitos[1]) == () or m == m_check
        if mod == True or end:
            # printa o mapa e a pontuacao em cada turno
            print(mapa_para_str(m) + '\n' + '[ ' + str(exercitos[0]) + ':' + str(calcula_pontos(m, exercitos[0])) + ' ' + str(exercitos[1]) + ':' + str(calcula_pontos(m, exercitos[1])) + ' ]')
    # printa o mapa e a pontuacao finais
    return str(exercitos[0]) if calcula_pontos(m, exercitos[1]) == 0 else (str(exercitos[1]) if calcula_pontos(m, exercitos[0]) == 0 else "EMPATE")