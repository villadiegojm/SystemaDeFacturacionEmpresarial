package com.jmvn.proyectos

class FacturaDatabaseManager (private val url : String) {

    val databaseConection = DatabaseConection(url)

    fun crearFactura() {

        databaseConection.validarCliente()
        databaseConection.timbrarArticulos()
        databaseConection.guardarFactura()

    }
}