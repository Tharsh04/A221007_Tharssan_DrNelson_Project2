package com.example.a221007_tharssan_drnelson_project2.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomNav(currentView: String, onNavigate: (String) -> Unit) {
    val navItems = listOf(
        NavItem("home", "Home", Icons.Default.Home),
        NavItem("locator", "Locator", Icons.Default.LocationOn), // Route hook link connected to Screen 6
        NavItem("history", "History", Icons.Default.History),
        NavItem("profile", "Profile", Icons.Default.Person)
    )

    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        navItems.forEach { item ->
            val isActive = currentView == item.id
            NavigationBarItem(
                selected = isActive,
                onClick = { onNavigate(item.id) },
                icon = { Icon(item.icon, item.label, tint = if (isActive) Color(0xFFEA580C) else Color.Gray) },
                label = { Text(item.label, fontSize = 12.sp, color = if (isActive) Color(0xFFEA580C) else Color.Gray) },
                colors = NavigationBarItemDefaults.colors(indicatorColor = Color.Transparent)
            )
        }
    }
}

data class NavItem(val id: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)