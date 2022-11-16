package com.example.signin

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.signin.data.model.GoogleUser
import com.example.signin.ui.theme.SignInTheme
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController


    val signInViewModel = SignInViewModel()
    private var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = applicationContext

        setContent {
            runBlocking {
                val datastore = LoggerDatastore(context)
                this@MainActivity.apply{
                    datastore.getLog.collect{
                        signInViewModel.changeLog()
                    }

                    if(signInViewModel.checkLoggerBool()){
                        signInViewModel.setRoute()
                        Log.d("runblocking", signInViewModel.route.value)
                    }
                }
            }

            SignInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val datastore = LoggerDatastore(context)
                    navController = rememberNavController()
                    NavGraph(navController = navController, datastore = datastore, signInViewModel = signInViewModel)
                }
            }
        }
    }
}


@Composable
fun LoginScreen(
    signInViewModel: SignInViewModel = viewModel(),
    datastore: LoggerDatastore,
    navigateToProfileScreen: () -> Unit
) {
    Log.d("Login", signInViewModel.name.collectAsState().value)

    val signInLauncher = rememberLauncherForActivityResult(
        contract = signInViewModel.firebaseAuthUiContract,
        onResult = { res -> signInViewModel.onSignInResult(
            res,
            navigateToProfileScreen,
            datastore
        ) }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                signInViewModel.onSignInAttempt(signInLauncher)
            }
        ){
            Text("Sign Up")
        }
    }
}

@Composable
fun ProfileScreen(
    signInViewModel: SignInViewModel
){
    val name = signInViewModel.name.collectAsState()
    val email = signInViewModel.email.collectAsState()
    val phone = signInViewModel.phone.collectAsState()
    val photoUrl = signInViewModel.photoUrl.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ){
        AsyncImage(
            modifier = Modifier
                .clip(CircleShape)
                .size(120.dp),
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(photoUrl.value)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.FillBounds,
        )
        UserInfo(qInfo = "Name", aInfo = name.value)
        UserInfo(qInfo = "Email", aInfo = email.value)
        UserInfo(qInfo = "Phone Number", aInfo = phone.value)
    }
}

@Composable
fun UserInfo(qInfo: String, aInfo: String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(qInfo)
        Text(aInfo)
        Log.d("aInfo", aInfo)
    }
}




@Preview
@Composable
fun AuthScreenTest(){
    SignInTheme {
    }
}