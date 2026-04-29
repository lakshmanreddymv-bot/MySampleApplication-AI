package com.example.mysampleapplication.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Root composable for the item detail screen.
 *
 * Observes [DetailViewModel.uiState] and recomposes when state changes:
 * - [DetailUiState.Loading] — shows a full-screen spinner with "Generating AI summary..."
 * - [DetailUiState.Success] — renders the AI-generated description
 * - [DetailUiState.Error] — shows an error message with a retry button
 *
 * The top bar title reflects the item name as soon as it is known, even during loading.
 *
 * @param vm The [DetailViewModel] driving this screen.
 * @param onBack Callback invoked when the user taps the back navigation button.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(vm: DetailViewModel, onBack: () -> Unit) {
    val uiState by vm.uiState.collectAsState()

    val title = when (val state = uiState) {
        is DetailUiState.Success -> state.item.text
        is DetailUiState.Error -> state.item?.text ?: "Detail"
        is DetailUiState.Loading -> state.item?.text ?: "Loading..."
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            when (val state = uiState) {
                is DetailUiState.Loading -> {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Generating AI summary...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                is DetailUiState.Success -> Text(state.description)
                is DetailUiState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error: ${state.message}")
                    TextButton(onClick = { state.item?.let { vm.loadDetail(it.id) } }) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
