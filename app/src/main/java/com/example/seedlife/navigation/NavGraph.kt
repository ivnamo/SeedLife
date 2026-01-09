package com.example.seedlife.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.seedlife.ui.auth.AuthScreen
import com.example.seedlife.ui.auth.AuthViewModel
import com.example.seedlife.ui.home.HomeScreen
import com.example.seedlife.ui.profile.ProfileScreen
import com.example.seedlife.ui.seeddetail.SeedDetailScreen
import com.example.seedlife.ui.session.SessionViewModel
import com.example.seedlife.ui.stats.StatsScreen

/**
 * Rutas de navegación para Auth
 */
sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("auth/login")
}

/**
 * Rutas de navegación para App (con Bottom Navigation)
 */
sealed class AppScreen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Garden : AppScreen("app/garden", "Jardín", Icons.Default.Home)
    object Stats : AppScreen("app/stats", "Estadísticas", Icons.Default.BarChart)
    object Profile : AppScreen("app/profile", "Perfil", Icons.Default.Person)
    
    data class SeedDetail(val seedId: String = "{seedId}") : AppScreen("app/seed_detail/{seedId}", "Detalle", Icons.Default.Home) {
        fun createRoute(seedId: String) = "app/seed_detail/$seedId"
    }
}

/**
 * NavGraph de autenticación
 */
@Composable
fun AuthNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.Login.route
    ) {
        composable(AuthScreen.Login.route) {
            AuthScreen(
                onAuthSuccess = onAuthSuccess,
                viewModel = authViewModel
            )
        }
    }
}

/**
 * NavGraph principal de la aplicación (con Bottom Navigation)
 */
@Composable
fun AppNavGraph(
    navController: NavHostController,
    sessionViewModel: SessionViewModel,
    uid: String?,
    isGuest: Boolean,
    onSignOut: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    androidx.compose.material3.Scaffold(
        bottomBar = {
            // Solo mostrar BottomNavigation si no estamos en SeedDetail
            if (currentDestination?.route?.startsWith("app/seed_detail") != true) {
                NavigationBar {
                    val items = listOf(
                        AppScreen.Garden,
                        AppScreen.Stats,
                        AppScreen.Profile
                    )

                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Garden.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppScreen.Garden.route) {
                HomeScreen(
                    isGuest = isGuest,
                    userName = sessionViewModel.sessionState.value.userProfile?.name,
                    onSignOut = {}, // Se maneja desde ProfileScreen
                    authViewModel = null, // Ya no se necesita aquí
                    onSeedClick = { seedId ->
                        navController.navigate(AppScreen.SeedDetail("").createRoute(seedId))
                    },
                    uid = uid ?: "guest"
                )
            }

            composable(AppScreen.Stats.route) {
                StatsScreen(sessionViewModel = sessionViewModel)
            }

            composable(AppScreen.Profile.route) {
                ProfileScreen(
                    onNavigateToAuth = onSignOut,
                    sessionViewModel = sessionViewModel
                )
            }

            composable(
                route = "app/seed_detail/{seedId}",
                arguments = listOf(
                    navArgument("seedId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val seedId = backStackEntry.arguments?.getString("seedId") ?: ""
                SeedDetailScreen(
                    seedId = seedId,
                    uid = uid ?: "guest",
                    isGuest = isGuest,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

