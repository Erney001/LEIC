-- 6.1
SELECT dia_semana, concelho, SUM(unidades)
FROM vendas
WHERE ano >= 2022 AND trimestre >= 2 AND mes >= 6 AND dia_mes >= 1
    AND ano < 2023 AND trimestre < 3 AND mes < 8 AND dia_mes < 20
GROUP BY GROUPING SETS ((dia_semana), (concelho), ());
 
-- 6.2
SELECT concelho, cat, dia_semana, SUM(unidades)
FROM vendas
WHERE distrito = 'Lisboa'
GROUP BY GROUPING SETS ((concelho, cat, dia_semana), ());

-- 7.1

-- Indice Hash para nome_cat em responsavel_por
-- Iria melhorar o tempo de execucao da segunda igualdade, ao comparar
-- o nome_cat com o nome fixo (com certa hash) 'Frutos'

CREATE INDEX nome_cat_index ON responsavel_por USING HASH(nome_cat);

-- 7.2

-- Indice Btree para descr em produto
-- Iria melhorar o tempo de execucao do 'like', ao usar uma Btree para
-- separar as descr que comecam por 'A' e as que nao comecam

CREATE INDEX descr_index ON produto(descr);
