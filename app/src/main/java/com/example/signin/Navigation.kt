package com.example.signin

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.datastore.dataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavGraph(
    navController: NavHostController,
    datastore: LoggerDatastore,
    signInViewModel: SignInViewModel
){

    Log.d("Navigation", signInViewModel.route.collectAsState().value)
    NavHost(
        navController = navController,
        startDestination = signInViewModel.route.collectAsState().value
    ){

        composable(
            route = "login"
        ){
            LoginScreen(
                signInViewModel = signInViewModel,
                datastore = datastore,
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