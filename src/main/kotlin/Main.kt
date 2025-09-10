package com.jmvn.proyectos


fun main() {

    val url = "jdbc:sqlite:SystemaFacturacion.db"
    val clientesManager = ClienteDatabaseManager(url)
    val articulosManager = ArticuloDatabaseManager(url)
    val facturasManager = FacturaDatabaseManager(url)
    val menu = Menu()
    do {
        menu.mostrar()
        var seleccion: Int = 0
        print("selecciona una opcion: ")
        seleccion = readln().toInt()
        when(seleccion){
            1 -> facturasManager.crearFactura()
            2 -> {
                val facturas = facturasManager.listadoFacturas()
                if (facturas.isEmpty()){
                    println("***NO HAY FACTUTAS GENERADAS***")
                } else {
                    println("-------------------------------------")
                    println("FACT NUMERO----------TOTAL----------FECHA")
                    facturas.forEach(){factura ->
                        println("     ${factura.numero}---------${factura.total}----------${factura.fecha}")
                    }
                }
            }
            3 -> {
                val clientes = clientesManager.listarClientes()
                if (clientes.isEmpty()){
                    println("no hay clientes en la base")
                }else {
                    println("______________________________")
                    var contador = 0
                    clientes.forEach(){cliente ->
                        contador ++
                        println("$contador ${cliente.nombre} ${cliente.cedula}*****${cliente.telefono}")
                    }
                }
            }
            4 -> {
                val articulos = articulosManager.listar()
                if (articulos.isEmpty()){
                    println("no hay articulos")
                }else {
                    println("___________________________________")
                    var contador = 0
                    articulos.forEach(){ articulo ->
                        contador ++
                        println("$contador ${articulo.nombre} ${articulo.precio}")
                    }
                }
            }
            5 -> facturasManager.imprimeFactura()
            6 -> clientesManager.registrarCliente()
            7 -> println("\nGRACIAS")
        }
    } while(seleccion != 7)

}