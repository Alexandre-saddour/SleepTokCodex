package com.example.kmpbackbone.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.kmpbackbone.ui.home.HomeScreen

@Composable
fun AppRoot() {
    MaterialTheme {
        HomeScreen()
    }
}
