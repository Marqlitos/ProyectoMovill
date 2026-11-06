package com.example.proyectom.data.repository

import com.example.proyectom.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseProductRepository : ProductRepository {

    private val database = FirebaseDatabase.getInstance().getReference("products")

    override suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = database.get().await()
            snapshot.children.mapNotNull { it.getValue(Product::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun observeProducts(): Flow<List<Product>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }

    override suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val id = if (product.id.isBlank()) {
                database.push().key ?: "PROD-${System.currentTimeMillis()}"
            } else {
                product.id
            }
            val productToSave = product.copy(id = id)
            database.child(id).setValue(productToSave).await()
            Result.success(productToSave)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Product> {
        return try {
            database.child(product.id).setValue(product).await()
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            database.child(productId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
