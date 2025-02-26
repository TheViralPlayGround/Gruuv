package com.example.gruuv.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val label: String) {
    object Dashboard : NavigationItem("dashboard", Icons.Default.Home, "Dashboard")
    object Graph : NavigationItem("graph", Icons.Default.DateRange, "Graph")
}
