package com.shree.madappfinal.ui.screens.voice_translation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.shree.madappfinal.data.TranslationState
import com.shree.madappfinal.data.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun VoiceTranslationScreen(
    onNavigateBack: () -> Unit,
    viewModel: TranslationViewModel = viewModel()
) {
    var selectedLanguage by remember { mutableStateOf("Spanish") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val translationState by viewModel.translationState.collectAsState()
    
    val languages = listOf("Spanish", "French", "German", "Italian", "Portuguese", "Russian", "Japanese", "Chinese")

    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    
    val speechRecognizer = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            results?.get(0)?.let { recognizedText ->
                viewModel.translateText(recognizedText, selectedLanguage)
            }
        }
        isRecording = false
    }

    LaunchedEffect(Unit) {
        viewModel.initializeTextToSpeech(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Voice Translation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedLanguage,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Target Language") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                selectedLanguage = language
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Mic Button
            FloatingActionButton(
                onClick = {
                    if (permissionState.status.isGranted) {
                        startVoiceRecognition(context, speechRecognizer)
                        isRecording = true
                    } else {
                        permissionState.launchPermissionRequest()
                    }
                },
                modifier = Modifier.size(72.dp)
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                    modifier = Modifier.size(32.dp)
                )
            }

            if (isRecording) {
                Text(
                    text = "Recording...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when (translationState) {
                is TranslationState.Success -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = (translationState as TranslationState.Success).translatedText,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.speakText((translationState as TranslationState.Success).translatedText)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak Translation"
                                    )
                                }
                            }
                        }
                    }
                }
                is TranslationState.Error -> {
                    Text(
                        text = (translationState as TranslationState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
                else -> {}
            }
        }
    }
}

private fun startVoiceRecognition(context: Context, launcher: androidx.activity.result.ActivityResultLauncher<Intent>) {
    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to translate")
    }
    launcher.launch(intent)
} 