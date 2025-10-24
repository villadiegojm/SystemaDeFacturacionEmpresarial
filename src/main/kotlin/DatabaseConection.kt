package com.jmvn.proyectos

import java.sql.DriverManager

class DatabaseConection (private val url : String ) {

    val sql = """SELECT
                 f.factura_numero,
                 f.fecha,
                 f.total,
                 c.id AS cliente_id,
                 c.nombre  AS nombre_cliente,
                 c.telefono  AS telefono_cliente,
                 a.codigo   AS codigo_articulo,
                 a.nombre   AS nombre_articulo,
                 a.precio   AS precio_articulo,
                 i.cantidad,
                 i.subtotal
             FROM facturas  f
             JOIN clientes  c ON f.cliente_id = c.id
             JOIN items     i ON f.factura_numero = i.factura_id
             JOIN articulos a ON i.articulo_id = a.id
             WHERE f.factura_numero = ?;"""

    fun consultarDatosCliente (cedula : Int) : Cliente {
        val cliente : Cliente
        val connection = DriverManager.getConnection(url)
        val statement = connection.createStatement()
        statement.use {
            val resultSet = it.executeQuery("SELECT * FROM clientes WHERE cedula = $cedula")
            resultSet.use {
                val nombre = resultSet.getString("nombre")
                val cedula = resultSet.getInt("cedula")
                val telefono = resultSet.getInt("telefono")
                val estado = resultSet.getString("estado")
                cliente = Cliente(nombre, cedula, telefono, estado)
            }
        }
        return cliente
    }

    fun guardarFactura (clienteId: Int, totalFactura: Double,items: MutableList<Item>) {
        var facturaId = 0
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                it.executeUpdate("INSERT INTO facturas (cliente_id, total) VALUES ($clienteId, $totalFactura)")
                val resultSet = it.generatedKeys
                if (resultSet.next()){
                    facturaId = resultSet.getInt(1)
                }
            }
            guardarItems(items, facturaId)
        }
    }

    fun guardarItems (items: List<Item>, facturaId: Int){
        val sql = "INSERT INTO items (factura_id, articulo_id, cantidad, subtotal) VALUES (?, ?, ?, ?)"
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.prepareStatement(sql)
            statement.use {
                for (item in items){
                    it.setInt(1,facturaId)
                    it.setInt(2,item.id)
                    it.setInt(3,item.cantidad)
                    it.setDouble(4,item.subtotal)
                    statement.addBatch()
                }
                statement.executeBatch()
            }
        }
    }

    fun consultarCedula (cedula: Int, mensaje: String): Pair<Boolean,Int>{
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM clientes WHERE cedula= $cedula")
                resultSet.use {
                    if (!resultSet.next()) {
                        println("\n$mensaje")
                        return Pair(false,0)
                    } else {
                         val clienteId = resultSet.getInt("id")
                        return Pair(true,clienteId)
                    }
                }
            }
        }
    }

    fun consultarDatosArticulo (codigo: Int) : List<Articulo> {
        val datos = mutableListOf<Articulo>()
        val connection = DriverManager.getConnection(url)
        val statement = connection.createStatement()
        statement.use {
            val resultSet = it.executeQuery("SELECT * FROM articulos WHERE codigo = $codigo")
            resultSet.use {
                val nombre = resultSet.getString("nombre")
                val precio = resultSet.getDouble("precio")
                val descripcion = resultSet.getString("descripcion")
                val cantidadStock = resultSet.getInt("cantidadstok")
                val articulo = Articulo(codigo,nombre,precio,descripcion,cantidadStock)
                datos.add(articulo)
            }
        }
        return datos
    }

    fun buscarPrecioArticulo (codigo : Int): Double  {
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM articulos WHERE codigo = $codigo")
                resultSet.use {
                    if (!resultSet.next()){
                        println("\n**ARTICULO NO EXISTE. INGRESE UN CODIGO VALIDO**")
                        return 0.0
                    }else {
                        val precio = resultSet.getDouble("precio")
                        return precio
                    }
                }
            }
        }
    }

    fun buscarIdArticulo (codigo:Int, mensaje: String): Pair<Boolean, Int> {
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM articulos WHERE codigo = $codigo")
                resultSet.use {
                    if (!resultSet.next()){
                        println(mensaje)
                        return Pair(false, 0)
                    }else {
                        val idArticulo = resultSet.getInt("id")
                        return Pair(true, idArticulo)
                    }
                }
            }
        }
    }

    fun actualizarStock(id: Int, cantidad : Int) {
        val sql = "UPDATE articulos SET cantidadstok = cantidadstok - ? WHERE id = $id"
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.prepareStatement(sql)
            statement.setInt(1,cantidad)
            statement.use {
                it.executeUpdate()
            }
        }
    }

    fun listarFacturas (): MutableList<ListadoFacturas>{
        val sql = """SELECT
                        f.factura_numero,
                        f.cliente_id,
                        f.total,
                        f.fecha,
                        c.nombre
                       FROM facturas f
                       LEFT JOIN clientes c ON f.cliente_id = c.id
                       WHERE c.nombre IS NOT NULL;"""
        val facturas = mutableListOf<ListadoFacturas>()
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.prepareStatement(sql)
            statement.use {
                val resultSet = it.executeQuery()
                resultSet.use {
                    while (resultSet.next()){
                        val numero = resultSet.getInt("factura_numero")
                        val cliente_id = resultSet.getInt("cliente_id")
                        val total = resultSet.getDouble("total")
                        val fecha = resultSet.getDate("fecha")
                        val nombre = resultSet.getString("nombre")
                        val factura = ListadoFacturas(numero,nombre,total,fecha)
                        facturas.add(factura)
                    }
                }
            }
        }
        return facturas
    }

    fun validarFactura (numeroFactura : Int) : Boolean {
        var facturaExistente = true
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resulset = it.executeQuery("SELECT * FROM facturas WHERE factura_numero = $numeroFactura")
                resulset.use {
                    if (!resulset.next()){
                        println("***LA FACTURA NO EXISTE***\n")
                        facturaExistente = false
                    }
                }
            }
        }
        return facturaExistente
    }

    fun datosFactura (numeroFactura :Int) :Pair<Factura,Cliente>? {
        var sql = """SELECT 
                        f.factura_numero,
                        f.cliente_id,
                        f.total,
                        f.fecha,
                        c.nombre,
                        c.cedula,
                        c.telefono,
                        c.estado
                    FROM facturas f 
                    JOIN clientes c ON f.cliente_id = c.id
                    WHERE f.factura_numero = ?; """
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.prepareStatement(sql)
            statement.use {
                statement.setInt(1,numeroFactura)
                val resultSet = statement.executeQuery()
                if (resultSet.next()) {
                    val numeroFactura = resultSet.getInt("factura_numero")
                    val cliente_id = resultSet.getInt("cliente_id")
                    val fecha = resultSet.getDate("fecha")
                    val total = resultSet.getDouble("total")
                    val nombre = resultSet.getString("nombre")
                    val cedula = resultSet.getInt("cedula")
                    val telefono = resultSet.getInt("telefono")
                    val estado = resultSet.getString("estado")
                    val factura = Factura(numeroFactura, cliente_id, total, fecha)
                    val cliente = Cliente(nombre, cedula, telefono, estado)
                    return Pair(factura,cliente)
                }else return null
            }
        }
    }

    fun detallesFactura (numeroFactura: Int) : List<DetallesFactura>{
        val detalles = mutableListOf<DetallesFactura>()
        val sql = """SELECT
                        a.codigo,
                        a.nombre,
                        a.precio,
                        i.cantidad,
                        i.subtotal
                    FROM items i
                    JOIN facturas f  ON i.factura_id = f.factura_numero
                    JOIN articulos a ON i.articulo_id = a.id
                    WHERE f.factura_numero = ?;"""
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.prepareStatement(sql)
            statement.use {
                statement.setInt(1, numeroFactura)
                val resultSet = statement.executeQuery()
                while (resultSet.next()){
                    val codigo = resultSet.getInt("codigo")
                    val nombre = resultSet.getString("nombre")
                    val precio = resultSet.getDouble("precio")
                    val cantidad = resultSet.getInt("cantidad")
                    val subtotal = resultSet.getDouble("subtotal")
                    val detalle = DetallesFactura(codigo, nombre, precio, cantidad, subtotal)
                    detalles.add(detalle)
                }
            }
        }
        return detalles
    }

    fun listarArticulos (): MutableList<Articulo>{
        val articulos = mutableListOf<Articulo>()
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM articulos ORDER BY codigo")
                resultSet.use {
                    while (resultSet.next()){
                        val id = resultSet.getInt("id")
                        val codigo = resultSet.getInt("codigo")
                        val nombre = resultSet.getString("nombre")
                        val precio = resultSet.getDouble("precio")
                        val descripcion = resultSet.getString("descripcion")
                        val cantidadStok = resultSet.getInt("cantidadStok")
                        val articulo = Articulo(codigo,nombre,precio,descripcion,cantidadStok)
                        articulos.add(articulo)
                    }
                }

            }

        }
        return articulos
    }

    fun listarClientes (): MutableList<Cliente>{

        val listaClientes = mutableListOf<Cliente>()
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM clientes ORDER BY nombre")
                resultSet.use {
                    while (resultSet.next()){
                        val id = resultSet.getInt("id")
                        val nombre = resultSet.getString("nombre")
                        val cedula = resultSet.getInt("cedula")
                        val telefono = resultSet.getInt("telefono")
                        val estado = resultSet.getString("estado")
                        listaClientes.add(Cliente(nombre,cedula,telefono,estado))

                    }
                }
            }
        }
        return listaClientes
    }

    fun registrarCliente (nombre: String, cedula: Int, telefono: Int, estado: String):Int{
        val sql = "INSERT INTO clientes (nombre, cedula, telefono, estado ) VALUES (?, ?, ?, ?)"
        val conn = DriverManager.getConnection(url)
        conn.use {
            val statement = it.prepareStatement(sql)
            statement.use {
                it.setString(1,nombre)
                it.setInt(2,cedula)
                it.setInt(3,telefono)
                it.setString(4,estado)
                it.executeUpdate()
                println("\n***REGISTRO EXITOSO***")
            }
        }
        return cedula
    }

    fun crearArticulo (codigo: Int, nombre: String, precio: Double, descripcion: String, cantidadDeStock: Int){
        val sql = "INSERT INTO articulos (codigo, nombre, precio, descripcion, cantidadStok) VALUES (?, ?, ?, ?, ?)"
        val conn = DriverManager.getConnection(url)
        conn.use {
            val statement = it.prepareStatement(sql)
            statement.use {
                it.setInt(1,codigo)
                it.setString(2,nombre)
                it.setDouble(3,precio)
                it.setString(4,descripcion)
                it.setInt(5,cantidadDeStock)
                it.executeUpdate()
            }
        }
    }
}