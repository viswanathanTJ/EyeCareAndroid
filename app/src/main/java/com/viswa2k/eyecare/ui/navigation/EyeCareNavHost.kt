package com.viswa2k.eyecare.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.viswa2k.eyecare.ui.break_.BreakScreen
import com.viswa2k.eyecare.ui.home.HomeScreen
import com.viswa2k.eyecare.ui.onboarding.OnboardingScreen
import com.viswa2k.eyecare.ui.settings.SettingsScreen
import com.viswa2k.eyecare.ui.stats.StatsScreen

const val ONBOARDING_ROUTE = "onboarding"
const val BREAK_ROUTE = "break"

@Composable
fun EyeCareNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(ONBOARDING_ROUTE) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(ONBOARDING_ROUTE) { inclusive = true }
                    }
                }
            )
        }
        composable(BREAK_ROUTE) {
            BreakScreen(
                onDismiss = { navController.popBackStack() }
            )
        }
    }
}
