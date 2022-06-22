#!/usr/bin/python3
from wsgiref.handlers import CGIHandler
from flask import Flask
from flask import render_template, request, redirect, url_for
import psycopg2
import psycopg2.extras

## SGBD configs
DB_HOST = "db.tecnico.ulisboa.pt"
DB_USER = "istxxxx"
DB_DATABASE = DB_USER
DB_PASSWORD = "xxx"
DB_CONNECTION_STRING = "host=%s dbname=%s user=%s password=%s" % (
    DB_HOST,
    DB_DATABASE,
    DB_USER,
    DB_PASSWORD,
)

app = Flask(__name__)

# Auxiliary functions
## Executes queries in sequence at same DB session
### queries = (query, data=None) 
def executeQueries(dbConn, queries, cursor=None):
    def executeQuery(cursor, query, data):
        cursor.execute(query, data) if data else cursor.execute(query)

    if (not cursor):
        cursors = list()
        for query, data in queries:
            cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
            executeQuery(cursor, query, data)
            cursors.append(cursor)
        return cursors
    
    for query, data in queries:
        executeQuery(cursor, query, data)

## Closes DB connections
def closeConnection(dbConn, cursors):
    for cursor in cursors:
        cursor.close()
    dbConn.close()

def checkCursor(cursor, query, cond, data=None, n=1):
    cursor.execute(query, data) if data else cursor.execute(query)
    if n:
        return cond(cursor.fetchmany(n))
    return cond(cursor.fetchall())

## Homepage all routes

@app.route('/')
def index():
    return render_template("index.html", cursor=None)

## 5.a)

# Get category hierarchy
@app.route("/categorias", methods=["GET"])
def list_categorias():
    dbConn = None
    cursors = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursors = executeQueries(dbConn, 
            [("SELECT * FROM categoria;", None),
             ("SELECT * FROM tem_outra;", None)])
        return render_template("categorias.html", cursors=cursors)
    except Exception as e:
        # there is no need to do rollback in get methods
        return str(e)  # Renders a page with the error.
    finally:
        dbConn.commit()
        closeConnection(dbConn, cursors)


### TODO insert category html and route
@app.route("/categorias/nova_categoria")
def create_category():
    try:
        return render_template("categoria_form.html")
    except Exception as e:
        return str(e)


@app.route("/categorias/insert", methods=["POST"])
def insert_category():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        name = request.form["nome"]
        father = request.form["pai"]

        if name == father: # Trigger [R1-1] does this
            raise Exception("Categoria nao pode estar contida em si mesma!")

        name_in = checkCursor(cursor, "SELECT * FROM categoria WHERE nome = %s", lambda x: x != [], data=(name, ))
        father_in = checkCursor(cursor, "SELECT * FROM categoria WHERE nome = %s", lambda x: x != [], data=(father, ))

        if name_in:
            raise Exception("Categoria '%s' existente!" %(name))

        if not father_in:
            raise Exception("Categoria pai '%s' não existe!" %(father))

        cursor.execute("INSERT INTO categoria VALUES (%s);", (name, ))
        cursor.execute("INSERT INTO categoria_simples VALUES (%s);", (name, ))

        father_simpler = checkCursor(cursor, "SELECT * FROM categoria_simples WHERE nome = %s;", lambda x: x != [], data=(father, ))

        if father_simpler:
            cursor.execute("DELETE FROM categoria_simples WHERE nome = %s;", (father, ))
            cursor.execute("INSERT INTO super_categoria VALUES (%s);", (father, ))
        
        cursor.execute("INSERT INTO tem_outra VALUES (%s, %s);", (father, name))

        return redirect(url_for('list_categorias'))
    except Exception as e:
        dbConn.rollback()
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


@app.route("/categorias/remove")
def remove_category():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        category = request.args['categoria']
        # check if category exists (no triggers assigned for this)
        is_super = False
        is_simpler = checkCursor(cursor, "SELECT 1 FROM categoria_simples WHERE nome = %s", lambda t: t, data= (category, ), n=1)
        if not is_simpler:
            is_super = checkCursor(cursor, "SELECT 1 FROM super_categoria WHERE nome = %s", lambda t: t, data= (category, ), n=1)
            if not is_super:
                raise Exception("Category '%s' does not exist" %(category))

        cursor.execute("DELETE FROM evento_reposicao USING produto WHERE evento_reposicao.ean=produto.ean AND produto.cat = %s;", (category, ))
        cursor.execute("DELETE FROM planograma USING produto, prateleira WHERE (planograma.ean=produto.ean AND produto.cat = %s) OR \
                            (planograma.nro=prateleira.nro AND planograma.num_serie=prateleira.num_serie AND \
                                planograma.fabricante=prateleira.fabricante AND prateleira.nome = %s);", (category, category))
        
        cursor.execute("DELETE FROM tem_categoria WHERE nome = %s;", (category, ))
        cursor.execute("DELETE FROM produto WHERE cat = %s;", (category, ))
        
        cursor.execute("DELETE FROM prateleira WHERE nome = %s;", (category, ))
        cursor.execute("DELETE FROM responsavel_por WHERE nome_cat = %s;", (category, ))
        
        cursor.execute("DELETE FROM tem_outra WHERE super_categoria = %s OR categoria = %s;", (category, category))
        
        if is_super:
            cursor.execute("DELETE FROM super_categoria WHERE nome = %s;", (category, ))
        else:
            cursor.execute("DELETE FROM categoria_simples WHERE nome = %s;", (category, ))
        cursor.execute("DELETE FROM categoria WHERE nome = %s;", (category, ))
    
        
        # verificar se a categoria pai nao tiver filhos passar a simples
        staged_fathers = checkCursor(cursor, "SELECT nome FROM super_categoria \
                                                EXCEPT SELECT super_categoria FROM tem_outra;", lambda t: t, data= (category, ), n=None)
        for father in staged_fathers:
            cursor.execute("DELETE FROM super_categoria WHERE nome = %s;", father)
            cursor.execute("INSERT INTO categoria_simples VALUES (%s);", father)

        return redirect(url_for('list_categorias'))
    except Exception as e:
        dbConn.rollback()
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


## 5.b)

@app.route("/retalhistas", methods=["GET"])
def list_retalhistas():
    dbConn = None
    cursors = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursors = executeQueries(dbConn, 
            [("SELECT * FROM retalhista NATURAL JOIN responsavel_por;", None)])

        return render_template("retalhistas.html", cursors=cursors)
    except Exception as e:
        return str(e)  # Renders a page with the error.
    finally:
        dbConn.commit()
        closeConnection(dbConn, cursors)


@app.route("/retalhistas/novo_retalhista")
def create_retalhista():
    try:
        return render_template("novo_retalhista.html")
    except Exception as e:
        return str(e)


@app.route("/retalhistas/insert", methods=["POST"])
def insert_retalhista():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        tin = request.form["tin"]
        nome = request.form["nome"]
        num_serie = request.form["num_serie"]
        fabricante = request.form["fabricante"]
        categoria = request.form["categoria"]
        ean = request.form["ean"]
        nro = request.form["nro"]
        faces = request.form["faces"]
        unidades = request.form["unidades"]
        loc = request.form["loc"]
        # adicionar mais alguma coisa? a que se referia o prod na img?

        cursor.execute("INSERT INTO retalhista VALUES (%s, %s);", (tin, nome, ))
        cursor.execute("INSERT INTO responsavel_por VALUES (%s, %s, %s, %s);", (categoria, tin, num_serie, fabricante, ))
        cursor.execute("INSERT INTO planograma VALUES (%s, %s, %s, %s, %s, %s, %s);", (ean, nro, num_serie, fabricante, faces, unidades, loc, ))

        return redirect(url_for('list_retalhistas'))
    except Exception as e:
        dbConn.rollback()
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


@app.route("/retalhistas/remove")
def remove_retalhista():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        tin = request.args['tin']
    
        executeQueries(dbConn, [    
            ("DELETE FROM evento_reposicao WHERE tin = %s;", (tin, )),
            ("DELETE FROM retalhista WHERE tin = %s;", (tin, )),
            ("DELETE FROM responsavel_por WHERE tin = %s);", (tin, ))], cursor)

        return redirect(url_for('list_retalhistas'))
    except Exception as e:
        dbConn.rollback()
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


## 5.c)

@app.route("/ivms", methods=["GET"])
def list_ivms():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        cursor.execute("SELECT * FROM IVM;")

        return render_template("ivms.html", cursor=cursor)
    except Exception as e:
        # there is no need to do rollback in get methods
        return str(e)  # Renders a page with the error.
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


@app.route("/ivms/eventos", methods=["GET"]) # post?
def list_eventos():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        num_serie = request.args['num_serie']
        fabricante = request.args['fabricante']

        # Here
        cursor.execute("SELECT nome, SUM(unidades) FROM evento_reposicao \
            INNER JOIN tem_categoria ON evento_reposicao.ean=tem_categoria.ean \
            WHERE num_serie = %s AND fabricante = %s \
            GROUP BY nome;", (num_serie, fabricante, ))

        return render_template("eventos.html", cursor=cursor) # confirm
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


## 5.d)

@app.route("/categorias/subcategorias", methods=["GET"])
def list_sub_categories():
    dbConn = None
    cursor = None
    try:
        dbConn = psycopg2.connect(DB_CONNECTION_STRING)
        cursor = dbConn.cursor(cursor_factory=psycopg2.extras.DictCursor)
        super_categoria = request.args['super_categoria']

        # confirm this
        cursor.execute("WITH RECURSIVE subcats AS ( \
            SELECT super_categoria, categoria FROM tem_outra WHERE super_categoria = %s \
            UNION \
            SELECT t.super_categoria, t.categoria FROM tem_outra t INNER JOIN subcats s ON s.categoria=t.super_categoria \
        ) SELECT * FROM subcats;", (super_categoria, ))

        return render_template("subcategorias.html", cursor=cursor)
    except Exception as e:
        return str(e)
    finally:
        dbConn.commit()
        cursor.close()
        dbConn.close()


CGIHandler().run(app)
