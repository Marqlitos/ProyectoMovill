package com.example.proyectom.data.repository

import com.example.proyectom.model.Product

interface ProductRepository {
    suspend fun getProducts(): List<Product>
    suspend fun createProduct(product: Product): Result<Product>
    suspend fun updateProduct(product: Product): Result<Product>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
