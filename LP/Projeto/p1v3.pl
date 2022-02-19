% Projeto LP 2019-2020
% Afonso Ferreira 96832

:- [codigo_comum].

% Predicado 1 ------------------------------------------------------

/*	Pega na primeira palavra da lista e decompoe em caracteres atomicos
	E aplicada recursivamente ao resto da lista
	No final, juntam-se os resultados e ordenam-se */

% Primeiro arg e lista de palavras
obtem_letras_palavras([], []):-!.
obtem_letras_palavras([H|T], Letras):-!, maplist(atom_chars, [H], L),
	obtem_letras_palavras(T, Letras1), append([L, Letras1], L2),
	sort(L2, L3), Letras = L3.

% Predicado 2 ------------------------------------------------------

/*	Devolve todos os espacos de uma fila */

espaco_fila([], []).
espaco_fila(Fila, Esp):-find(Fila, 0, [], [], LTemp), constroi(LTemp, Esp1),
	devolve(Esp1, Esp).

/*	Percorre a fila e verifica a existencia de # que delimitam os espacos
	Cada vez que aparece um # inicia-se um novo espaco */
find([], _, Valid, Acc, L):-!, append(Valid, [], V1),
	append(Acc, [V1], L).
find([H|T], Count, Valid, Acc, L):-H == '#', !, Count1 is Count+1,
	append(Acc, [Valid], Acc1), V2 = [],
	find(T, Count1, V2, Acc1, L).
find([H|T], Count, Valid, Acc, L):-H \== '#', !, append(Valid, [H], V1),
	flatten(V1, V2), find(T, Count, V2, Acc, L).

/* Constroi a lista apenas com os espacos que tem mais de 3 elementos */
constroi([], []):-!.
constroi([H|T], [H|Res1]):-length(H, N), N>=3, !, constroi(T, Res1).
constroi([H|T], Res1):-length(H, N), N<3, !, constroi(T, Res1).

/*	Vai devolver o primeiro espaco ou um seguinte */
devolve([], []):-!, fail.
devolve([H|_], H).
devolve([_|T], Res):-devolve(T, Res).

% Predicado 3 ------------------------------------------------------

/*	Devolve todos os espacos de uma fila, ou seja, o seu conjunto
	Se nao existirem espacos, devolve [] */
espacos_fila([], []).
espacos_fila(Fila, Esp):- \+ setof(Espacos, espaco_fila(Fila, Espacos), Esp) -> Esp = [].
espacos_fila(Fila, Esp):-setof(Espacos, espaco_fila(Fila, Espacos), Esp).

% Predicado 4 ------------------------------------------------------

/*	Devolve os espacos todos, horizontais e verticais, de um puzzle,
	ou seja, os espacos das filas na matriz normal juntamente com os
	espacos das filas da matriz transposta */
espacos_puzzle([], []).
espacos_puzzle(L, Espacos):-esp_puz_norm(L, Esp1), mat_transposta(L, L2),
	esp_puz_transp(L2, Esp2), append(Esp1, Esp2, Espacos).

/*	Ve todos os espacos de uma fila em todas as filas da matriz normal */
esp_puz_norm([], []):-!.
esp_puz_norm([H|T], Esp):-espacos_fila(H, Esp1), esp_puz_norm(T, Esp2),
	append(Esp1, Esp2, Esp).

/*	Ve todos os espacos de uma fila em todas as filas da matriz transposta */
esp_puz_transp([], []):-!.
esp_puz_transp([H|T], Esp):-espacos_fila(H, Esp1), esp_puz_transp(T, Esp2),
	append(Esp1, Esp2, Esp).

% Predicado 5 ------------------------------------------------------

iguais(X, Y):- X == Y.

/*	Para obter os espacos com posicoes comuns a um dado espaco
	vamos ver todas as posicoes individuais do espaco dado
	e percorremos o conjunto de espacos para encontrar aqueles
	que tem uma dessas posicoes individuais e sao espacos comuns */
espacos_com_posicoes_comuns(Espacos, Esp, Esps_com):-obter_atom_esp(Esp, A_Esp), !,
	percorre_esp(Espacos, A_Esp, Esp1), !, exclude(iguais(Esp), Esp1, Esp2), !,
	Esps_com = Esp2, !.

/*	Obter posicoes individuais do espaco */
obter_atom_esp([], []):-!.
obter_atom_esp([H|T], [H|Res2]):-obter_atom_esp(T, Res2), !.

/*	Percorrer o conjunto de espacos
	Primeiro verificamos o primeiro espaco e depois os seguintes recursivamente
	No final, juntamos o resultado */
percorre_esp([], _, []):-!.
percorre_esp([H|T], A_Esp, Res):-check(H, A_Esp, Res1), Res1 == [], !, percorre_esp(T, A_Esp, Res2),
	append([Res2], Res).
percorre_esp([H|T], A_Esp, Res):-check(H, A_Esp, Res1), Res1 \== [], percorre_esp(T, A_Esp, Res2),
	append([Res1], Res2, Res).

/*	Para verificar um espaco verificamos se algum das posicoes
	individuais do espaco dado lhe pertence
	Se sim, ele e um espaco comum */
check([], _, []):-!, fail.
check(_, [], []):- !.
check(L, [H|_], L):-membro(H, L), !.
check(L, [H|T], Res):- \+ membro(H, L), check(L, T, Res), !.

% Funcoes auxiliares membro e nao_membro
membro(_, []):-!, fail.
membro(X, [H|_]):-X == H, !.
membro(X, [_|T]):-membro(X, T).

nao_membro(_, []):-!.
nao_membro(El, [H|T]):-El \== H, nao_membro(El, T).

% Predicado 6 ------------------------------------------------------

/*	Pal e uma palavra possivel para Esp se for uma palavra da lista,
	se for unificavel com o espaco Esp e se nao incomodar espacos comuns a esse */
palavra_possivel_esp([], _, _, []).
palavra_possivel_esp(Pal, Esp, Espacos, Letras):-!, member(Pal, Letras),
	unifiable(Pal, Esp, _),
	!, espacos_com_posicoes_comuns(Espacos, Esp, Esps_com), !,
	check_others(Pal, Esp, Esps_com, Letras), !.

/*	Recebe lista de espacos em comum e analisa um por um */
check_others(_,_,[],_):-!.
check_others(Pal, Esp, [H|T], Letras):-check_pals_comuns(Pal, Esp, H, Letras),
	check_others(Pal, Esp, T, Letras).

/*	No ultimo arg, recebe a lista de palavras
	Vejo o indice de Esp onde interseta Esp_com e vejo a letra nessa espaco
	Vejo o indice de Esp_com onde interseta Esp e vejo a letra nessa espaco
	E vejo se ha palavras que podem ficar com letras nessas posicoes */
check_pals_comuns(Pal, Esp, Esp_com, [H|T]):-verifica_esp_com_2(Esp, Esp_com, Ind_pal_original),
	nth1(Ind_pal_original, Pal, L), verifica_esp_com_2(Esp_com, Esp, Ind_pal_comum),
	aux(Esp_com, Ind_pal_comum, L, [H|T]).

/*	Vejo se existe alguma palavra possivel para aquele indice do
	Esp comum, ou seja, vejo se existe uma letra duma palavra para
	aquele indice do Esp comum */
aux(_, _, _, []):-fail.
aux(Esp_com, Ind, L, [H|_]):-unifiable(Esp_com, H, _), nth1(Ind, H, L1), L1 == L.
aux(Esp_com, Ind, L, [_|T]):-aux(Esp_com, Ind, L, T).

/*	Verifica a cabeca do espaco com os espacos comuns */
verifica_esp_com([H|T], [H1|_], _, -1):-nao_membro(H1, [H|T]), !.
verifica_esp_com([_], _, Acc, Acc):-!.
verifica_esp_com([H|_], [H1|_], Acc, Acc):-H == H1.
verifica_esp_com([H|T], [H1|T1], Acc, Ind):-H \== H1, Acc1 is Acc+1,
	verifica_esp_com(T, [H1|T1], Acc1, Ind).

/*	Verifica cada um dos espacos comuns conforme
	a cabeca de Esp lhe pertence ou nao */
verifica_esp_com_2([H|T], [H1|T1], Ind):- \+ nao_membro(H1, [H|T]),
	verifica_esp_com([H|T], [H1|T1], 1, Ind).
verifica_esp_com_2([H|T], [H1|T1], Ind):- nao_membro(H1, [H|T]),
	verifica_esp_com_2([H|T], T1, Ind).

% Predicado 7 ------------------------------------------------------

/*	Vejo as palavras possiveis para um espaco */
palavras_possiveis_esp(Letras, Espacos, Esp, Pals_Possiveis):-
	ver_pals(Esp, Espacos, Letras, Letras, Pals_Possiveis).

/*	Percorrendo a lista de palavras e vejo todas aquelas
	que sao palavras possiveis para o Esp usando o predicado
	anterior */
ver_pals(_, _, [], _, []).
ver_pals(Esp, Espacos, [H|T], Letras, Res):-
	ver_pal(H, Esp, Espacos, Letras, Res1),
	ver_pals(Esp, Espacos, T, Letras, Res2), append(Res1, Res2, Res).

ver_pal(Pal, Esp, Espacos, Letras, [Pal]):-
	palavra_possivel_esp(Pal, Esp, Espacos, Letras), !.
ver_pal(Pal, Esp, Espacos, Letras, []):-
	\+ palavra_possivel_esp(Pal, Esp, Espacos, Letras).

% Predicado 8 ------------------------------------------------------

/*	Percorre cada um dos espacos e ve as suas palavras possiveis
	atraves dos predicados anteriores e 2 auxiliares */
palavras_possiveis(Letras, Espacos, Pals_Possiveis):-
	analisar(Letras, Espacos, Espacos, Pals_Possiveis).

analisar(_, _, [], []).
analisar(Letras, Espacos, [H|T], Res):-
	analisar(Letras, Espacos, T, Res2),
	analisa_esp(Letras, Espacos, H, Res1),
	append([Res1], Res2, Res).

analisa_esp(Letras, Espacos, Esp, Pal_Possiveis):-
	palavras_possiveis_esp(Letras, Espacos, Esp, Pals_Pos),
	append([Esp], [Pals_Pos], Pal_Possiveis).

% Predicado 9 ------------------------------------------------------

/*	Para uma lista de palavras, vejo as letras comuns a todas as palavras
	Comeco por verificar se o primeiro par (Ind, Letra) ocorre para todas as pals
	Em seguida verifico para todos os outros pares (Ind, Letra) */
letras_comuns([], []):-!.
letras_comuns([First_Pal|T], Letras_comuns):-divide_palavra(First_Pal, First_Pal, [], R),
	corre_verificacao(R, [First_Pal|T], Letras_comuns).

% primeiro arg e primeiro par (Ind, Letra)
corre_verificacao([], _, []):-!.
corre_verificacao([First|T], Lst_pals, Res):-verificacao_letra([First], Lst_pals),
	corre_verificacao(T, Lst_pals, R), append([First], R, Res).
corre_verificacao([First|T], Lst_pals, Res):- \+ verificacao_letra([First], Lst_pals),
	corre_verificacao(T, Lst_pals, Res).

%	Verifica se (Ind, Letra) ocorre para todas as palavras na lista
verificacao_letra(_, []):-!.
verificacao_letra([(Ind, Letra)], [First_pal|T]):-nth1(Ind, First_pal, Letra) -> verificacao_letra([(Ind, Letra)], T); fail.

%	Divide a palavra num conjunto (Indice, Letra) que a compoem
divide_palavra(_, [], Acc, Acc):-!.
divide_palavra(Pal, [Letra|T], Acc, Res):-nth1(Ind, Pal, Letra),
	Temp = (Ind, Letra), nao_membro(Temp, Acc) -> append(Acc, [Temp], Acc1),
	divide_palavra(Pal, T, Acc1, Res).

% Predicado 10 -----------------------------------------------------

/*	Predicado para atribuir letras comuns a lista de pals possiveis
	para cada um dos espacos */
atribui_comuns(Pals_Possiveis):-div_espacos(Pals_Possiveis).

%	Primeiro analisa um espaco e depois os restantes recursivamente
div_espacos([]):-!.
div_espacos([H|T]):-check_espaco(H), div_espacos(T).

/*	Primeiro vemos as letras em comum
	Passamos essa lista de letras para outra forma
	E unificamos a letra no indice do espaco que estamos a analisar */
check_espaco([H, T|_]):-letras_comuns(T, Letras_comuns),
	obtem_lista_pals_comuns(Letras_comuns, Lst_letras_comuns),
	preenche(H, Lst_letras_comuns).

%	H e indice, T e letra
preenche(_, []).
preenche(Esp, [H|T]):-auxilio(Esp, H), preenche(Esp, T).

auxilio(Esp, [H, T|_]):-nth1(H, Esp, T).

%	Passar lista de (X, Y) para lista de [X, Y]
obtem_lista_pals_comuns([], []):-!.
obtem_lista_pals_comuns([H|T], Res):-item_to_list([H|T], R1),
	obtem_lista_pals_comuns(T, R2), append([R1], R2, Res).

%	Passar de (X, Y) para [X, Y]
item_to_list([], []):-!.
item_to_list([(X, Y)|_], R):- R = [X, Y].

% Predicado 11 -----------------------------------------------------

/*	Percorre todas as palavras possiveis para cada espaco e
	retira aqueles que ja nao sao unificaveis com o espaco */
retira_impossiveis([], []).
retira_impossiveis(Pals_Possiveis, Novas_Pals_Possiveis):-
	analisa_imp(Pals_Possiveis, Novas_Pals_Possiveis).

/*	Analisa um espaco e os restantes recursivamente
	Juntando os resultados np fim */
analisa_imp([], []).
analisa_imp([H|T], Novas_Pals_Possiveis):-check_space(H, Res1),
	analisa_imp(T, Res2),
	append([Res1], Res2, Novas_Pals_Possiveis).

%	Atualizamos o conjunto Esp e Lst_Pals_Possiveis para Esp
check_space([H,T|_], Res):-check_pal_imp(H, T, Res1), append([H], [Res1], Res).

/*	Predicado auxiliar para devolver [] se a palavra ja nao e unificavel
	e para devolver a propria para se ainda for
	E chamado recursivamente para analisar todas as palavras possiveis
	para um dado espaco */
check_pal_imp(_, [], []).
check_pal_imp(Esp, [H|T], Res):-unifiable(Esp, H, _),
	check_pal_imp(Esp, T, T1), append([H], T1, Res).
check_pal_imp(Esp, [H|T], Res):- \+ unifiable(Esp, H, _),
	check_pal_imp(Esp, T, T1), append([], T1, Res).

% Predicado 12 -----------------------------------------------------

/*	Verifica quais sao as palavras unicas com recurso a um predicado auxiliar,
	recursao e juncao de resultados com append */
obtem_unicas([], []).
%	H e primeiro conjunto formado por Esp e Lst_Pals_Possiveis para Esp
obtem_unicas([H|T], Unicas):-existencia_unicas(H, Unicas1),
	obtem_unicas(T, Unicas2), append(Unicas1, Unicas2, Unicas).

/*	A palavra e unica se for a unica possivel para esse espaco, ou seja
	a unica na lista de palavras possiveis para esse espaco */
%	T e lista de palavras possiveis para o Esp a analisar
existencia_unicas([_, T|_], T):-length(T, 1).
existencia_unicas([_,T|_], []):- \+ length(T, 1).

% Predicado 13 -----------------------------------------------------

/*	Obtemos as palavras unicas para depois as comecar a remover de Pals_Possiveis */
retira_unicas([], []).
retira_unicas(Pals_Possiveis, Novas_Pals_Possiveis):-obtem_unicas(Pals_Possiveis, Unicas),
	new_words(Pals_Possiveis, Unicas, Novas_Pals_Possiveis).

/*	Analisa-se o primeiro espaco e depois os outros atraves de recursao
	No final, juntam-se os resultados para formar a nova lista */
new_words([], _, []).
%	H e primeiro conjuto Esp e Lst_Pals_Possiveis para Esp
new_words([H|T], Unicas, Novas_Pals_Possiveis):-aux_check(H, Unicas, Res1),
	new_words(T, Unicas, Res2), append([Res1], Res2, Novas_Pals_Possiveis).

/*	Se nao for palavra unica, chama-se elimina_unicas_2 para eliminar
	as palavras unicas */
%	H e Esp e T e Lst_Pals_Possiveis para Esp
aux_check([H, T|_], [], [H,T]):-!.
aux_check([H, T|_], _, [H, T]):-length(T, 1), !.
aux_check([H, T|_], [H1|T1], Res):-length(T, N), N>1, elimina_unicas_2(T, [H1|T1], Res1),
	append([H], [Res1], Res).

/*	Para cada uma das palavras possiveis para um espaco, ve se e unica
	Se for retira-a, se nao for passa ao proximo elemento recursivamente */
elimina_unicas_2([], _, []).
elimina_unicas_2([First_word|T], Lst_Unicas, Res):- \+ nao_membro(First_word, Lst_Unicas),
	elimina_unicas_2(T, Lst_Unicas, Res1), append([], Res1, Res).
elimina_unicas_2([First_word|T], Lst_Unicas, Res):- nao_membro(First_word, Lst_Unicas),
	elimina_unicas_2(T, Lst_Unicas, Res1), append([First_word], Res1, Res).

% Predicado 14 -----------------------------------------------------

/*	Predicado para simplificar Pals_Possiveis, ou seja, aplicar atribui_comuns,
	retira_impossiveis e retira_unicas ate nao haver alteracoes */
simplifica([], []).
/*	Se Res1 e igual a Res2, e altura de o predicado devolver o resultado, 
	logo unificamos Novas_Pals_Possiveis com Res2 */
simplifica(Pals_Possiveis, Novas_Pals_Possiveis):-perform_checks(Pals_Possiveis, Res1),
	perform_checks(Res1, Res2), Res1 == Res2, !, Novas_Pals_Possiveis = Res2.
simplifica(Pals_Possiveis, Novas_Pals_Possiveis):-perform_checks(Pals_Possiveis, Res1),
	perform_checks(Res1, Res2), Res1 \== Res2, !, simplifica(Res2, Novas_Pals_Possiveis).

perform_checks(Pals_Possiveis, Novas_Pals_Possiveis):-atribui_comuns(Pals_Possiveis),
	retira_impossiveis(Pals_Possiveis, Novas_Pals_Possiveis_1),
	retira_unicas(Novas_Pals_Possiveis_1, Novas_Pals_Possiveis_2),
	Novas_Pals_Possiveis = Novas_Pals_Possiveis_2.

% Predicado 15 -----------------------------------------------------

/*	Para inicializar um puzzle vemos as letras das palavras, os espacos do
	puzzle, as palavras possiveis para cada espaco e, por fim, aplicamos
	o predicado simplifica */
inicializa([], []).
%	H e lista de palavras e T e a grelha do puzzle
inicializa([H,T|_], Pals_Possiveis):-obtem_letras_palavras(H, Letras),
	espacos_puzzle(T, Espacos), palavras_possiveis(Letras, Espacos, Pals_Pos),
	simplifica(Pals_Pos, Pals_Possiveis).

% Predicado 16 -----------------------------------------------------

/*	Escolhe um conjunto Esp e Lst_Pals_Possiveis para Esp
	que tem um numero minimo de pals possiveis */
escolhe_menos_alternativas([], []):-!.
%	Se nao houver escolha possivel, Res ser [], da false
escolhe_menos_alternativas(Pals_Possiveis, Escolha):-
	confirma_escolha(Pals_Possiveis, Res), Res \== [], !, nth1(1, Res, Escolha);
	!, fail.

/*	Ve o conjunto de todas as escolha possiveis, ou seja, todas as lista de
	palavras possiveis para um Esp que tem mais que uma palavra */
confirma_escolha([], []).
confirma_escolha([H|T], Res):-analisa_lst_pals(H, Res1), confirma_escolha(T, Res2),
	append([Res1], Res2, Res).
confirma_escolha([H|T], Res):- \+ analisa_lst_pals(H, _), confirma_escolha(T, Res).

/*	Devolve o conjunto Esp e Lst_Pals_Possiveis para Esp se
	essa lista de palavras possiveis, representada por T, tiver mais
	do que uma palavra */
analisa_lst_pals([_,T|_], _):-length(T, 1), fail.
analisa_lst_pals([H,T|_], [H,T]):-length(T, 2).

% Predicado 17 -----------------------------------------------------

/*	Para experimentar uma palavra vejo uma palavra em Lst_Pals com o predicado
	member, unifico a palavra escolhida com o Esp da Escolha e em seguida
	construo a lista Novas_Pals_Possiveis, a lista com a alteracao */
experimenta_pal(_, [], []):-!.
%	Primeiro arg e Escolha
experimenta_pal([Esp, Lst_Pals|_], Pals_Possiveis, Novas_Pals_Possiveis):-
	nth1(Index, Pals_Possiveis, [Esp, Lst_Pals]), member(Pal, Lst_Pals), Esp = Pal,
	atualiza_lst_pals(Pal, Lst_Pals, Res), length(Pals_Possiveis, N), length(Novas_Pals_Possiveis, N),
	constroi_novo(Pals_Possiveis, 1, N, Index, [Esp, Res], Novas_Pals_Possiveis).

/*	Para constroir a nova lista, copia-se tudo menos a parte do Espaco alterado
	Essa parte e susbtituida pelo Espaco e a Lista atualizada de palavras
	possiveis para esse espaco */
constroi_novo(_, Comp, N, _, _, _):-X is N+1, X == Comp, !.
constroi_novo(Pals_Possiveis, Comp, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis):-
	Comp =< N, Comp \== Ind, !, nth1(Comp, Pals_Possiveis, Elem),
	nth1(Comp, Novas_Pals_Possiveis, Elem), Comp1 is Comp+1,
	constroi_novo(Pals_Possiveis, Comp1, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis).
constroi_novo(Pals_Possiveis, Comp, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis):-
	Comp =< N, Comp == Ind, !, nth1(Comp, Novas_Pals_Possiveis, Lst_Pals_atualizada),
	Comp1 is Comp+1, constroi_novo(Pals_Possiveis, Comp1, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis).

/*	Remove a palavra que nao unificou com a Escolha da lista de palavras
	possiveis para esse Esp */
atualiza_lst_pals(_, [], []).
atualiza_lst_pals(Pal, [H|_], [H]):-Pal == H.
atualiza_lst_pals(Pal, [H|T], Res):-Pal \== H, atualiza_lst_pals(Pal, T, Res).

% Predicado 18 -----------------------------------------------------

/*	Este predicado arranja uma Escolha, experimenta essa Escolha e
	simplifica o conjunto Pals_Possiveis gerado
	Estes passos sao repetidos ate nao haver Espacos com numero de palavras
	superior a 1 */
resolve_aux([], []):-!.
resolve_aux(Pals_Possiveis, Novas_Pals_Possiveis):-
	escolhe_menos_alternativas(Pals_Possiveis, Escolha),
	experimenta_pal(Escolha, Pals_Possiveis, Novas_Pals_Possiveis_1),
	simplifica(Novas_Pals_Possiveis_1, Novas_Pals_Possiveis_2),
	ver_unicas(Novas_Pals_Possiveis_2), !,
	Novas_Pals_Possiveis = Novas_Pals_Possiveis_2.
resolve_aux(Pals_Possiveis, Novas_Pals_Possiveis):-
	escolhe_menos_alternativas(Pals_Possiveis, Escolha),
	experimenta_pal(Escolha, Pals_Possiveis, Novas_Pals_Possiveis_1),
	simplifica(Novas_Pals_Possiveis_1, Novas_Pals_Possiveis_2),
	\+ ver_unicas(Novas_Pals_Possiveis_2),
	resolve_aux(Novas_Pals_Possiveis_2, Novas_Pals_Possiveis), !.

/*	Predicado auxiliar que devolve true se todos os Espacos tiveremos
	apenas uma palavra possivel */
ver_unicas([]):-!.
ver_unicas([First_esp|T]):-First_esp = [_,Lst_Pals_Pos],
	length(Lst_Pals_Pos, 1),
	ver_unicas(T).

% Predicado 19 -----------------------------------------------------

/*	Este predicado comeca por inicializar o Puzzle e depois tenta resolve-lo
	ate chegar a uma solucao */
resolve([]):-!.
resolve(Puz):-inicializa(Puz, Pals_Possiveis),
	resolve_aux(Pals_Possiveis, _).

