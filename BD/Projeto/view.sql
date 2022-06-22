CREATE OR REPLACE VIEW vendas AS
SELECT tem_categoria.ean,
tem_categoria.nome AS cat,
EXTRACT(YEAR FROM evento_reposicao.instante) AS ano,
EXTRACT(QUARTER FROM evento_reposicao.instante) AS trimestre,
EXTRACT(MONTH FROM evento_reposicao.instante) AS mes,
EXTRACT(DAY FROM evento_reposicao.instante) AS dia_mes,
EXTRACT(DOW FROM evento_reposicao.instante) AS dia_semana,
ponto_de_retalho.distrito,
ponto_de_retalho.concelho,
evento_reposicao.unidades
FROM tem_categoria
JOIN evento_reposicao ON tem_categoria.ean=evento_reposicao.ean
JOIN instalada_em ON evento_reposicao.num_serie=instalada_em.num_serie AND evento_reposicao.fabricante=instalada_em.fabricante
JOIN ponto_de_retalho ON ponto_de_retalho.nome=instalada_em.local;
