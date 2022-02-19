% Projeto LP 2019-2020
% Afonso Ferreira 96832

:- [codigo_comum].

% Predicado 1 ------------------------------------------------------

obtem_letras_palavras([], []).
obtem_letras_palavras([H|T], Letras):-!,maplist(atom_chars, [H], L),
	obtem_letras_palavras(T, Letras1), append([L, Letras1], L2),
	sort(L2, L3), Letras = L3.

% Predicado 2 ------------------------------------------------------

espaco_fila([], []).
espaco_fila(Fila, Esp):-find(Fila, 0, [], [], LTemp), constroi(LTemp, Esp1),
	devolve(Esp1, Esp).

find([], _, Valid, Acc, L):-!, append(Valid, [], V1),
	append(Acc, [V1], L).
find([H|T], Count, Valid, Acc, L):-H == '#', !, Count1 is Count+1,
	append(Acc, [Valid], Acc1), V2 = [],
	find(T, Count1, V2, Acc1, L).
find([H|T], Count, Valid, Acc, L):-H \== '#', !, append(Valid, [H], V1),
	flatten(V1, V2), find(T, Count, V2, Acc, L).

constroi([], []):-!.
constroi([H|T], [H|Res1]):-length(H, N), N>=3, !, constroi(T, Res1).
constroi([H|T], Res1):-length(H, N), N<3, !, constroi(T, Res1).

devolve([], []):-!, fail.
devolve([H|_], H).
devolve([_|T], Res):-devolve(T, Res).

% Predicado 3 ------------------------------------------------------

% setof sem negacao em segundo se n for pra dar false
espacos_fila([], []).
espacos_fila(Fila, Esp):- \+ setof(Espacos, espaco_fila(Fila, Espacos), Esp) -> Esp = [].
espacos_fila(Fila, Esp):-setof(Espacos, espaco_fila(Fila, Espacos), Esp).

% Predicado 4 ------------------------------------------------------

espacos_puzzle([], []).

% com cut no fim se n for pra dar false
espacos_puzzle(L, Espacos):-esp_puz_norm(L, Esp1), mat_transposta(L, L2),
	esp_puz_transp(L2, Esp2), append(Esp1, Esp2, Espacos).

esp_puz_norm([], []):-!.
esp_puz_norm([H|T], Esp):-espacos_fila(H, Esp1), esp_puz_norm(T, Esp2),
	append(Esp1, Esp2, Esp).

esp_puz_transp([], []):-!.
esp_puz_transp([H|T], Esp):-espacos_fila(H, Esp1), esp_puz_transp(T, Esp2),
	append(Esp1, Esp2, Esp).

% Predicado 5 ------------------------------------------------------

iguais(X, Y):- X == Y.

% como fazer sem dar false no fim ?
espacos_com_posicoes_comuns(Espacos, Esp, Esps_com):-obter_atom_esp(Esp, A_Esp), !,
	percorre_esp(Espacos, A_Esp, Esp1), !, exclude(iguais(Esp), Esp1, Esp2), !,
	Esps_com = Esp2, !.

obter_atom_esp([], []):-!.
obter_atom_esp([H|T], [H|Res2]):-obter_atom_esp(T, Res2), !.

percorre_esp([], _, []):-!.
percorre_esp([H|T], A_Esp, Res):-check(H, A_Esp, Res1), Res1 == [], !, percorre_esp(T, A_Esp, Res2),
	append([Res2], Res).
percorre_esp([H|T], A_Esp, Res):-check(H, A_Esp, Res1), Res1 \== [], percorre_esp(T, A_Esp, Res2),
	append([Res1], Res2, Res).

check([], _, []):-!, fail.
check(_, [], []):- !.
check(L, [H|_], L):-membro(H, L), !.
check(L, [H|T], Res):- \+ membro(H, L), check(L, T, Res), !.

membro(_, []):-!, fail.
membro(X, [H|_]):-X == H, !.
membro(X, [_|T]):-membro(X, T).

nao_membro(_, []):-!.
nao_membro(El, [H|T]):-El \== H, nao_membro(El, T).

% Predicado 6 ------------------------------------------------------

palavra_possivel_esp([], _, _, []).

% como fazer sem dar false no fim ?
palavra_possivel_esp(Pal, Esp, Espacos, Letras):-!, member(Pal, Letras),
	unifiable(Pal, Esp, _),
	!, espacos_com_posicoes_comuns(Espacos, Esp, Esps_com), !,
	%check_pals_comuns(Pal, Esp, T1, Letras).
	check_others(Pal, Esp, Esps_com, Letras), !.

% recebe lista de espaços em comum e analisa um por um
% ainda e preciso verificar que as palavras possiveis nos espacos comuns
% tem o tamanho do espaco ? (ja ta feito em aux)
% check_others(Pal, Esp, [H], Letras):- !, check_pals_comuns(Pal, Esp, H, Letras).

check_others(_,_,[],_):-!.
%check_others(_, _, [], _).
check_others(Pal, Esp, [H|T], Letras):-check_pals_comuns(Pal, Esp, H, Letras),
	check_others(Pal, Esp, T, Letras).

% no ultimo arg, recebe a lista contendo listas que sao compostas por letras de palavras
% vejo o indice de Esp onde interseta Esp_com e vejo a letra nesse espaco
% vejo o indice de Esp_com onde interseta Esp e vejo as letras possiveis
check_pals_comuns(Pal, Esp, Esp_com, [H|T]):-verifica_esp_com_2(Esp, Esp_com, Ind_pal_original),
	nth1(Ind_pal_original, Pal, L), verifica_esp_com_2(Esp_com, Esp, Ind_pal_comum),
	aux(Esp_com, Ind_pal_comum, L, [H|T]).

%check_pals_comuns(Pal, Esp, Esp_com, [H|T]):- \+ unifiable(Esp_com, H, _),
%	check_pals_comuns(Pal, Esp, Esp_com, T).

%verifica_pal_aux(Esp, Esp_com, Lst_Pals):-verifica_esp_com_2(Esp_com, Esp, Ind),
%	maplist(nth1, Lst_Pals, Lst_Pals_Pos).

%aux(Ind, [First_word|T], Lst_Pals_Pos):-nth1(Ind, First_word, L).
% ver se existe alguma palavra possivel para aquele indice do esp comum
aux(_, _, _, []):-fail.
aux(Esp_com, Ind, L, [H|_]):-unifiable(Esp_com, H, _), nth1(Ind, H, L1), L1 == L.
aux(Esp_com, Ind, L, [_|T]):-aux(Esp_com, Ind, L, T).

%verifica_pals(Esp_com, Lst_Pals, Ind).
%verifica_pal(Esp_com, Pal, Ind):-nth1(Ind, Pal, Letra), 

% verifica a cabeca do espaco com os espacos comuns
verifica_esp_com([H|T], [H1|_], _, -1):-nao_membro(H1, [H|T]), !.
verifica_esp_com([_], _, Acc, Acc):-!.
verifica_esp_com([H|_], [H1|_], Acc, Acc):-H == H1.
verifica_esp_com([H|T], [H1|T1], Acc, Ind):-H \== H1, Acc1 is Acc+1,
	verifica_esp_com(T, [H1|T1], Acc1, Ind).

% verifica cada um dos espacos comuns conforme a cabeca de Esp lhe pertence ou nao
verifica_esp_com_2([H|T], [H1|T1], Ind):- \+ nao_membro(H1, [H|T]),
	verifica_esp_com([H|T], [H1|T1], 1, Ind).
verifica_esp_com_2([H|T], [H1|T1], Ind):- nao_membro(H1, [H|T]),
	verifica_esp_com_2([H|T], T1, Ind).

% Predicado 7 ------------------------------------------------------

% da sem o include para alguns exemplos
palavras_possiveis_esp(Letras, Espacos, Esp, Pals_Possiveis):-
	%include(palavra_possivel_esp(Esp, Espacos, Letras), Letras, Pals_Possiveis).
	ver_pals(Esp, Espacos, Letras, Letras, Pals_Possiveis).

% quarto arg. e lista de palavras (Letras)
%ver_pals(Esp, Espacos, [H|T], Letras, Res):-.
%	ver_pals_2(H, Esp, Espacos, [H|T], Res1),
%	ver_pals_2(H, Esp, Espacos, [H|T], Res1), append(Res1, Res2, Res).

ver_pals(_, _, [], _, []).
ver_pals(Esp, Espacos, [H|T], Letras, Res):-
	ver_pal(H, Esp, Espacos, Letras, Res1),
	ver_pals(Esp, Espacos, T, Letras, Res2), append(Res1, Res2, Res).

ver_pals_2(Pal, Esp, Espacos, Letras, Res):-
	ver_pal(Pal, Esp, Espacos, Letras, Res).

ver_pal(Pal, Esp, Espacos, Letras, [Pal]):-
	palavra_possivel_esp(Pal, Esp, Espacos, Letras), !.
ver_pal(Pal, Esp, Espacos, Letras, []):-
	\+ palavra_possivel_esp(Pal, Esp, Espacos, Letras).

% Predicado 8 ------------------------------------------------------

% da certo ou os appends com listas tao mal ?
palavras_possiveis(Letras, Espacos, Pals_Possiveis):-
	analisar(Letras, Espacos, Espacos, Pals_Possiveis). %Pals_Possiveis = [Res].

% 2 arg = 3 arg = Espacos
analisar(_, _, [], []).
analisar(Letras, Espacos, [H|T], Res):-
	analisar(Letras, Espacos, T, Res2),
	analisa_esp(Letras, Espacos, H, Res1),
	append([Res1], Res2, Res).

analisa_esp(Letras, Espacos, Esp, Pal_Possiveis):-
	palavras_possiveis_esp(Letras, Espacos, Esp, Pals_Pos),
	append([Esp], [Pals_Pos], Pal_Possiveis).

% Predicado 9 ------------------------------------------------------

letras_comuns([], []):-!.
% 1 arg = lista de palavras
letras_comuns([First_Pal|T], Letras_comuns):-divide_palavra(First_Pal, First_Pal, [], R),
	corre_verificacao(R, [First_Pal|T], Letras_comuns).

% 1 arg = primeiro par (Ind, Letra)
corre_verificacao([], _, []):-!.
corre_verificacao([First|T], Lst_pals, Res):-verificacao_letra([First], Lst_pals),
	corre_verificacao(T, Lst_pals, R), append([First], R, Res).
corre_verificacao([First|T], Lst_pals, Res):- \+ verificacao_letra([First], Lst_pals),
	corre_verificacao(T, Lst_pals, Res).

% verifica se (Ind, Letra) ocorre para todas as palavras na lista
verificacao_letra(_, []):-!.
verificacao_letra([(Ind, Letra)], [First_pal|T]):-nth1(Ind, First_pal, Letra) -> verificacao_letra([(Ind, Letra)], T); fail.

% divide a palavra num conjunto (Indice, Letra) que a compoem
divide_palavra(_, [], Acc, Acc):-!.
divide_palavra(Pal, [Letra|T], Acc, Res):-nth1(Ind, Pal, Letra),
	Temp = (Ind, Letra), nao_membro(Temp, Acc) -> append(Acc, [Temp], Acc1),
	divide_palavra(Pal, T, Acc1, Res).

% Predicado 10 -----------------------------------------------------

atribui_comuns(Pals_Possiveis):-div_espacos(Pals_Possiveis).

div_espacos([]).
% cabeca (lista) = espaco + lista de pals possiveis
div_espacos([H|T]):-check_espaco(H), div_espacos(T).

% Head = Espaco, T = Lista de pals possieveis
check_espaco([H, T|_]):-letras_comuns(T, Letras_comuns),
	obtem_lista_pals_comuns(Letras_comuns, Lst_letras_comuns),
	preenche(H, Lst_letras_comuns), !.

% preenche o Esp com as letras em comum
preenche(_, []).
preenche(Esp, [H|T]):-auxilio(Esp, H), preenche(Esp, T).

% H = indice, T = letra
auxilio(Esp, [H, T|_]):-nth1(H, Esp, T).
%auxilio(Esp, [H|T]):-nth1(H, Esp, X), X = T.

% passa lista de (X, Y)s para lista de [X, Y]s
obtem_lista_pals_comuns([], []).
obtem_lista_pals_comuns([H|T], Res):-item_to_list([H|T], R1),
	obtem_lista_pals_comuns(T, R2), append([R1], R2, Res).

% passar de (X, Y) para [X, Y]
item_to_list([], []).
item_to_list([(X, Y)|_], R):- R = [X, Y].

% Predicado 11 -----------------------------------------------------

retira_impossiveis([], []).
retira_impossiveis(Pals_Possiveis, Novas_Pals_Possiveis):-
	analisa_imp(Pals_Possiveis, Novas_Pals_Possiveis).

% Head = Esp + Lista de palavras
analisa_imp([], []).
analisa_imp([H|T], Novas_Pals_Possiveis):-check_space(H, Res1),
	analisa_imp(T, Res2),
	append([Res1], Res2, Novas_Pals_Possiveis).

% H = Esp, T = Lista de palavras
check_space([H,T|_], Res):-check_pal_imp(H, T, Res1), append([H], [Res1], Res).

% verifica se a palavra ainda e possivel
check_pal_imp(_, [], []).
check_pal_imp(Esp, [H|T], Res):-unifiable(Esp, H, _),
	check_pal_imp(Esp, T, T1), append([H], T1, Res).
check_pal_imp(Esp, [H|T], Res):- \+ unifiable(Esp, H, _),
	check_pal_imp(Esp, T, T1), append([], T1, Res).

% Predicado 12 -----------------------------------------------------

% 1 arg = Pals_Possiveis
obtem_unicas([], []).
obtem_unicas([H|T], Unicas):-existencia_unicas(H, Unicas1),
	obtem_unicas(T, Unicas2), append(Unicas1, Unicas2, Unicas).

% H = Esp, T = Lista de Pals_Possiveis para esse espaco
% 2 arg e Resultado
existencia_unicas([_, T|_], T):-length(T, 1).
existencia_unicas([_,T|_], []):- \+ length(T, 1).

% Predicado 13 -----------------------------------------------------

retira_unicas([], []).
retira_unicas(Pals_Possiveis, Novas_Pals_Possiveis):-obtem_unicas(Pals_Possiveis, Unicas),
	new_words(Pals_Possiveis, Unicas, Novas_Pals_Possiveis).

new_words([], _, []).
new_words([H|T], Unicas, Novas_Pals_Possiveis):-aux_check(H, Unicas, Res1),
	new_words(T, Unicas, Res2), append([Res1], Res2, Novas_Pals_Possiveis).

% 1 arg = Esp + Lista de Pals, 2 arg = Pals unicas, 3 arg = Resultado
aux_check([H, T|_], [], [H,T]):-!.
aux_check([H, T|_], _, [H, T]):-length(T, 1), !.
aux_check([H, T|_], [H1|T1], Res):-length(T, N), N>1, elimina_unicas_2(T, [H1|T1], Res1),
	append([H], [Res1], Res).

% 1 arg = lista de pals possiveis pro esp, 2 arg = lista pals unicas, 3 arg = Res
%elimina_unicas([First_word|T], [First_unique_word|T1], []):-.
%	First_word = First_unique_word.
%elimina_unicas([First_word|T], [First_unique_word|T1], []):-.
%	First_word \= First_unique_word.

elimina_unicas_2([], _, []).
elimina_unicas_2([First_word|T], Lst_Unicas, Res):- \+ nao_membro(First_word, Lst_Unicas),
	elimina_unicas_2(T, Lst_Unicas, Res1), append([], Res1, Res).
elimina_unicas_2([First_word|T], Lst_Unicas, Res):- nao_membro(First_word, Lst_Unicas),
	elimina_unicas_2(T, Lst_Unicas, Res1), append([First_word], Res1, Res).

% Predicado 14 -----------------------------------------------------

simplifica([], []).
simplifica(Pals_Possiveis, Novas_Pals_Possiveis):-perform_checks(Pals_Possiveis, Res1),
	perform_checks(Res1, Res2), Res1 == Res2, !, Novas_Pals_Possiveis = Res2.
simplifica(Pals_Possiveis, Novas_Pals_Possiveis):-perform_checks(Pals_Possiveis, Res1),
	perform_checks(Res1, Res2), Res1 \== Res2, !, simplifica(Res2, Novas_Pals_Possiveis).

perform_checks(Pals_Possiveis, Novas_Pals_Possiveis):-atribui_comuns(Pals_Possiveis),
	retira_impossiveis(Pals_Possiveis, Novas_Pals_Possiveis_1),
	retira_unicas(Novas_Pals_Possiveis_1, Novas_Pals_Possiveis_2),
	Novas_Pals_Possiveis = Novas_Pals_Possiveis_2.

% Predicado 15 -----------------------------------------------------

% H = Lst Pals, T = Grelha
inicializa([], []).
inicializa([H,T|_], Pals_Possiveis):-obtem_letras_palavras(H, Letras),
	espacos_puzzle(T, Espacos), palavras_possiveis(Letras, Espacos, Pals_Pos),
	simplifica(Pals_Pos, Pals_Possiveis).

% Predicado 16 -----------------------------------------------------

escolhe_menos_alternativas([], []):-!.
escolhe_menos_alternativas(Pals_Possiveis, Escolha):-
	confirma_escolha(Pals_Possiveis, Res), Res \== [], !, nth1(1, Res, Escolha);
	!, fail.

% 1 arg = Pals_Possiveis
confirma_escolha([], []).
confirma_escolha([H|T], Res):-analisa_lst_pals(H, Res1), confirma_escolha(T, Res2),
	append([Res1], Res2, Res).
confirma_escolha([H|T], Res):- \+ analisa_lst_pals(H, _), confirma_escolha(T, Res).

% 1 arg = Esp + Lst Pals possiveis
analisa_lst_pals([_,T|_], _):-length(T, 1), fail.
analisa_lst_pals([H,T|_], [H,T]):-length(T, 2).

% Predicado 17 -----------------------------------------------------

% 1 arg = Escolha = Esp + Lst_Pals
experimenta_pal(_, [], []):-!.
experimenta_pal([Esp, Lst_Pals|_], Pals_Possiveis, Novas_Pals_Possiveis):-
	nth1(Index, Pals_Possiveis, [Esp, Lst_Pals]), member(Pal, Lst_Pals), Esp = Pal,
	atualiza_lst_pals(Pal, Lst_Pals, Res), length(Pals_Possiveis, N), length(Novas_Pals_Possiveis, N),
	constroi_novo(Pals_Possiveis, 1, N, Index, [Esp, Res], Novas_Pals_Possiveis).

constroi_novo(_, Comp, N, _, _, _):-X is N+1, X == Comp, !.
constroi_novo(Pals_Possiveis, Comp, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis):-
	Comp =< N, Comp \== Ind, !, nth1(Comp, Pals_Possiveis, Elem),
	nth1(Comp, Novas_Pals_Possiveis, Elem), Comp1 is Comp+1,
	constroi_novo(Pals_Possiveis, Comp1, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis).
constroi_novo(Pals_Possiveis, Comp, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis):-
	Comp =< N, Comp == Ind, !, nth1(Comp, Novas_Pals_Possiveis, Lst_Pals_atualizada),
	Comp1 is Comp+1, constroi_novo(Pals_Possiveis, Comp1, N, Ind, Lst_Pals_atualizada, Novas_Pals_Possiveis).

atualiza_lst_pals(_, [], []).
atualiza_lst_pals(Pal, [H|_], [H]):-Pal == H.
atualiza_lst_pals(Pal, [H|T], Res):-Pal \== H, atualiza_lst_pals(Pal, T, Res).

% Predicado 18 -----------------------------------------------------

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

ver_unicas([]):-!.
ver_unicas([First_esp|T]):-First_esp = [_,Lst_Pals_Pos],
	length(Lst_Pals_Pos, 1),
	ver_unicas(T).

%ver_unicas(Pals_Possiveis):-obtem_unicas(Pals_Possiveis, Unicas),
%	length(Pals_Possiveis, N1), length(Unicas, N2), N1 == N2.

% Predicado 19 -----------------------------------------------------

resolve([]):-!.
resolve(Puz):-inicializa(Puz, Pals_Possiveis),
	resolve_aux(Pals_Possiveis, _).

