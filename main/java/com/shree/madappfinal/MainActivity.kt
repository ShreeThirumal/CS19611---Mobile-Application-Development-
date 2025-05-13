package com.shree.madappfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shree.madappfinal.ui.theme.MADAppFinalTheme
import com.shree.madappfinal.ui.screens.home.HomeScreen
import com.shree.madappfinal.ui.screens.login.LoginScreen
import com.shree.madappfinal.ui.screens.text_translation.TextTranslationScreen
import com.shree.madappfinal.ui.screens.voice_translation.VoiceTranslationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MADAppFinalTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TranslatorApp()
                }
            }
        }
    }
}

@Composable
fun TranslatorApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onTextTranslationClick = { navController.navigate("text_translation") },
                onVoiceTranslationClick = { navController.navigate("voice_translation") }
            )
        }
        composable("text_translation") {
            TextTranslationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("voice_translation") {
            VoiceTranslationScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}