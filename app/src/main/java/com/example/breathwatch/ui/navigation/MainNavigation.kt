package com.example.breathwatch.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.breathwatch.ui.screens.weather.WeatherScreen
import com.example.breathwatch.ui.screens.airquality.AirQualityScreen
import com.example.breathwatch.ui.screens.fun.FunScreen
import com.example.breathwatch.ui.screens.extras.ExtrasScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Weather.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Weather.route) {
                WeatherScreen()
            }
            composable(BottomNavScreen.Air.route) {
                AirQualityScreen()
            }
            composable(BottomNavScreen.Fun.route) {
                FunScreen()
            }
            composable(BottomNavScreen.Extras.route) {
                ExtrasScreen()
            }
        }
    }
}

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Weather : BottomNavScreen("weather", "Weather", Icons.Default.Cloud)
    object Air : BottomNavScreen("air", "Air Quality", Icons.Default.Air)
    object Fun : BottomNavScreen("fun", "Fun", Icons.Default.EmojiEmotions)
    object Extras : BottomNavScreen("extras", "Extras", Icons.Default.Extension)
}

val bottomNavItems = listOf(
    BottomNavScreen.Weather,
    BottomNavScreen.Air,
    BottomNavScreen.Fun,
    BottomNavScreen.Extras
)
