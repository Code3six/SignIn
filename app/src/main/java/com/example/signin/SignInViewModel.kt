package com.example.signin

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.signin.data.model.GoogleUser
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SignInViewModel: ViewModel() {

    private var _user = MutableStateFlow(GoogleUser())
    val user: StateFlow<GoogleUser> = _user.asStateFlow()

    private var signInIntent: Intent = Intent()

    val firebaseAuthUiContract = FirebaseAuthUIActivityResultContract()

    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult, navigateToProfile: () -> Unit) {
        val response = result.idpResponse
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            // Successfully signed in
            val userData = FirebaseAuth.getInstance().currentUser
            updateUserData(userData)
            navigateToProfile()
        } else {
            Log.d("Failed", "no message")
        }
    }

    fun onSignInAttempt(signInLauncher: ActivityResultLauncher<Intent>){
        signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun updateUserData(
        userData: FirebaseUser
    ){
        _user.update {
            it.copy(
                photoUrl = userData.photoUrl.toString(),
                name = userData.displayName,
                email = userData.email,
                phoneNumber = userData.phoneNumber?:"No Data",
            )
        }
    }

}