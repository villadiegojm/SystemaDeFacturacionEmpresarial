package com.jmvn.proyectos

class FacturaDatabaseManager (private val url : String) {

    val databaseConection = DatabaseConection(url)
    var timbrar = ""

    fun crearFactura() {

        databaseConection.validarCliente()
        println("DESEA TIMBRAR ARTICULOS? s/n: ")
        timbrar = readln().lowercase()
        if (timbrar == "s"){
            println("EMPEZANDO FACTURACION!")
            databaseConection.timbrarArticulos()
            var guardar = ""
            print("TOTALIZAR Y GUARDAR: s/n ")
            guardar = readln().lowercase()
            if (guardar == "s"){
                databaseConection.guardarFactura()
                println("***FACTURA GUARDADA EXITOSAMENTE***")
            } else
                println("FACTURA GUARDADA PARCIALMENTE")
                databaseConection.cancelarFactura()
        }
//

    }

    fun listadoFacturas () : List<Factura> {
        val listaFacturas = databaseConection.listarFacturas()
        return listaFacturas
    }

    fun imprimeFactura (){

        databaseConection.imprimirFactura()
    }
}