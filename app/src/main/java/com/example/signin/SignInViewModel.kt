package com.example.signin

import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signin.data.model.GoogleUser
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class SignInViewModel: ViewModel() {

    private var _user = MutableStateFlow(GoogleUser())
    val user: StateFlow<GoogleUser> = _user.asStateFlow()

    private var signInIntent: Intent = Intent()
    val firebaseAuthUiContract = FirebaseAuthUIActivityResultContract()

    private val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())

    private var _name = MutableStateFlow("")
    val name = _name
    private var _email = MutableStateFlow("")
    val email = _email
    private var _phone = MutableStateFlow("")
    val phone = _phone
    private var _photoUrl = MutableStateFlow("")
    val photoUrl = _photoUrl

    private var _route = MutableStateFlow("login")
    val route = _route

    private var _isLogged = MutableStateFlow(false)
    val isLogged: StateFlow<Boolean> = _isLogged


    fun onSignInResult(
        result: FirebaseAuthUIAuthenticationResult,
        navigateToProfile: () -> Unit,
        datastore: LoggerDatastore) {
        val response = result.idpResponse
        if (result.resultCode == ComponentActivity.RESULT_OK) {
            // Successfully signed in
            val userData = FirebaseAuth.getInstance().currentUser

            updateUserData(userData)
            saveLogin(datastore)
            navigateToProfile()

        } else {
            Log.d("Failed", "no message")
        }
    }

    private fun saveLogin(datastore: LoggerDatastore){
        viewModelScope.launch {
            datastore.saveInfo(
                name = user.value.name,
                email = user.value.email,
                photoUrl = user.value.photoUrl,
                phone = user.value.phoneNumber
            )
            Log.d("saveLogin", name.value)
        }
    }

    fun getLogin(datastore: LoggerDatastore){
        setName(datastore)
        setEmail(datastore)
        setPhone(datastore)
        setPhoto(datastore)
        setLog(datastore)
    }

    fun setRoute(){
        _route.value = "profile"
    }

    fun checkLogger(datastore: LoggerDatastore) {
        viewModelScope.launch{
            datastore.getName.collect {
                _isLogged.value = it != ""
                Log.d("checkLogger", it)
            }
            Log.d("isLoggedd", "${isLogged.value}")
        }
        Log.d("isLogged", "${isLogged.value}")
        if(isLogged.value) {
            _route.value = "profile"
        } else {
            _route.value = "login"
        }
    }

    fun checkLoggerBool():Boolean{
        return isLogged.value
    }

    private fun setEmail(datastore: LoggerDatastore){
        viewModelScope.launch{
            datastore.getEmail.collect{
                _email.value = it
            }
        }
    }

    private fun setName(datastore: LoggerDatastore){
        viewModelScope.launch{
            datastore.getName.collect{
                _name.value = it
            }
        }
    }

    fun changeLog(){
        _isLogged.value = true
    }

    private fun setLog(datastore: LoggerDatastore){
        viewModelScope.launch{
            datastore.getLog.collect{
                _isLogged.value = it
            }
        }
    }

    private fun setPhone(datastore: LoggerDatastore){
        viewModelScope.launch{
            datastore.getPhone.collect{
                _phone.value = it
            }
        }
    }

    private fun setPhoto(datastore: LoggerDatastore){
        viewModelScope.launch{
            datastore.getPhoto.collect{
                _photoUrl.value = it
            }
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