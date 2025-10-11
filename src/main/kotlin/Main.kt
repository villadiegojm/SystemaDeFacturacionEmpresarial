package com.jmvn.proyectos


fun main() {

    val url = "jdbc:sqlite:SystemaFacturacion.db"
    val facturasManager = FacturaManager(url)
    val articulosManager = ArticuloManager(url)
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
                    println("================================================")
                    println("                   FACTURAS                   ")
                    println("________________________________________________")
                    println("NUM________CLIENTE__________TOTAL________FECHA")
                    facturas.forEach(){factura ->
                        println("${factura.factura_numero.toString().padEnd(6)}${factura.cliente.padEnd(22)}${factura.total.toString().padEnd(10)}${factura.fecha.toString().padStart(12)}")
                    }
                }
            }
            3 -> {
                val clientes = DatabaseConection(url).listarClientes()
                if (clientes.isEmpty()){
                    println("no hay clientes en la base")
                }else {
                    println("_______________________________________________")
                    println("IT       NOMBRE         CEDULA        TELEFONO")
                    println("-----------------------------------------------")
                    var contador = 0
                    clientes.forEach(){cliente ->
                        contador ++
                        println("${contador.toString().padEnd(4)} ${cliente.nombre.padEnd(20)} ${cliente.cedula.toString().padEnd(8)} ${cliente.telefono.toString().padStart(10)}")
                    }
                    println("_____________________________________________")
                    println("\nTOTAL CLIENTES:____________________________$contador")
                }
            }
            4 -> {
                val articulos = DatabaseConection(url).listarArticulos()
                if (articulos.isEmpty()){
                    println("no hay articulos")
                }else {
                    println("_______________________________________")
                    println("CODIGO         ARTICULO              PRECIO")
                    println("---------------------------------------")
                    articulos.forEach(){ articulo ->
                        print("${articulo.codigo.toString().padEnd(10)}")
                        print("${articulo.nombre.padEnd(22)}")
                        println("${articulo.precio.toString().padStart(8)}")
                    }
                }
            }
            5 -> {
                var numero = 0
                var encontrarFactura = false
                do {
                    print("NUMERO DE FACTURA QUE DESEA IMPRIMIR: ")
                    numero = readln().toInt()
                    encontrarFactura = DatabaseConection(url).validarFactura(numero)
                }while (encontrarFactura == false)
                facturasManager.imprimirFactura(numero)
            }
            6 -> {
                    ClienteManager(url).registroCliente()
            }
            7 -> {
                articulosManager.creacionArticulo()
            }
            8 -> println("\nGRACIAS")
        }
    } while(seleccion != 8)

}