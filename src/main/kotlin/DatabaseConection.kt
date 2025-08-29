package com.jmvn.proyectos

import java.sql.DriverManager

class DatabaseConection (private val url : String ) {

    var totalFactura = 0.0
    var codigoArticulo = 0
    var cliente_id = 0
    var cedula = 0
    var items = mutableListOf<Item>()

    fun validarCliente (){

        do {
            println("digite su numero de cedula: ")
            cedula = readln().toInt()
            val validarCliente = consultarCedula(cedula)
        } while (validarCliente == 0)
    }

    fun timbrarArticulos (){
        var comprar = "si"
        while (comprar == "si"){
            do {
                print("\ntimbrar articulo: ")
                codigoArticulo = readln().toInt()
                var encontrado = buscarPrecioArticulo(codigoArticulo)
            } while (encontrado == 0.0)
            print("cantidad de articulos: ")
            var cantidad: Int = readln().toInt()
            print("desea llevar otro articulo si/no: ")
            comprar = readln().lowercase()
            var precio: Double = buscarPrecioArticulo(codigoArticulo)
            var subtotal = precio * cantidad
            var idArticulo = buscarIdArticulo(codigoArticulo)
            totalFactura += subtotal
            var item = Item(idArticulo,cantidad,subtotal)
            items.add(item)
        }
    }

    fun guardarFactura () {
        var facturaId = 0
        cliente_id = consultarCedula(cedula)
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                it.executeUpdate("INSERT INTO facturas (cliente_id, total) VALUES ($cliente_id, $totalFactura)")
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


    fun consultarCedula (cedula: Int): Int{
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
                        val idCliente = resultSet.getInt("id")
                        return idCliente
                    }
                }
            }
        }
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
}