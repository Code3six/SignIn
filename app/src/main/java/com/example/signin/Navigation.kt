package com.example.signin

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(
    navController: NavHostController,
    signInViewModel: SignInViewModel = viewModel()
){
    NavHost(navController = navController, startDestination = "login" ){
        composable(
            route = "login"
        ){
            LoginScreen(
                signInViewModel = signInViewModel,
                navigateToProfileScreen = {
                    navController.navigate(
                        route = "profile",
                    )
                }
            )
        }

        composable(
            route = "profile",
        ){
            ProfileScreen(
                signInViewModel = signInViewModel
            )
        }
    }
}