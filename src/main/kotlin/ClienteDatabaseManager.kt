package com.jmvn.proyectos

import java.sql.DriverManager

class ClienteDatabaseManager (private val url : String) {

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
}