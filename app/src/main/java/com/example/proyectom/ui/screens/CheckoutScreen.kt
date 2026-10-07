package com.example.proyectom.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectom.model.CartItem
import com.example.proyectom.viewmodel.SalesViewModel
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: SalesViewModel,
    onNavigateBack: () -> Unit,
    onOrderSuccess: () -> Unit
) {
    val customerName by viewModel.customerName.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val paymentMethod by viewModel.paymentMethod.collectAsState()
    val expressDelivery by viewModel.expressDelivery.collectAsState()
    val giftWrap by viewModel.giftWrap.collectAsState()
    val taxInvoice by viewModel.taxInvoice.collectAsState()
    val deliveryTime by viewModel.deliveryTime.collectAsState()
    val lastCreatedOrder by viewModel.lastCreatedOrder.collectAsState()

    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    // TimePickerDialog helper
    val calendar = Calendar.getInstance()
    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            viewModel.setDeliveryTime(hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    )

    // Check if order was successfully submitted
    LaunchedEffect(lastCreatedOrder) {
        if (lastCreatedOrder != null) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Venta / Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Cajas de Texto (TextField)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "1. Datos del Cliente (Caja de Texto)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { viewModel.updateCustomerName(it) },
                            label = { Text("Nombre del Cliente") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                }
            }

            // 2. Listas (Cart Items)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "2. Productos en la Lista (${cartItems.sumOf { it.quantity }})",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (cartItems.isNotEmpty()) {
                                TextButton(onClick = { viewModel.clearCart() }) {
                                    Text("Vaciar", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (cartItems.isEmpty()) {
                            Text(
                                text = "El carrito está vacío. Agregue productos desde el Catálogo.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            cartItems.forEach { item ->
                                CartItemRow(
                                    item = item,
                                    onAdd = { viewModel.addToCart(item.product) },
                                    onRemove = { viewModel.removeFromCart(item.product) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }

            // 3. Radios (Payment Methods)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "3. Método de Pago (Radios)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val paymentOptions = listOf("Efectivo", "Tarjeta de Crédito", "Transferencia")
                        paymentOptions.forEach { option ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = paymentMethod == option,
                                    onClick = { viewModel.setPaymentMethod(option) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = option, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // 4. Checks (Checkboxes) & TimePicker
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "4. Opciones Adicionales (Checks)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Check 1
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = expressDelivery,
                                onCheckedChange = { viewModel.toggleExpressDelivery(it) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Envío Exprés (+$10.00)", modifier = Modifier.weight(1f))
                        }

                        // Check 2
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = giftWrap,
                                onCheckedChange = { viewModel.toggleGiftWrap(it) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Empaquetado para Regalo (+$3.00)", modifier = Modifier.weight(1f))
                        }

                        // Check 3
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = taxInvoice,
                                onCheckedChange = { viewModel.toggleTaxInvoice(it) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Requiere Factura Fiscal", modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(16.dp))

                        // TimePicker
                        Text(
                            text = "5. Hora de Entrega (TimePicker funcional)",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Hora seleccionada: $deliveryTime", fontWeight = FontWeight.SemiBold)
                            }
                            Button(onClick = { timePickerDialog.show() }) {
                                Text("Cambiar Hora")
                            }
                        }
                    }
                }
            }

            // Summary & Submit Button
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("$${String.format(Locale.getDefault(), "%.2f", viewModel.calculateSubtotal())}", color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        if (expressDelivery || giftWrap) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Extras:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("$${String.format(Locale.getDefault(), "%.2f", viewModel.calculateExtraCost())}", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total a Pagar:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("$${String.format(Locale.getDefault(), "%.2f", viewModel.calculateTotal())}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val success = viewModel.submitOrder()
                                if (!success) {
                                    showErrorDialog = true
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Confirmar y Realizar Venta", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog && lastCreatedOrder != null) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.clearLastCreatedOrder()
            },
            title = { Text("¡Venta Exitosa!") },
            text = {
                Column {
                    Text("Pedido ID: ${lastCreatedOrder?.id}")
                    Text("Cliente: ${lastCreatedOrder?.customerName}")
                    Text("Total: $${String.format(Locale.getDefault(), "%.2f", lastCreatedOrder?.totalAmount ?: 0.0)}")
                    Text("Hora de Entrega: ${lastCreatedOrder?.deliveryTime}")
                    Text("Método de Pago: ${lastCreatedOrder?.paymentMethod}")
                }
            },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    viewModel.clearLastCreatedOrder()
                    onOrderSuccess()
                }) {
                    Text("Ver Historial")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel.clearLastCreatedOrder()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Datos Incompletos") },
            text = { Text("Por favor, ingrese el nombre del cliente y agregue al menos un producto al carrito.") },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.product.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                text = "$${String.format(Locale.getDefault(), "%.2f", item.product.price)} x ${item.quantity} = $${String.format(Locale.getDefault(), "%.2f", item.subtotal)}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
            }
            Text(text = "${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            IconButton(onClick = onAdd) {
                Text("+", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
