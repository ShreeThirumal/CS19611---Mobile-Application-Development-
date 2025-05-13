package com.shree.madappfinal.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onTextTranslationClick: () -> Unit,
    onVoiceTranslationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Translation App",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        Button(
            onClick = onTextTranslationClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.TextFields,
                contentDescription = "Text Translation",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Text Translation")
        }

        Button(
            onClick = onVoiceTranslationClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Mic,
                contentDescription = "Voice Translation",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Voice Translation")
        }
    }
} 