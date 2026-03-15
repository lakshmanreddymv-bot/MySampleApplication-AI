package com.example.mysampleapplication.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(vm: DetailViewModel, onBack: () -> Unit) {
    val uiState by vm.uiState.collectAsState()

    val title = when (val state = uiState) {
        is DetailUiState.Success -> state.item.text
        is DetailUiState.Error -> state.item?.text ?: "Detail"
        is DetailUiState.Loading -> "Loading..."
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
                is DetailUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
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