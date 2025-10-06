package com.jmvn.proyectos

class ArticuloManager (private val url : String) {

    val databaseConection = DatabaseConection(url)

    fun creacionArticulo (){

        val mensaje = "\n**CODIGO NO EXISTE. PUEDE CONTINUAR CON LA CREACION DEL ARTICULO**"
        var validacion = false
        while (validacion == false){
            print("\nCODIGO DEL ARTICULO NUEVO: ")
            val codigo = readln().toInt()
            val validarArticulo = databaseConection.buscarIdArticulo(codigo, mensaje).first
            if (validarArticulo == false){
                print("nombre del articulo: ")
                val nombre = readln()

                print("descripcion: ")
                val descripcion = readln()

                print("precio unitario: ")
                val precio = readln().toDouble()

                print("cantidad de stock: ")
                val cantidadStock = readln().toInt()
                databaseConection.crearArticulo(codigo,nombre,precio,descripcion,cantidadStock)
                validacion = true
                println("\n***ARTICULO CREADO EXITOSAMENTE***")

            } else println("ESTE ARTICULO YA ESTA EN BASE DE DATOS!!!")
        }


    }
}