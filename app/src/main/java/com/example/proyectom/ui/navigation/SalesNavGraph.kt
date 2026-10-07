package com.example.proyectom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectom.ui.screens.CatalogScreen
import com.example.proyectom.ui.screens.CheckoutScreen
import com.example.proyectom.ui.screens.HistoryScreen
import com.example.proyectom.ui.screens.HomeScreen
import com.example.proyectom.viewmodel.SalesViewModel

@Composable
fun SalesNavGraph() {
    val navController = rememberNavController()
    val salesViewModel: SalesViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToCatalog = { navController.navigate("catalog") },
                onNavigateToCheckout = { navController.navigate("checkout") },
                onNavigateToHistory = { navController.navigate("history") },
                onSettingsClick = { navController.navigate("Settings") }
            )
        }
        composable("catalog") {
            CatalogScreen(
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate("checkout") }
            )
        }
        composable("checkout") {
            CheckoutScreen(
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onOrderSuccess = { navController.navigate("history") }
            )
        }
        composable("history") {
            HistoryScreen(
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable ("Settings") {
            SettingsScreeen(

            )
        }
    }
}

@Composable
fun SettingsScreeen() {
    TODO("Not yet implemented")
}
