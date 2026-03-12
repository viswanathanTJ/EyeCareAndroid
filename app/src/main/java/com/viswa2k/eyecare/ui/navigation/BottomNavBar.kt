package com.viswa2k.eyecare.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(Screen.Home, Screen.Stats, Screen.Settings)
    NavigationBar(modifier = modifier) {
        items.forEachIndexed { index, screen ->
            val route = screen.route
            val title = screen.title
            val icon = screen.icon
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = title) },
                label = { Text(title) },
                selected = currentRoute == route,
                onClick = { onNavigate(items[index]) }
            )
        }
    }
}
