package com.example.signin

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch


class SignInViewModel: ViewModel() {

    private var signInIntent: Intent = Intent()

    val firebaseAuthUiContract = FirebaseAuthUIActivityResultContract()

    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            Log.d("Success", user.displayName)
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
}