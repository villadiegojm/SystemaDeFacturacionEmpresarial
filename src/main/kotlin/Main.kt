package com.jmvn.proyectos

import java.time.ZoneId
import java.time.format.DateTimeFormatter


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
                var contador = 0
                var facturas = facturasManager.listadoFacturas(contador)
                if (facturas.isEmpty()){
                    println("***NO HAY FACTUTAS GENERADAS***")
                } else {
                    var bandera = true
                    while (bandera == true){

                        println("================================================")
                        println("                   FACTURAS                   ")
                        println("________________________________________________")
                        println("NUM________CLIENTE__________TOTAL________FECHA")

                        facturas.forEach(){factura ->
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                            val format = formatter.format(factura.fecha.toInstant().atZone(ZoneId.of("America/Bogota")))
                            print("${factura.factura_numero.toString().padEnd(6)}")
                            print("${factura.cliente.padEnd(22)}")
                            print("${factura.total.toString().padEnd(10)}")
                            println("${format.padStart(12)}")
                            contador ++
                        }
                        print("\nDESEA MOSTRAR LA SIGUIENTE PAGINA s/n: ")
                        var siguientePagina = readln().toString().lowercase()
                        if (siguientePagina != "s") {
                            bandera = false
                        } else  {
                            facturas = facturasManager.listadoFacturas(contador)
                            if (facturas.isEmpty()){
                                println("\n***NO HAY MAS REGISTROS***")
                                bandera = false
                            }
                        }

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
                    println("___________________________________________________")
                    println("CODIGO       ARTICULO           PRECIO         STOCK")
                    println("---------------------------------------------------")
                    articulos.forEach(){ articulo ->
                        print("${articulo.codigo.toString().padEnd(10)}")
                        print("${articulo.nombre.padEnd(22)}")
                        print("$${articulo.precio.toString().padStart(8)}")
                        println("${articulo.cantidadDeStock.toString().padStart(10)}")
                    }
                }
            }
            5 -> {
                val articulos = DatabaseConection(url).listarArticulos()
                if (articulos.isEmpty()){
                    println("NO HAY INVENTARIO")
                }else {
                    println("______________________________________________")
                    println("CODIGO       ARTICULO           DISPONIBLE")
                    println("----------------------------------------------")
                    articulos.forEach() { articulo ->
                        print("${articulo.codigo.toString().padEnd(10)}")
                        print("${articulo.nombre.padEnd(22)}")
                        println("${articulo.cantidadDeStock.toString().padStart(8)}")
                    }
                }
            }
            6 -> {
                var numero = 0
                var encontrarFactura = false
                do {
                    print("NUMERO DE FACTURA QUE DESEA IMPRIMIR: ")
                    numero = readln().toInt()
                    encontrarFactura = DatabaseConection(url).validarFactura(numero)
                }while (encontrarFactura == false)
                facturasManager.imprimirFactura(numero)
            }
            7 -> {
                    ClienteManager(url).registroCliente()
            }
            8 -> {
                articulosManager.creacionArticulo()
            }
            9 -> println("\nGRACIAS")
        }
    } while(seleccion != 9)

}