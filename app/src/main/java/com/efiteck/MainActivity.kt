package com.efiteck

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.efiteck.navigation.NavGraph
import com.efiteck.navigation.Screen
import com.efiteck.ui.theme.EfiteckTheme
import com.efiteck.viewmodel.AuthState
import com.efiteck.viewmodel.AuthViewModel
import com.efiteck.viewmodel.PredictiveViewModel
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        enableEdgeToEdge()
        setContent {
            EfiteckTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val predictiveViewModel: PredictiveViewModel = viewModel()
                val authState by authViewModel.authState.collectAsState()
                
                // Determine start destination based on auth state
                LaunchedEffect(authState) {
                    when (authState) {
                        is AuthState.Authenticated -> {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                        else -> {}
                    }
                }
                
                val startDestination = when (authState) {
                    is AuthState.Authenticated -> Screen.Home.route
                    else -> Screen.Login.route
                }
                
                NavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    authViewModel = authViewModel,
                    predictiveViewModel = predictiveViewModel
                )
            }
        }
    }
}