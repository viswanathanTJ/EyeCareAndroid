package com.viswa2k.eyecare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.ui.navigation.BottomNavBar
import com.viswa2k.eyecare.ui.navigation.EyeCareNavHost
import com.viswa2k.eyecare.ui.navigation.ONBOARDING_ROUTE
import com.viswa2k.eyecare.ui.navigation.Screen
import com.viswa2k.eyecare.ui.theme.EyeCareTheme
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val preferencesManager: PreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EyeCareTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                var isReady by remember { mutableStateOf(false) }

                // Check onboarding on first launch
                LaunchedEffect(Unit) {
                    val onboardingDone = preferencesManager.onboardingCompleted.first()
                    if (!onboardingDone) {
                        navController.navigate(ONBOARDING_ROUTE) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                    isReady = true
                }

                val showBottomBar = isReady && currentRoute != ONBOARDING_ROUTE

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(
                                currentRoute = currentRoute,
                                onNavigate = { screen ->
                                    navController.navigate(screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    EyeCareNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
