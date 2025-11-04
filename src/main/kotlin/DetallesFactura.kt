package com.jmvn.proyectos

data class DetallesFactura (
        val codigo: Int,
        val nombre: String,
        val precioUnitario: Double,
        val cantidad: Int,
        val descuento: Double,
        val subtotal: Double
)