package com.example.proyectom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectom.ui.navigation.SalesNavGraph
import com.example.proyectom.ui.theme.ProyectoMTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectoMTheme {
                SalesNavGraph()
            }
        }
    }
}
