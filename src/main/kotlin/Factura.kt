package com.jmvn.proyectos

import java.time.LocalDateTime

class Factura (var numero: Int, var cliente: Cliente?, var items: MutableList<Item?>, var total: Double, var fecha: LocalDateTime) {

}