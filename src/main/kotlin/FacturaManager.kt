package com.jmvn.proyectos

class FacturaManager (private val url : String) {

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
            val mensaje = "***LA CEDULA NO EXISTE***"
            var validarCliente = databaseConection.consultarCedula(cedula,mensaje).first

            if (validarCliente == false){
                print("desea registrar cliente? s/n: ")
                val registar = readln().lowercase()

                if (registar == "s"){

                    val mensaje = ""
                    ClienteManager(url).registroCliente()
                    validarCliente = databaseConection.consultarCedula(cedula, mensaje).first

                }
            }else {
                val datos = databaseConection.consultarDatosCliente(cedula)

                println("\n====DATOS DEL CLIENTE====")
                println("NOMBRE:     ${datos.nombre}")
                println("CEDULA:     ${datos.cedula}")
                println("TELEFONO:   ${datos.telefono}")

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

                    if (precioEncontrado != 0.0){
                        val datosArticulo = databaseConection.consultarDatosArticulo(codigoArticulo)
                        datosArticulo.forEach { articulo ->
                            println("\nARTICULO:      ${articulo.nombre}")
                            println("DESCRIPCION:   ${articulo.descripcion}")
                            println("PRECIO:        ${articulo.precio}\n")
                        }
                        val mensaje = ""
                        print("cantidad de articulos: ")
                        var cantidad: Int = readln().toInt()

                        var precio: Double = databaseConection.buscarPrecioArticulo(codigoArticulo)
                        var idArticulo = databaseConection. buscarIdArticulo(codigoArticulo, mensaje).second

                        val resultado = timbrarArticulos(cantidad, precio, idArticulo, totalFactura, items)
                        totalFactura = resultado.first
                        items = resultado.second

                        print("desea llevar otro articulo s/n: ")
                        timbrar = readln().lowercase()
                    }

                } while (precioEncontrado == 0.0)
            }

            var guardar = ""
            print("TOTALIZAR Y GUARDAR: s/n ")
            guardar = readln().lowercase()
            if (guardar == "s"){
                val mensaje = ""
                clienteId = databaseConection.consultarCedula(cedula,mensaje).second
                databaseConection.guardarFactura(clienteId,totalFactura,items)
                println("***FACTURA GUARDADA EXITOSAMENTE***")
            } else
                println("***FACTURA GUARDADA PARCIALMENTE***")
                cancelarFactura(items,totalFactura)
        }
    }

    fun timbrarArticulos ( cantidad: Int, precio :Double, idArticulo : Int, totalFactura: Double, items: MutableList<Item>) : Pair<Double, MutableList<Item>>{
        var total = totalFactura
        var subtotal = precio * cantidad
        total += subtotal
        var item = Item(idArticulo,cantidad,subtotal)
        items.add(item)
        return Pair(total, items)
    }

    fun listadoFacturas () : List<ListadoFacturas> {
        val listaFacturas = databaseConection.listarFacturas()
        return listaFacturas
    }

    fun imprimirFactura (numero : Int) {

        val factura = "FACTURA NUMERO:"
        val cliente = "CLIENTE:"
        val fecha = "FECHA:"

        println("\n=============================================")
        val resultado = databaseConection.datosFactura(numero)
        val datosFactura = resultado?.first
        val datosCliente = resultado?.second
        println("${factura}${(datosFactura?.numero?:0).toString().padStart(28)}")
        println("$cliente${datosCliente?.nombre?.padStart(35)}")
        println("$fecha${(datosFactura?.fecha?:0).toString().padStart(37)}")
        println("\n-------------------RESUMEN-------------------")
        println("cod | articulo | prec unit | cant | subtotal")
        println("---------------------------------------------")
        val detalles = databaseConection.detallesFactura(numero)
        detalles.forEach { items ->
            print("${items.codigo.toString().padEnd(6)}")
            print("${items.nombre.padEnd(13)}")
            print("${items.precioUnitario.toString().padEnd(12)}")
            print("${items.cantidad.toString().padEnd(4)}")
            println("${items.subtotal.toString().padStart(10)}")
        }
        println("=============================================")
        println("TOTAL FACTURA:${(datosFactura?.total?:0).toString().padStart(29)}")
        println("_____________________________________________")
    }

    fun cancelarFactura (items: MutableList<Item>, totalFactura: Double){
        var items = items
        var totalFactura = totalFactura
        items = mutableListOf()
        totalFactura = 0.0
    }
}

//"${items.codigo.toString().padEnd(6)}${items.nombre.padEnd(13)}${items.precioUnitario.toString().padEnd(10)}${items.cantidad.toString().padEnd(4)}${items.subtotal.toString().padStart(10)}"



