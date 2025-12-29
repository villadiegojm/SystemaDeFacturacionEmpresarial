package com.jmvn.proyectos

class ClienteManager (private val url : String, val user: String, val password: String) {

    fun registroCliente () {

        val databaseConection = DatabaseConection(url,user, password)
        var validacion = false
        while (validacion == false){

            print("\nnumero de cedula: ")
            val cedula = readln().toInt()
            val mensaje = "***ESTA CEDULA NO ESTA REGISTRADA PUEDE CONTINUAR!***"
            val validarCedula = databaseConection.consultarCedula(cedula,mensaje).first
            if (validarCedula == false) {
                println("nombre completo: ")
                val nombre = readln()

                print("numero de telefono: ")
                val telefono = readln()

                val estado = "activo" //Default
                databaseConection.registrarCliente(nombre, cedula, telefono, estado)
                validacion = true
            } else println("\n--YA EXISTE UN REGISTRO CON ESTE NUMERO DE CEDULA--")
        }
    }
}