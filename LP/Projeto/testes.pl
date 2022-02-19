% corre_verificacao([a,i,a], [a, i,a], [[a,i,a],[i,d,a]], R)

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


nao_membro(_, []):-!.
nao_membro(El, [H|T]):-El \== H, nao_membro(El, T).
