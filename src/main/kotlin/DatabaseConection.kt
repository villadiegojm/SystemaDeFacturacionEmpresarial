package com.jmvn.proyectos

import java.sql.DriverManager

class DatabaseConection (private val url : String ) {

    val sql = """SELECT
                 f.factura_numero,
                 f.fecha,
                 f.total,
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

    fun validarDatosCliente (cedula : Int) : List<Cliente> {
        val datos = mutableListOf<Cliente>()
        val connection = DriverManager.getConnection(url)
        val statement = connection.createStatement()
        statement.use {
            val ResultSet = it.executeQuery("SELECT * FROM clientes WHERE cedula = $cedula")
            ResultSet.use {
                val nombre = ResultSet.getString("nombre")
                val cedula = ResultSet.getInt("cedula")
                val telefono = ResultSet.getInt("telefono")
                println("\nCLIENTE:  $nombre")
                println("CEDULA:   $cedula")
                println("TELEFONO: $telefono")
            }
        }
        return datos
    }

    fun timbrarArticulos (codigoArticulo: Int, totalFactura: Double, items: MutableList<Item>) : Pair<Double, MutableList<Item>>{
        var total = totalFactura

            print("cantidad de articulos: ")
            var cantidad: Int = readln().toInt()
            var precio: Double = buscarPrecioArticulo(codigoArticulo)
            var subtotal = precio * cantidad
            var idArticulo = buscarIdArticulo(codigoArticulo)
            total += subtotal
            var item = Item(idArticulo,cantidad,subtotal)
            items.add(item)

        return Pair(total, items)
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

    fun consultarIdCliente (cedula: Int): Int{
        var clienteId = 0
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM clientes WHERE cedula= $cedula")
                resultSet.use {
                    if (!resultSet.next()) {
                        println("\n***LA CEDULA NO EXISTE***")
                        return 0
                    } else {
                        clienteId = resultSet.getInt("id")
                    }
                }
            }
        }
        return clienteId
    }

    fun consultarCedula (cedula: Int): Boolean{
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM clientes WHERE cedula= $cedula")
                resultSet.use {
                    if (!resultSet.next()) {
                        println("\n***LA CEDULA NO EXISTE***")
                        return false
                    } else {
                        return true
                    }
                }
            }
        }
    }

    fun validarDatosArticulo (codigo: Int) : List<Cliente> {
        val datos = mutableListOf<Cliente>()
        val connection = DriverManager.getConnection(url)
        val statement = connection.createStatement()
        statement.use {
            val ResultSet = it.executeQuery("SELECT * FROM articulos WHERE codigo = $codigo")
            ResultSet.use {
                val nombre = ResultSet.getString("nombre")
                val precio = ResultSet.getDouble("precio")
                val descripcion = ResultSet.getString("descripcion")
                println("\nARTICULO:      $nombre")
                println("DESCRIPCION:   $descripcion")
                println("PRECIO:        $precio")
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

    fun buscarIdArticulo (codigo:Int): Int{
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM articulos WHERE codigo = $codigo")
                resultSet.use {
                    if (!resultSet.next()){
                        println("\n**ARTICULO NO EXISTE. INGRESE UN CODIGO VALIDO**")
                        return 0
                    }else {
                        val idArticulo = resultSet.getInt("id")
                        return idArticulo
                    }
                }
            }
        }
    }

    fun listarFacturas (): MutableList<Factura>{
        val facturas = mutableListOf<Factura>()
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM facturas")
                resultSet.use {
                    while (resultSet.next()){
                        val numero = resultSet.getInt("factura_numero")
                        val cliente_id = resultSet.getInt("cliente_id")
                        val total = resultSet.getDouble("total")
                        val fecha = resultSet.getDate("fecha")
                        val factura = Factura(numero,cliente_id,total,fecha)
                        facturas.add(factura)
                    }
                }
            }
        }
        return facturas
    }

    fun imprimirFactura () {
        var numero = 0
        var encontrarFactura = false
        do {
            print("NUMERO DE FACTURA QUE DESEA IMPRIMIR: ")
            numero = readln().toInt()
            encontrarFactura = validarFactura(numero)
        }while (encontrarFactura == false)
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.prepareStatement(sql)
            statement.use {
                it.setInt(1,numero)
                val resultSet = statement.executeQuery()
                println("\n=============================================")
                var encabezadoFactura = false
                var total = 0.0
                while (resultSet.next()){
                    if (encabezadoFactura == false){
                        println("FACTURA NUMERO: ${resultSet.getInt("factura_numero")}")
                        println("CLIENTE: ${resultSet.getString("nombre_cliente")}")
                        println("TELEFONO: ${resultSet.getInt("telefono_cliente")}")
                        println("FECHA: ${resultSet.getDate("fecha")}\n")
                        total = resultSet.getDouble("total")
                        encabezadoFactura = true
                        println("-------------------RESUMEN-------------------")
                        println("codigo | nombre | precio un | cant | subtotal")
                    }
                    val codigo = resultSet.getInt("codigo_articulo")
                    val nombre = resultSet.getString("nombre_articulo")
                    val precio = resultSet.getDouble("precio_articulo")
                    val cantidad = resultSet.getInt("cantidad")
                    val subtotal = resultSet.getDouble("subtotal")
                    println("$codigo     $nombre     $precio     $cantidad     $subtotal")
                }
                println("=============================================")
                println("TOTAL FACTURA:________________________$total")
            }
        }
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
}