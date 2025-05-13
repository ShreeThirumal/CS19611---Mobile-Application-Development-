package com.shree.madappfinal.ui.screens.text_translation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shree.madappfinal.data.TranslationState
import com.shree.madappfinal.data.TranslationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextTranslationScreen(
    onNavigateBack: () -> Unit,
    viewModel: TranslationViewModel = viewModel()
) {
    var inputText by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf("Spanish") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val translationState by viewModel.translationState.collectAsState()

    val languages = listOf("Spanish", "French", "German", "Italian", "Portuguese", "Russian", "Japanese", "Chinese")

    LaunchedEffect(Unit) {
        viewModel.initializeTextToSpeech(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Text Translation") },
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
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Enter text to translate") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                minLines = 3
            )

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
                        .padding(bottom = 16.dp)
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

            Button(
                onClick = {
                    viewModel.translateText(inputText, selectedLanguage)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = inputText.isNotEmpty() && translationState !is TranslationState.Loading
            ) {
                if (translationState is TranslationState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Translate",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Translate")
                }
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