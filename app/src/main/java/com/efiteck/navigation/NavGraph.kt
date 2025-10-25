package com.efiteck.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.efiteck.screens.*
import com.efiteck.viewmodel.AuthViewModel
import com.efiteck.viewmodel.PredictiveViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Predictive : Screen("predictive")
    object Notifications : Screen("notifications")
    object Settings : Screen("settings")
    object Help : Screen("help")
    object About : Screen("about")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    authViewModel: AuthViewModel = viewModel(),
    predictiveViewModel: PredictiveViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                authViewModel = authViewModel,
                onNavigateToRoute = { route ->
                    navController.navigate(route)
                }
            )
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Predictive.route) {
            PredictiveScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                predictiveViewModel = predictiveViewModel
            )
        }

        composable(Screen.Notifications.route) {
            PlaceholderScreen(
                title = "Notificaciones",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            PlaceholderScreen(
                title = "Configuraciones",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Help.route) {
            PlaceholderScreen(
                title = "Ayuda",
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.About.route) {
            PlaceholderScreen(
                title = "Acerca de",
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

