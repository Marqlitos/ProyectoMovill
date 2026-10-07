package com.example.proyectom.model

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val description: String = ""
)

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Double get() = product.price * quantity
}

data class Order(
    val id: String,
    val customerName: String,
    val items: List<CartItem>,
    val paymentMethod: String,
    val expressDelivery: Boolean,
    val giftWrap: Boolean,
    val taxInvoice: Boolean,
    val deliveryTime: String,
    val totalAmount: Double,
    val date: String
)
