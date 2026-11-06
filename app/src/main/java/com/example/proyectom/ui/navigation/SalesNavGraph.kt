package com.example.proyectom.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectom.ui.screens.AddProductScreen
import com.example.proyectom.ui.screens.CatalogScreen
import com.example.proyectom.ui.screens.CheckoutScreen
import com.example.proyectom.ui.screens.EditProductScreen
import com.example.proyectom.ui.screens.HistoryScreen
import com.example.proyectom.ui.screens.HomeScreen
import com.example.proyectom.ui.screens.LoginScreen
import com.example.proyectom.ui.screens.ProfileScreen
import com.example.proyectom.ui.screens.RegisterScreen
import com.example.proyectom.ui.screens.SettingsScreen
import com.example.proyectom.viewmodel.AuthViewModel
import com.example.proyectom.viewmodel.SalesViewModel

@Composable
fun SalesNavGraph(
    salesViewModel: SalesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToCatalog = { navController.navigate("catalog") },
                onNavigateToCheckout = { navController.navigate("checkout") },
                onNavigateToHistory = { navController.navigate("history") },
                onNavigateToSettings = { navController.navigate("settings") },
                onNavigateToProfile = {
                    if (authViewModel.uiState.value.isAuthenticated) {
                        navController.navigate("profile")
                    } else {
                        navController.navigate("login")
                    }
                }
            )
        }
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRegister = { navController.navigate("register") },
                onAuthSuccess = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate("profile") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
        composable("profile") {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLogin = { navController.navigate("login") },
                onLogoutSuccess = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            )
        }
        composable("catalog") {
            CatalogScreen(
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate("checkout") },
                onNavigateToAddProduct = { navController.navigate("addProduct") },
                onNavigateToEditProduct = { productId -> navController.navigate("editProduct/$productId") }
            )
        }
        composable("addProduct") {
            AddProductScreen(
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("editProduct/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            EditProductScreen(
                productId = productId,
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() }
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
        composable("settings") {
            SettingsScreen(
                viewModel = salesViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
