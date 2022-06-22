-- query 1

SELECT name
FROM retalhista
WHERE tin IN (
    SELECT tin
    FROM responsavel_por
    GROUP BY tin
    HAVING COUNT(nome_cat) >= ALL (
        SELECT COUNT(nome_cat)
        FROM responsavel_por
        GROUP BY tin
    )
);

-- query 2

SELECT name
FROM retalhista 
NATURAL JOIN responsavel_por
GROUP BY tin
HAVING COUNT(DISTINCT nome_cat) = (
    SELECT COUNT(*)
    FROM categoria_simples
);

-- query 3

SELECT ean
FROM produto
EXCEPT
SELECT ean
FROM evento_reposicao
ORDER BY ean;

-- query 4

SELECT ean
FROM evento_reposicao
GROUP BY ean
HAVING COUNT(DISTINCT tin) = 1;
