-- Restrições de integridade

-- [R1-1] - Uma Categoria não pode estar contida em si própria.

DROP TRIGGER IF EXISTS chk_categoria_contida_trigger ON tem_outra;

CREATE OR REPLACE FUNCTION chk_categoria_contida_proc()
RETURNS TRIGGER AS
$$
BEGIN
   IF NEW.categoria = NEW.super_categoria THEN
      RAISE EXCEPTION 'Categoria nao pode estar contida em si mesma!';
   END IF;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER chk_categoria_contida_trigger
BEFORE INSERT OR UPDATE ON tem_outra
FOR EACH ROW EXECUTE PROCEDURE chk_categoria_contida_proc();


-- [R1-4] - O número de unidades repostas num Evento de Reposição não pode exceder o 
---------- número de unidades especificado no Planograma.

DROP TRIGGER IF EXISTS chk_unidades_repostas_trigger ON evento_reposicao;

CREATE OR REPLACE FUNCTION chk_unidades_repostas_proc()
RETURNS TRIGGER AS
$$
DECLARE unidades_planograma INTEGER;
BEGIN
   SELECT unidades INTO unidades_planograma
   FROM planograma
   WHERE ean = NEW.ean AND nro = NEW.nro AND
         num_serie = NEW.num_serie AND 
         fabricante = NEW.fabricante;

   IF NEW.unidades > unidades_planograma THEN
      RAISE EXCEPTION 'Numero de unidades reposta excede o especificado no Planograma';
   END IF;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER chk_unidades_repostas_trigger
BEFORE INSERT OR UPDATE ON evento_reposicao
FOR EACH ROW EXECUTE PROCEDURE chk_unidades_repostas_proc();


-- [R1-5] - Um Produto só pode ser reposto numa Prateleira que apresente (pelo menos) 
---------- uma das Categorias desse produto.

DROP TRIGGER IF EXISTS chk_produto_em_prateleira_trigger ON evento_reposicao;

CREATE OR REPLACE FUNCTION chk_produto_em_prateleira_proc()
RETURNS TRIGGER AS
$$
DECLARE numero_categorias_comuns INTEGER;
BEGIN
   SELECT COUNT(*) INTO numero_categorias_comuns
   FROM (SELECT nome
         FROM tem_categoria
         WHERE ean = NEW.ean
         INTERSECT
         SELECT nome
         FROM prateleira
         WHERE nro = NEW.nro AND
            num_serie = NEW.num_serie AND 
            fabricante = NEW.fabricante) t;

   IF numero_categorias_comuns < 1 THEN
      RAISE EXCEPTION 'Produto nao pode ser apresentado na prateleira por esta nao apresentar uma categoria comum';
   END IF;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER chk_produto_em_prateleira_trigger
BEFORE INSERT OR UPDATE ON evento_reposicao
FOR EACH ROW EXECUTE PROCEDURE chk_produto_em_prateleira_proc();
