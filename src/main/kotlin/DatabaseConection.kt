package com.jmvn.proyectos

import com.jmvn.proyectos.tables.tables.references.ARTICULOS
import com.jmvn.proyectos.tables.tables.references.CLIENTES
import com.jmvn.proyectos.tables.tables.references.FACTURAS
import com.jmvn.proyectos.tables.tables.references.ITEMS
import org.jooq.impl.DSL
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DatabaseConection (private val url : String, val user: String, val password : String ) {

    fun consultarDatosCliente (cedula : Int) : Cliente {
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val record = it.select()
                .from(CLIENTES)
                .where(CLIENTES.CEDULA.eq(cedula))
                .fetchOne()
            val nombre = record?.get(ARTICULOS.NOMBRE,String::class.java)?:""
            val cedula = record?.get(CLIENTES.CEDULA,Int::class.java)?:0
            val telefono = record?.get(CLIENTES.TELEFONO,String::class.java)?:""
            val estado = record?.get(CLIENTES.ESTADO,String::class.java)?:""
            val cliente = Cliente(nombre, cedula, telefono, estado)
            return cliente
        }
    }

    fun guardarFactura (clienteId: Int, totalFactura: Double,items: MutableList<Item>) {
        var facturaId:Int
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val execute = it.insertInto(FACTURAS)
                .set(FACTURAS.CLIENTE_ID, clienteId)
                .set(FACTURAS.TOTAL, totalFactura)
                .returning(FACTURAS.FACTURA_NUMERO)
                .fetchOne()
            facturaId = execute?.facturaNumero ?: 0
        }
        guardarItems(items, facturaId)
    }

    fun guardarItems (items: List<Item>, facturaId: Int){
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            for (item in items){
                it.insertInto(ITEMS)
                    .set(ITEMS.FACTURA_ID,facturaId)
                    .set(ITEMS.ARTICULO_ID,item.id)
                    .set(ITEMS.CANTIDAD,item.cantidad)
                    .set(ITEMS.DESCUENTO,item.descuento)
                    .set(ITEMS.SUBTOTAL,item.subtotal)
                    .set(ITEMS.PRECIO,item.precio)
                    .execute()
            }
        }
    }

    fun consultarCedula (cedula: Int, mensaje: String): Pair<Boolean,Int>{
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val record = it.select().from(CLIENTES).where(CLIENTES.CEDULA.eq(cedula)).fetchOne()

            if (record == null){
                println("\n$mensaje")
                return Pair(false, 0)
            } else {
                val clienteId = record.get(CLIENTES.ID,Int::class.java)
                return Pair(true, clienteId)
            }
        }
    }

    fun consultarDatosArticulo (codigo: Int) : List<Articulo> {
        val datos = mutableListOf<Articulo>()
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val record = it.select()
                .from(ARTICULOS)
                .where(ARTICULOS.CODIGO.eq(codigo))
                .fetchOne()
            val nombre = record?.get(ARTICULOS.NOMBRE, String::class.java)?:""
            val precio = record?.get(ARTICULOS.PRECIO,Double::class.java)?:0.0
            val descripcion = record?.get(ARTICULOS.DESCRIPCION,String::class.java)?:""
            val cantidadStock = record?.get(ARTICULOS.CANTIDADSTOK,Int::class.java)?:0
            val descuento = record?.get(ARTICULOS.PORCENTAJE_DESCUENTO,Int::class.java)?:0
            val articulo = Articulo(codigo,nombre,precio,descripcion,cantidadStock,descuento)
            datos.add(articulo)

        }
        return datos
    }

    fun buscarPrecioArticulo (codigo : Int): Double  {
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val result = it.select()
                .from(ARTICULOS)
                .where(ARTICULOS.CODIGO.eq(codigo))
                .fetchOne()
            if (result == null){
                return 0.0
            }else{
                val precio = result.get(ARTICULOS.PRECIO,Double::class.java)
                return precio
            }
        }
    }

    fun buscarIdArticulo (codigo:Int, mensaje: String): Pair<Boolean, Int> {
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val result = it.select()
                .from(ARTICULOS)
                .where(ARTICULOS.CODIGO.eq(codigo))
                .fetchOne()
            if (result == null){
                println(mensaje)
                return Pair(false, 0)
            } else {
                val idArticulo = result.get(ARTICULOS.ID,Int::class.java)
                return Pair(true, idArticulo)
            }
        }
    }

    fun actualizarStock(id: Int, cantidad : Int) {
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val result = it.update(ARTICULOS)
                .set(ARTICULOS.CANTIDADSTOK, ARTICULOS.CANTIDADSTOK - cantidad)
                .where(ARTICULOS.ID.eq(id))
                .execute()
            println("FILAS AFECTADAS:     $result")
        }
    }

    fun listarFacturas (offset: Int): MutableList<ListadoFacturas>{
        val facturas = mutableListOf<ListadoFacturas>()
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val result = it.select(
                FACTURAS.FACTURA_NUMERO,
                FACTURAS.CLIENTE_ID,
                FACTURAS.TOTAL,
                FACTURAS.FECHA,
                CLIENTES.NOMBRE
            )
                .from(FACTURAS).leftJoin(CLIENTES).on(FACTURAS.CLIENTE_ID.eq(CLIENTES.ID))
                .where(CLIENTES.NOMBRE.isNotNull)
                .orderBy(FACTURAS.FECHA.desc())
                .limit(10)
                .offset(offset)
                .fetch()
            result.map { fac ->
                val numero = fac.get(FACTURAS.FACTURA_NUMERO)?:0
                val total = fac.get(FACTURAS.TOTAL)?.toDouble()?:0.0
                val fecha = fac.get(FACTURAS.FECHA)?: LocalDateTime.now()
                val nombre = fac.get(CLIENTES.NOMBRE)?:""

//                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//                val localDateTime = LocalDateTime.parse(fecha, formatter)
                //val dateTime = LocalDateTime.parse(fecha,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                //2025-08-28 21:21:23
                val date = Date.from(fecha.atZone(ZoneId.systemDefault()).toInstant())
                val factura = ListadoFacturas(numero,nombre,total,date)
                facturas.add(factura)
            }
        }
        return facturas
    }

    fun validarFactura (numeroFactura : Int) : Boolean {
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val records = it.select().from(FACTURAS).where(FACTURAS.FACTURA_NUMERO.eq(numeroFactura)).fetch()
            if (records.isEmpty()){
                println("***LA FACTURA NO EXISTE***\n")
                return false
            }
        }
        return true
    }

    fun datosFactura (numeroFactura :Int) :Pair<Factura,Cliente>? {
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val result = it.select(
                FACTURAS.FACTURA_NUMERO,
                FACTURAS.CLIENTE_ID,
                FACTURAS.TOTAL,
                FACTURAS.FECHA,
                CLIENTES.NOMBRE,
                CLIENTES.CEDULA,
                CLIENTES.TELEFONO,
                CLIENTES.ESTADO
            ).from(FACTURAS)
                .join(CLIENTES).on(FACTURAS.CLIENTE_ID.eq(CLIENTES.ID))
                .where(FACTURAS.FACTURA_NUMERO.eq(numeroFactura)).fetchOne()
            if (result == null) return null

            val numeroFactura = result.get(FACTURAS.FACTURA_NUMERO)?:0
            val cliente_id = result.get(FACTURAS.CLIENTE_ID)?:0
            val fecha = result.get(FACTURAS.FECHA)?:LocalDateTime.now()
            val total = result.get(FACTURAS.TOTAL)?.toDouble()?:0.0
            val nombre = result.get(CLIENTES.NOMBRE)?:""
            val cedula = result.get(CLIENTES.CEDULA)?:0
            val telefono = result.get(CLIENTES.TELEFONO)?:""
            val estado = result.get(CLIENTES.ESTADO)?:""

//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
//            val localDateTime = LocalDateTime.parse(fecha, formatter)
            val date = Date.from(fecha.atZone(ZoneId.systemDefault()).toInstant())

            val factura = Factura(numeroFactura, cliente_id, total, date)
            val cliente = Cliente(nombre, cedula, telefono, estado)
            return Pair(factura,cliente)
        }
    }

    fun detallesFactura (numeroFactura: Int) : List<DetallesFactura>{
        val detalles = mutableListOf<DetallesFactura>()
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val result = it.select(
                ARTICULOS.CODIGO,
                ARTICULOS.NOMBRE,
                ITEMS.PRECIO,
                ITEMS.CANTIDAD,
                ITEMS.DESCUENTO,
                ITEMS.SUBTOTAL
            ).from(ITEMS)
                .join(FACTURAS).on(ITEMS.FACTURA_ID.eq(FACTURAS.FACTURA_NUMERO))
                .join(ARTICULOS).on(ITEMS.ARTICULO_ID.eq(ARTICULOS.ID))
                .where(FACTURAS.FACTURA_NUMERO.eq(numeroFactura))
                .fetch()
            result.map { item ->
                val codigo = item.get(ARTICULOS.CODIGO)?:0
                val nombre = item.get(ARTICULOS.NOMBRE)?:""
                val precio = item.get(ITEMS.PRECIO)?.toDouble()?:0.0
                val cantidad = item.get(ITEMS.CANTIDAD)?:0
                val descuento = item.get(ITEMS.DESCUENTO)?.toDouble()?:0.0
                val subtotal = item.get(ITEMS.SUBTOTAL)?.toDouble()?:0.0
                val detalle = DetallesFactura(codigo, nombre, precio, cantidad, descuento, subtotal)
                detalles.add(detalle)
            }
        }
        return detalles
    }

    fun listarArticulos (): MutableList<Articulo>{
        val articulos = mutableListOf<Articulo>()
        val dsl = DSL.using(url,user,password)
        dsl.use {
            val result = it.select().from(ARTICULOS).fetch()
            val listaArticulos = result.map {
                Articulo(
                    it.get(ARTICULOS.CODIGO,Int::class.java),
                    it.get(ARTICULOS.NOMBRE,String::class.java),
                    it.get(ARTICULOS.PRECIO,Double::class.java),
                    it.get(ARTICULOS.DESCRIPCION,String::class.java),
                    it.get(ARTICULOS.CANTIDADSTOK,Int::class.java),
                    it.get(ARTICULOS.PORCENTAJE_DESCUENTO,Int::class.java)
                )
            }
            articulos.addAll(listaArticulos)
        }
        return articulos
    }

    fun listarClientes (): MutableList<Cliente>{

        val listaClientes = mutableListOf<Cliente>()
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            val result = it.select().from(CLIENTES).orderBy(CLIENTES.NOMBRE)
            result.map { client->
                val nombre = client.get(CLIENTES.NOMBRE)?:""
                val cedula = client.get(CLIENTES.CEDULA)?:0
                val telefono = client.get(CLIENTES.TELEFONO)?:""
                val estado = client.get(CLIENTES.ESTADO)?:""
                listaClientes.add(Cliente(nombre,cedula,telefono,estado))
            }
        }
        return listaClientes
    }

    fun registrarCliente (nombre: String, cedula: Int, telefono: String, estado: String):Int{
        val dslContext = DSL.using(url,user,password)
        dslContext.use {
            it.insertInto(CLIENTES)
                .columns(CLIENTES.NOMBRE, CLIENTES.CEDULA, CLIENTES.TELEFONO, CLIENTES.ESTADO)
                .values(nombre,cedula,telefono,estado).execute()
            println("\n***REGISTRO EXITOSO***")
        }
        return cedula
    }

    fun crearArticulo (codigo: Int, nombre: String, precio: Double, descripcion: String, cantidadDeStock: Int){
        val dsl = DSL.using(url,user,password)
        dsl.use {
            val execute = it.insertInto(ARTICULOS)
                .columns(
                    ARTICULOS.CODIGO,
                    ARTICULOS.NOMBRE,
                    ARTICULOS.PRECIO,
                    ARTICULOS.DESCRIPCION,
                    ARTICULOS.CANTIDADSTOK
                ).values(codigo, nombre, precio, descripcion, cantidadDeStock).execute()
            println("\nREGISTROS NUEVOS: $execute")
        }
    }
}