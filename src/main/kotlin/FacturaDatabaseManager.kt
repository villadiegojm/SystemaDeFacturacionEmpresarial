package com.jmvn.proyectos

class FacturaDatabaseManager (private val url : String) {

    val databaseConection = DatabaseConection(url)

    fun crearFactura() {

        var totalFactura = 0.0
        var codigoArticulo = 0
        var clienteId = 0
        var cedula = 0
        var items = mutableListOf<Item>()
        var timbrar = ""

        do {
            print("digite su numero de cedula: ")
            cedula = readln().toInt()
            var validarCliente = databaseConection.consultarCedula(cedula)

            if (validarCliente == false){
                print("desea registrar cliente? s/n: ")
                val registar = readln().lowercase()

                if (registar == "s"){
                    ClienteDatabaseManager(url).registrarCliente()
                    validarCliente = databaseConection.consultarCedula(cedula)
                }
            }else {
               databaseConection.validarDatosCliente(cedula)
            }
        } while (validarCliente == false)

        print("\nDESEA TIMBRAR ARTICULOS? s/n: ")
        timbrar = readln().lowercase()
        if (timbrar == "s"){
            println("EMPEZANDO FACTURACION!")


            while (timbrar == "s"){

                do {
                print("\ntimbrar articulo: ")
                codigoArticulo = readln().toInt()
                var precioEncontrado = databaseConection.buscarPrecioArticulo(codigoArticulo)

                    databaseConection.validarDatosArticulo(codigoArticulo)
                    val resultado = databaseConection.timbrarArticulos(codigoArticulo, totalFactura, items)
                    totalFactura = resultado.first
                    items = resultado.second

                    print("desea llevar otro articulo s/n: ")
                    timbrar = readln().lowercase()

                } while (precioEncontrado == 0.0)


            }


            var guardar = ""
            print("TOTALIZAR Y GUARDAR: s/n ")
            guardar = readln().lowercase()
            if (guardar == "s"){
                clienteId = databaseConection.consultarIdCliente(cedula)
                databaseConection.guardarFactura(clienteId,totalFactura,items)
                println("***FACTURA GUARDADA EXITOSAMENTE***")
            } else
                println("FACTURA GUARDADA PARCIALMENTE")
                cancelarFactura(items,totalFactura)
        }


    }

    fun listadoFacturas () : List<Factura> {
        val listaFacturas = databaseConection.listarFacturas()
        return listaFacturas
    }

    fun imprimeFactura (){

        databaseConection.imprimirFactura()
    }

    fun cancelarFactura (items: MutableList<Item>, totalFactura: Double){
        var items = items
        var totalFactura = totalFactura
        items = mutableListOf()
        totalFactura = 0.0
    }
}