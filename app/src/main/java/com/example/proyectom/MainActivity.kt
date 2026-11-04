package com.example.proyectom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectom.ui.navigation.SalesNavGraph
import com.example.proyectom.ui.theme.ProyectoMTheme
import com.example.proyectom.viewmodel.SalesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val salesViewModel: SalesViewModel = viewModel()
            val isDarkMode by salesViewModel.isDarkMode.collectAsState()

            ProyectoMTheme(darkTheme = isDarkMode) {
                SalesNavGraph(salesViewModel = salesViewModel)
            }
        }
    }
}
