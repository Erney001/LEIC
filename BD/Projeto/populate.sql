DROP TABLE categoria CASCADE;
DROP TABLE categoria_simples CASCADE;
DROP TABLE super_categoria CASCADE;
DROP TABLE tem_outra CASCADE;
DROP TABLE produto CASCADE;
DROP TABLE tem_categoria CASCADE;
DROP TABLE IVM CASCADE;
DROP TABLE ponto_de_retalho CASCADE;
DROP TABLE instalada_em CASCADE;
DROP TABLE prateleira CASCADE;
DROP TABLE planograma CASCADE;
DROP TABLE retalhista CASCADE;
DROP TABLE responsavel_por CASCADE;
DROP TABLE evento_reposicao CASCADE;

CREATE TABLE categoria (
    nome VARCHAR(40),
    CONSTRAINT pk_categoria PRIMARY KEY (nome)
);

CREATE TABLE categoria_simples (
    nome VARCHAR(40),
    CONSTRAINT pk_categoria_simples PRIMARY KEY (nome),
    CONSTRAINT fk_categoria_simples_categoria FOREIGN KEY (nome) REFERENCES categoria(nome)
);

CREATE TABLE super_categoria (
    nome VARCHAR(40),
    CONSTRAINT pk_super_categoria PRIMARY KEY (nome),
    CONSTRAINT fk_super_categoria_categoria FOREIGN KEY (nome) REFERENCES categoria(nome)
);

CREATE TABLE tem_outra (
    super_categoria VARCHAR(40) NOT NULL,
    categoria VARCHAR(40),
    CONSTRAINT pk_tem_outra PRIMARY KEY (categoria),
    CONSTRAINT fk_tem_outra_super_categoria FOREIGN KEY (super_categoria) REFERENCES super_categoria(nome),
    CONSTRAINT fk_tem_outra_categoria FOREIGN KEY (categoria) REFERENCES categoria(nome)
);

CREATE TABLE produto (
    ean INTEGER,
    cat VARCHAR(40) NOT NULL,
    descr VARCHAR(40),
    CONSTRAINT pk_produto PRIMARY KEY (ean),
    CONSTRAINT fk_produto_categoria FOREIGN KEY (cat) REFERENCES categoria(nome)
);

CREATE TABLE tem_categoria (
    ean INTEGER,
    nome VARCHAR(40),
    CONSTRAINT pk_tem_categoria PRIMARY KEY (ean, nome),
    CONSTRAINT fk_tem_categoria_produto FOREIGN KEY (ean) REFERENCES produto(ean),
    CONSTRAINT fk_tem_categoria_categoria FOREIGN KEY (nome) REFERENCES categoria(nome)
);

CREATE TABLE IVM (
    num_serie INTEGER,
    fabricante VARCHAR(40),
    CONSTRAINT pk_IVM PRIMARY KEY (num_serie, fabricante)
);

CREATE TABLE ponto_de_retalho (
    nome VARCHAR(40),
    distrito VARCHAR(40) NOT NULL,
    concelho VARCHAR(40) NOT NULL,
    CONSTRAINT pk_ponto_de_retalho PRIMARY KEY (nome)
);

CREATE TABLE instalada_em (
    num_serie INTEGER,
    fabricante VARCHAR(40),
    local VARCHAR(40) NOT NULL,
    CONSTRAINT pk_instalada_em PRIMARY KEY (num_serie, fabricante),
    CONSTRAINT fk_instalada_em_IVM FOREIGN KEY (num_serie, fabricante) REFERENCES IVM(num_serie, fabricante),
    CONSTRAINT fk_instalada_em_ponto_de_retalho FOREIGN KEY (local) REFERENCES ponto_de_retalho(nome) -- 3
);

CREATE TABLE prateleira (
    nro INTEGER,
    num_serie INTEGER,
    fabricante VARCHAR(40),
    altura NUMERIC(4, 2) NOT NULL CHECK(altura > 0), -- numeros do genero AB.CD
    nome VARCHAR(40) NOT NULL,
    CONSTRAINT pk_prateleira PRIMARY KEY (nro, num_serie, fabricante),
    CONSTRAINT fk_prateleira_IVM FOREIGN KEY (num_serie, fabricante) REFERENCES IVM(num_serie, fabricante),
    CONSTRAINT fk_prateleira_categoria FOREIGN KEY (nome) REFERENCES categoria(nome)
);

CREATE TABLE planograma (
    ean INTEGER,
    nro INTEGER,
    num_serie INTEGER,
    fabricante VARCHAR(40),
    faces INTEGER NOT NULL CHECK(faces >= 0),
    unidades INTEGER NOT NULL CHECK(unidades > 0),
    loc VARCHAR(40) NOT NULL,
    CONSTRAINT pk_planograma PRIMARY KEY (ean, nro, num_serie, fabricante),
    CONSTRAINT fk_planograma_produto FOREIGN KEY (ean) REFERENCES produto(ean),
    CONSTRAINT fk_planograma_prateleira FOREIGN KEY (nro, num_serie, fabricante) REFERENCES prateleira(nro, num_serie, fabricante)
);

CREATE TABLE retalhista (
    tin INTEGER,
    name VARCHAR(40) NOT NULL UNIQUE,
    CONSTRAINT pk_retalhista PRIMARY KEY (tin)
);

CREATE TABLE responsavel_por (
    nome_cat VARCHAR(40) NOT NULL,
    tin INTEGER NOT NULL,
    num_serie INTEGER,
    fabricante VARCHAR(40),
    CONSTRAINT pk_responsavel_por PRIMARY KEY (num_serie, fabricante),
    CONSTRAINT fk_responsavel_por_IVM FOREIGN KEY (num_serie, fabricante) REFERENCES IVM(num_serie, fabricante),
    CONSTRAINT fk_responsavel_por_retalhista FOREIGN KEY (tin) REFERENCES retalhista(tin),
    CONSTRAINT fk_responsavel_por_categoria FOREIGN KEY (nome_cat) REFERENCES categoria(nome)
);

CREATE TABLE evento_reposicao (
    ean INTEGER,
    nro INTEGER,
    num_serie INTEGER,
    fabricante VARCHAR(40),
    instante TIMESTAMP,
    unidades INTEGER NOT NULL CHECK(unidades > 0),
    tin INTEGER NOT NULL,
    CONSTRAINT pk_evento_reposicao PRIMARY KEY (ean, nro, num_serie, fabricante, instante),
    CONSTRAINT fk_evento_reposicao_planograma FOREIGN KEY (ean, nro, num_serie, fabricante) REFERENCES planograma(ean, nro, num_serie, fabricante),
    CONSTRAINT fk_evento_reposicao_retalhista FOREIGN KEY (tin) REFERENCES retalhista(tin)
);

---------------------------------------------------------------------------------------------------

INSERT INTO categoria VALUES ('Refrigerantes');
INSERT INTO categoria VALUES ('Frios');
INSERT INTO categoria VALUES ('Congelados');
INSERT INTO categoria VALUES ('Gelados');
INSERT INTO categoria VALUES ('Frutos');

INSERT INTO categoria_simples VALUES ('Refrigerantes');
INSERT INTO categoria_simples VALUES ('Congelados');
INSERT INTO categoria_simples VALUES ('Gelados');
INSERT INTO categoria_simples VALUES ('Frutos');

INSERT INTO super_categoria VALUES ('Frios');

INSERT INTO tem_outra VALUES ('Frios', 'Congelados');
INSERT INTO tem_outra VALUES ('Frios', 'Gelados');

INSERT INTO produto VALUES (0000000000001, 'Congelados', 'Camaroes');
INSERT INTO produto VALUES (0000000000002, 'Gelados', 'Cornetto');
INSERT INTO produto VALUES (0000000000003, 'Gelados', 'Magnum');
INSERT INTO produto VALUES (0000000000004, 'Frios', 'Iogurte');
INSERT INTO produto VALUES (0000000000005, 'Refrigerantes', 'Coca-cola');
INSERT INTO produto VALUES (0000000000006, 'Frutos', 'Ameixas');
INSERT INTO produto VALUES (0000000000007, 'Frutos', 'Amoras');
INSERT INTO produto VALUES (0000000000008, 'Refrigerantes', 'Ale');

INSERT INTO tem_categoria VALUES (0000000000001, 'Congelados');
INSERT INTO tem_categoria VALUES (0000000000002, 'Gelados');
INSERT INTO tem_categoria VALUES (0000000000003, 'Gelados');
INSERT INTO tem_categoria VALUES (0000000000004, 'Frios');
INSERT INTO tem_categoria VALUES (0000000000005, 'Refrigerantes');
INSERT INTO tem_categoria VALUES (0000000000006, 'Frutos');
INSERT INTO tem_categoria VALUES (0000000000007, 'Frutos');
INSERT INTO tem_categoria VALUES (0000000000008, 'Refrigerantes');

INSERT INTO IVM VALUES (0001, 'Vending Machines Lda');
INSERT INTO IVM VALUES (0002, 'Vending Machines Lda');
INSERT INTO IVM VALUES (0003, 'Vending Machines Lda');
INSERT INTO IVM VALUES (0004, 'Vending Machines Lda');
INSERT INTO IVM VALUES (0005, 'Vending Machines Lda');

INSERT INTO ponto_de_retalho VALUES ('Galp', 'Lisboa', 'Oeiras');
INSERT INTO ponto_de_retalho VALUES ('Repsol', 'Lisboa', 'Oeiras');

INSERT INTO instalada_em VALUES (0001, 'Vending Machines Lda', 'Galp');
INSERT INTO instalada_em VALUES (0002, 'Vending Machines Lda', 'Repsol');
INSERT INTO instalada_em VALUES (0003, 'Vending Machines Lda', 'Galp');
INSERT INTO instalada_em VALUES (0004, 'Vending Machines Lda', 'Repsol');
INSERT INTO instalada_em VALUES (0005, 'Vending Machines Lda', 'Repsol');

INSERT INTO prateleira VALUES (1, 0001, 'Vending Machines Lda', 0.2, 'Gelados');
INSERT INTO prateleira VALUES (2, 0001, 'Vending Machines Lda', 0.2, 'Gelados');
INSERT INTO prateleira VALUES (3, 0001, 'Vending Machines Lda', 0.3, 'Refrigerantes');
INSERT INTO prateleira VALUES (1, 0002, 'Vending Machines Lda', 0.2, 'Frios');
INSERT INTO prateleira VALUES (2, 0002, 'Vending Machines Lda', 0.2, 'Congelados');
INSERT INTO prateleira VALUES (3, 0002, 'Vending Machines Lda', 0.3, 'Refrigerantes');
INSERT INTO prateleira VALUES (1, 0003, 'Vending Machines Lda', 0.2, 'Frios');
INSERT INTO prateleira VALUES (2, 0003, 'Vending Machines Lda', 0.2, 'Congelados');
INSERT INTO prateleira VALUES (3, 0003, 'Vending Machines Lda', 0.3, 'Refrigerantes');
INSERT INTO prateleira VALUES (1, 0004, 'Vending Machines Lda', 0.2, 'Frios');
INSERT INTO prateleira VALUES (2, 0004, 'Vending Machines Lda', 0.2, 'Congelados');
INSERT INTO prateleira VALUES (3, 0004, 'Vending Machines Lda', 0.3, 'Refrigerantes');
INSERT INTO prateleira VALUES (1, 0005, 'Vending Machines Lda', 0.2, 'Frutos');
INSERT INTO prateleira VALUES (2, 0005, 'Vending Machines Lda', 0.2, 'Frutos');
INSERT INTO prateleira VALUES (3, 0005, 'Vending Machines Lda', 0.3, 'Refrigerantes');

INSERT INTO planograma VALUES (0000000000002, 1, 0001, 'Vending Machines Lda', 2, 6, 'A');
INSERT INTO planograma VALUES (0000000000003, 1, 0001, 'Vending Machines Lda', 2, 4, 'A');
INSERT INTO planograma VALUES (0000000000002, 2, 0001, 'Vending Machines Lda', 4, 6, 'A');
INSERT INTO planograma VALUES (0000000000003, 2, 0001, 'Vending Machines Lda', 2, 6, 'A');
INSERT INTO planograma VALUES (0000000000005, 3, 0001, 'Vending Machines Lda', 4, 8, 'A');
INSERT INTO planograma VALUES (0000000000003, 1, 0002, 'Vending Machines Lda', 2, 6, 'B');
INSERT INTO planograma VALUES (0000000000004, 1, 0002, 'Vending Machines Lda', 2, 8, 'B');
INSERT INTO planograma VALUES (0000000000005, 3, 0002, 'Vending Machines Lda', 2, 6, 'B');
INSERT INTO planograma VALUES (0000000000005, 3, 0003, 'Vending Machines Lda', 2, 4, 'C');
INSERT INTO planograma VALUES (0000000000005, 3, 0004, 'Vending Machines Lda', 2, 8, 'D');

INSERT INTO retalhista VALUES (0001, 'Continente');
INSERT INTO retalhista VALUES (0002, 'Pingo Doce');
INSERT INTO retalhista VALUES (0003, 'Auchan');
INSERT INTO retalhista VALUES (0004, 'Lidl');

INSERT INTO responsavel_por VALUES ('Gelados', 0001, 0001, 'Vending Machines Lda');
INSERT INTO responsavel_por VALUES ('Frios', 0002, 0002, 'Vending Machines Lda');
INSERT INTO responsavel_por VALUES ('Refrigerantes', 0001, 0003, 'Vending Machines Lda');
INSERT INTO responsavel_por VALUES ('Congelados', 0001, 0004, 'Vending Machines Lda');
INSERT INTO responsavel_por VALUES ('Frutos', 0003, 0005, 'Vending Machines Lda');

INSERT INTO evento_reposicao VALUES (0000000000002, 1, 0001, 'Vending Machines Lda', '2022-06-09 08:20:17', 4, 0001);
INSERT INTO evento_reposicao VALUES (0000000000002, 2, 0001, 'Vending Machines Lda', '2022-05-08 14:02:21', 2, 0002);
INSERT INTO evento_reposicao VALUES (0000000000005, 3, 0001, 'Vending Machines Lda', '2022-06-11 15:47:04', 8, 0002);

-- Resultado esperado para as queries pedidas na parte 3:
--  1: 1 - Continente
--  2: 1 - Continente
--  3: 1 - Camaroes, 3 - Magnum, 4 - Iogurte
--  4: 5 - Coca-cola
