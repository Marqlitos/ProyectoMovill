package com.example.proyectom.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectom.model.Order
import com.example.proyectom.viewmodel.SalesViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: SalesViewModel,
    onNavigateBack: () -> Unit
) {
    val orderHistory by viewModel.orderHistory.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Ventas / Compras") },
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
        if (orderHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay ventas registradas aún.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Total de ventas: ${orderHistory.size}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                items(orderHistory) { order ->
                    OrderItemCard(order = order)
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = order.id,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp
                )
                Text(
                    text = order.date,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Cliente: ${order.customerName}", fontWeight = FontWeight.SemiBold)
            Text(text = "Pago: ${order.paymentMethod} | Entrega: ${order.deliveryTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (order.expressDelivery || order.giftWrap || order.taxInvoice) {
                Spacer(modifier = Modifier.height(4.dp))
                val extras = mutableListOf<String>()
                if (order.expressDelivery) extras.add("Exprés")
                if (order.giftWrap) extras.add("Regalo")
                if (order.taxInvoice) extras.add("Factura")
                Text(text = "Extras: ${extras.joinToString(", ")}", fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Productos (${order.items.sumOf { it.quantity }}):", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            order.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "• ${item.product.name} (x${item.quantity})", fontSize = 12.sp)
                    Text(text = "$${String.format(Locale.getDefault(), "%.2f", item.subtotal)}", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Pagado:", fontWeight = FontWeight.Bold)
                Text(
                    text = "$${String.format(Locale.getDefault(), "%.2f", order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
