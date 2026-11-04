package com.example.proyectom.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectom.model.CartItem
import com.example.proyectom.model.Order
import com.example.proyectom.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SalesViewModel : ViewModel() {

    // Catalog Products
    private val _products = MutableStateFlow(
        listOf(
            Product("1", "Playera Polo talla M", 150.00, "Ropa", "Vendo playera polo talla M color azul"),
            Product("2", "Laptop Hp", 1400.00, "Computación", "Vendo laptop por falta de uso"),
            Product("3", "Vestido casual", 300.00, "Ropa", "Vendo bonito vestido, muy comdo, no menos..."),
            Product("4", "Nintendo Switch", 2000.00, "Computación", "Vendo nintendo switch con magia, o cambio por algo de mi agrado"),
            Product("5", "Cafetera Express", 1750.00, "Hogar", "Vendo bonita cafetera express, Solo para conocedores, No menos")

        )
    )
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    // Cart Items
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // Form Fields State (Text box)
    private val _customerName = MutableStateFlow("")
    val customerName: StateFlow<String> = _customerName.asStateFlow()

    // Radio button state (Payment method)
    private val _paymentMethod = MutableStateFlow("Efectivo")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    // Checkboxes states
    private val _expressDelivery = MutableStateFlow(false)
    val expressDelivery: StateFlow<Boolean> = _expressDelivery.asStateFlow()

    private val _giftWrap = MutableStateFlow(false)
    val giftWrap: StateFlow<Boolean> = _giftWrap.asStateFlow()

    private val _taxInvoice = MutableStateFlow(false)
    val taxInvoice: StateFlow<Boolean> = _taxInvoice.asStateFlow()

    // TimePicker state
    private val _deliveryTime = MutableStateFlow("12:00 PM")
    val deliveryTime: StateFlow<String> = _deliveryTime.asStateFlow()

    // Order History List
    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory: StateFlow<List<Order>> = _orderHistory.asStateFlow()

    // UI Feedback state
    private val _lastCreatedOrder = MutableStateFlow<Order?>(null)
    val lastCreatedOrder: StateFlow<Order?> = _lastCreatedOrder.asStateFlow()

    // Dark Mode state
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    // Additional Settings states
    private val _selectedLanguage = MutableStateFlow("Español")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _countryCity = MutableStateFlow("México / CDMX")
    val countryCity: StateFlow<String> = _countryCity.asStateFlow()

    fun setLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun setCountryCity(location: String) {
        _countryCity.value = location
    }

    fun addProductToCatalog(name: String, price: Double, category: String, description: String): Boolean {
        if (name.isBlank() || price <= 0.0) return false
        val newProduct = Product(
            id = "PROD-${System.currentTimeMillis().toString().takeLast(5)}",
            name = name.trim(),
            price = price,
            category = if (category.isBlank()) "General" else category.trim(),
            description = description.trim()
        )
        _products.update { listOf(newProduct) + it }
        return true
    }

    fun updateProduct(product: Product) {
        _products.update { currentList ->
            currentList.map { if (it.id == product.id) product else it }
        }
    }

    fun deleteProduct(productId: String) {
        _products.update { currentList ->
            currentList.filter { it.id != productId }
        }
        _cartItems.update { currentCart ->
            currentCart.filter { it.product.id != productId }
        }
    }

    // Methods for Cart
    fun addToCart(product: Product) {
        _cartItems.update { currentList ->
            val existingIndex = currentList.indexOfFirst { it.product.id == product.id }
            if (existingIndex >= 0) {
                currentList.mapIndexed { index, item ->
                    if (index == existingIndex) item.copy(quantity = item.quantity + 1) else item
                }
            } else {
                currentList + CartItem(product = product, quantity = 1)
            }
        }
    }

    fun removeFromCart(product: Product) {
        _cartItems.update { currentList ->
            val existingIndex = currentList.indexOfFirst { it.product.id == product.id }
            if (existingIndex >= 0) {
                val item = currentList[existingIndex]
                if (item.quantity > 1) {
                    currentList.mapIndexed { index, cartItem ->
                        if (index == existingIndex) cartItem.copy(quantity = cartItem.quantity - 1) else cartItem
                    }
                } else {
                    currentList.filter { it.product.id != product.id }
                }
            } else {
                currentList
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Form inputs updates
    fun updateCustomerName(name: String) {
        _customerName.value = name
    }

    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

    fun toggleExpressDelivery(enabled: Boolean) {
        _expressDelivery.value = enabled
    }

    fun toggleGiftWrap(enabled: Boolean) {
        _giftWrap.value = enabled
    }

    fun toggleTaxInvoice(enabled: Boolean) {
        _taxInvoice.value = enabled
    }

    fun setDeliveryTime(hour: Int, minute: Int) {
        val amPm = if (hour >= 12) "PM" else "AM"
        val formattedHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val formattedMinute = String.format(Locale.getDefault(), "%02d", minute)
        _deliveryTime.value = "$formattedHour:$formattedMinute $amPm"
    }

    // Calculate totals
    fun calculateSubtotal(): Double {
        return _cartItems.value.sumOf { it.product.price * it.quantity }
    }

    fun calculateExtraCost(): Double {
        var extra = 0.0
        if (_expressDelivery.value) extra += 10.0
        if (_giftWrap.value) extra += 3.0
        return extra
    }

    fun calculateTotal(): Double {
        return calculateSubtotal() + calculateExtraCost()
    }

    // Submit Order
    fun submitOrder(): Boolean {
        if (_customerName.value.isBlank() || _cartItems.value.isEmpty()) {
            return false
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val newOrder = Order(
            id = "ORD-${System.currentTimeMillis().toString().takeLast(5)}",
            customerName = _customerName.value.trim(),
            items = _cartItems.value,
            paymentMethod = _paymentMethod.value,
            expressDelivery = _expressDelivery.value,
            giftWrap = _giftWrap.value,
            taxInvoice = _taxInvoice.value,
            deliveryTime = _deliveryTime.value,
            totalAmount = calculateTotal(),
            date = currentDate
        )

        _orderHistory.update { listOf(newOrder) + it }
        _lastCreatedOrder.value = newOrder

        // Reset cart and form partially
        _cartItems.value = emptyList()
        _customerName.value = ""
        _expressDelivery.value = false
        _giftWrap.value = false
        _taxInvoice.value = false

        return true
    }

    fun clearLastCreatedOrder() {
        _lastCreatedOrder.value = null
    }
}
