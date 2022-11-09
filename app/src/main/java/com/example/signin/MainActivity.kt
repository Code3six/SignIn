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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.signin.ui.theme.SignInTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import kotlinx.coroutines.launch
import kotlin.math.sign

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}


@Composable
fun LoginScreen(
    signInViewModel: SignInViewModel = viewModel(),
    navigateToProfileScreen: () -> Unit
) {

    val signInLancher = rememberLauncherForActivityResult(
        contract = signInViewModel.firebaseAuthUiContract,
        onResult = { res -> signInViewModel.onSignInResult(res, navigateToProfileScreen) }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                signInViewModel.onSignInAttempt(signInLancher)
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

    val user by signInViewModel.user.collectAsState()

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
                .data(user.photoUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.FillBounds,
        )
        UserInfo(qInfo = "Name", aInfo = user.name)
        UserInfo(qInfo = "Email", aInfo = user.email)
        UserInfo(qInfo = "Phone Number", aInfo = user.phoneNumber)
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
        Log.d("aInfo","${aInfo}")
    }
}




@Preview
@Composable
fun AuthScreenTest(){
    SignInTheme {
        ProfileScreen(signInViewModel = SignInViewModel())
    }
}