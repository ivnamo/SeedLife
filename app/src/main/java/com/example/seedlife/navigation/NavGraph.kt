package com.example.seedlife.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.seedlife.ui.auth.AuthScreen
import com.example.seedlife.ui.auth.AuthViewModel
import com.example.seedlife.ui.home.HomeScreen
import com.example.seedlife.ui.seeddetail.SeedDetailScreen

/**
 * Rutas de navegación
 */
sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Home : Screen("home")
    data class SeedDetail(val seedId: String = "{seedId}") : Screen("seed_detail/{seedId}") {
        fun createRoute(seedId: String) = "seed_detail/$seedId"
    }
}

/**
 * NavGraph principal de la aplicación
 */
@Composable
fun SeedLifeNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Auth.route,
    authViewModel: AuthViewModel,
    uid: String?,
    isGuest: Boolean = false
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                isGuest = isGuest,
                userName = null, // Se obtendrá del ViewModel si es necesario
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                authViewModel = if (!isGuest) authViewModel else null,
                onSeedClick = { seedId ->
                    navController.navigate(Screen.SeedDetail("").createRoute(seedId))
                },
                uid = uid ?: "guest"
            )
        }

        composable(
            route = "seed_detail/{seedId}",
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
