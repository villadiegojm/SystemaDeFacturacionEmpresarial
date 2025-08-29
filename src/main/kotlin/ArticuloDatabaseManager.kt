package com.jmvn.proyectos

import java.sql.DriverManager

class ArticuloDatabaseManager (private val url : String) {

    fun listar (): MutableList<Articulo>{
        val articulos = mutableListOf<Articulo>()
        val connection = DriverManager.getConnection(url)
        connection.use {
            val statement = it.createStatement()
            statement.use {
                val resultSet = it.executeQuery("SELECT * FROM articulos")
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
}