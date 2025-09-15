package com.jmvn.proyectos

import java.sql.DriverManager

class ClienteDatabaseManager (private val url : String) {

    val sql1 = """INSERT INTO clientes (nombre, cedula, telefono, estado ) VALUES (?, ?, ?, ?);""".trimMargin()

    fun listarClientes (): MutableList<Cliente>{

        val listaClientes = mutableListOf<Cliente>()
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM clientes")
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

    fun registrarCliente ():Int{
        println("nombre completo: ")
        val nombre = readln().toString()
        print("numero de cedula: ")
        val cedula = readln().toInt()
        print("numero de telefono: ")
        val telefono = readln().toInt()
        val estado = "activo" //Default
        val conn = DriverManager.getConnection(url)
        conn.use {
            val sql = "INSERT INTO clientes (nombre, cedula, telefono, estado ) VALUES (?, ?, ?, ?)"
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
}