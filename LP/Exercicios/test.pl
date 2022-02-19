% Ficha 7
nao_membro(_, []):-!.
nao_membro(El, [H|T]):-El \== H, nao_membro(El, T).

% Ficha 8 (fail)
insere_ordenado(El, Lst1, Lst2):-aux(El, Lst1, [], Lst2).

aux(El, [], Acc, Lst2):-append(Acc, [El], Lst2).
aux(El, [H|T], Acc, Lst2):-El>=H, append(Acc, [H], Acc1), aux(El, T, Acc1, Lst2); El<H, append(Acc, [El,H|T], Lst2).

junta_novo_aleatorio(Lst1, Inf, Sup, Lst2):-random_between(Inf, Sup, N), insere_ordenado(N, Lst1, Lst2).

repete_el(El, N, L):-aux_1(El, N, [], L).

aux_1(_, 0, Acc, Acc).
aux_1(El, N, Acc, L):-N>0, append(Acc, [El], Acc1), N1 is N-1, aux_1(El, N1, Acc1, L).

duplica(L1, L2):-aux_2(L1, [], L2).

aux_2([], Acc, Acc).
aux_2([H|T], Acc, L2):-append(Acc, [H,H], Acc1), aux_2(T, Acc1, L2).

num_occ(El, L, N):-aux_3(El, L, N).

aux_3(_, [], 0).
aux_3(El, [H|T], N):-aux_3(El, T, N1), El==H, N is N1+1; aux_3(El, T, N1), El\==H, N is N1.

substitui_maiores_N(N, Subst, L1, L2):-aux_4(N, Subst, L1, [], L2).

aux_4(_, _, [], Acc, Acc).
aux_4(N, Subst, [H|T], Acc, L2):-H>N, !, append(Acc, [Subst], Acc1), aux_4(N, Subst, T, Acc1, L2).
aux_4(N, Subst, [H|T], Acc, L2):-append(Acc, [H], Acc1), aux_4(N, Subst, T, Acc1, L2).

% Ficha 8 (OK)
insere_ordenado_2(El, L1, L2):-union(L1, [El], L3),
    msort(L3, L2).

junta_novo_aleatorio_2(L1, Inf, Sup, L2):-
    random_between(Inf, Sup, N),
    append([L1, [N]], L3), msort(L3, L2).

repete_el_2(_, 0, []).
repete_el_2(El, N, L):-N1 is N-1, repete_el_2(El, N1, L1),
    append(L1, [El], L).

duplica_elementos_2(L1, L2):-append(L1, L1, L3),
    msort(L3, L2).

num_occ_2(L, N, 0):- \+ member(N, L).
num_occ_2(L, El, N):-select(El, L, L2), num_occ_2(L2, El, N1),
    N is N1+1.

substitui_maiores_2(_, _, [], []).
substitui_maiores_2(N, Subst, L1, L2):-select(N, L1, Subst, L3),
    substitui_maiores_2(N, Subst, L3, L2), \+ member(N, L2).

% Ficha 9
suc(N, M):-M is N+1.
ant(N, M):-M is N-1.

perimetro(R, P):-P is 2*pi*R.

divisor(D, N):-R is D mod N, R == 0.

aplica_op(+, Val1, Val2, R):-R is Val1 + Val2.
aplica_op(-, Val1, Val2, R):-R is Val1 - Val2.
aplica_op(*, Val1, Val2, R):-R is Val1 * Val2.
aplica_op(/, Val1, Val2, R):-R is Val1 / Val2.

soma_digitos(0, 0).
soma_digitos(N, S):-N>0, N1 is N mod 10, N2 is N div 10,
	soma_digitos(N2, S1), S is S1 + N1.

soma_digitos_it(N, S):-aux_5(N, 0, S).

aux_5(0, Acc, Acc).
aux_5(N, Acc, S):-N>0, N1 is N mod 10, Acc1 is Acc + N1, N2 is N div 10,
	aux_5(N2, Acc1, S).

inverte(N, Inv):-aux_6(N, 0, Inv).

aux_6(N, Acc, Inv):-N>0, N<10, Inv is Acc*10+N.
aux_6(N, Acc, Inv):-N>0, Unidades is N mod 10, Restante is N div 10,
	Acc1 is Acc*10+Unidades, aux_6(Restante, Acc1, Inv).

triangular(N):-N>0, teste(N, 0, 1).

teste(N, Acc, _):-N == Acc.
teste(N, Acc, Num):-N>Acc, Acc1 is Acc+Num, Num1 is Num+1, teste(N, Acc1, Num1).
teste(N, Acc, _):-N<Acc, fail.

% Aval 8
% Ex. 1
digitos_1_N(1, 1).
digitos_1_N(N, I):-N>1, N<10, N1 is N-1, digitos_1_N(N1, I1),
	I is I1*10+N.

% Ex. 3
escreve_N_ast(0):-nl.
escreve_N_ast(N):-N>0, write('*'), N1 is N-1, escreve_N_ast(N1).

% Ex. 4
triangulo(N):-N>0, constroi(N, 1), !.

constroi(N, N):-!, escreve_N_ast(N), !.
constroi(N, Num):-escreve_N_ast(Num), Num1 is Num+1, constroi(N, Num1), !.

% Ex. 6
digitos_pares(0):-!.
digitos_pares(N):-N>0, Last_digit is N mod 10, Restante is N div 10,
	R is Last_digit mod 2, R == 0, digitos_pares(Restante), !.

% Ficha 10
classe(0, zero):-!.
classe(N, positivo):- N>0, !.
classe(N, negativo):- N<0, !.

intersecao([], _, []):- !.
intersecao([H|T], [H1|T1], [H|Res]):- \+ nao_membro(H, [H1|T1]), intersecao(T, [H1|T1], Res), !.
intersecao([H|T], [H1|T1], Res):- nao_membro(H, [H1|T1]), intersecao(T, [H1|T1], Res), !.

disjuntas([], []):-!.
disjuntas([], _):-!.
disjuntas(_, []):-!.
disjuntas(L1, L2):-intersecao(L1, L2, Res), Res \== [], !, fail.
disjuntas(L1, L2):-intersecao(L1, L2, Res), Res == [].

disjuntas_2([], []):-!.
disjuntas_2([], _):-!.
disjuntas_2(_, []):-!.
disjuntas_2([H|_], L2):- \+ nao_membro(H, L2), !, fail.
disjuntas_2([H|T], L2):- nao_membro(H, L2), disjuntas_2(T, L2).

