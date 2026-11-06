package com.example.proyectom.data.repository.placeholder

import com.example.proyectom.data.repository.ProductRepository
import com.example.proyectom.model.Product
import kotlinx.coroutines.delay

class PlaceholderProductRepository : ProductRepository {

    private val productMemoryStore = mutableListOf<Product>()

    override suspend fun getProducts(): List<Product> {
        delay(120)
        // TODO BD/Backend: Consultar catálogo real desde API/DB y mapear a Product.
        return productMemoryStore.toList()
    }

    override suspend fun createProduct(product: Product): Result<Product> {
        delay(120)
        productMemoryStore.add(product)
        // TODO BD/Backend: Reemplazar por POST/INSERT real y usar ID devuelto por backend/DB.
        return Result.success(product)
    }

    override suspend fun updateProduct(product: Product): Result<Product> {
        delay(120)
        val index = productMemoryStore.indexOfFirst { it.id == product.id }
        if (index < 0) return Result.failure(IllegalArgumentException("Producto no encontrado."))
        productMemoryStore[index] = product
        // TODO BD/Backend: Reemplazar por UPDATE real en la fuente de datos final.
        return Result.success(product)
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        delay(120)
        productMemoryStore.removeAll { it.id == productId }
        // TODO BD/Backend: Reemplazar por DELETE real en API/DB.
        return Result.success(Unit)
    }
}
