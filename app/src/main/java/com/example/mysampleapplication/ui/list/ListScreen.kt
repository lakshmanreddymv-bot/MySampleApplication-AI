package com.example.mysampleapplication.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.mysampleapplication.domain.model.MyListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(vm: ListViewModel, onItemClick: (MyListItem) -> Unit) {
    val query by vm.query.collectAsState()
    val uiState by vm.uiState.collectAsState()

    val isLoading = uiState is ListUiState.Loading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = vm::onQueryChange,
                        placeholder = { Text("Search with AI...") },
                        singleLine = true,
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 8.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { vm.search(query) }),
                        trailingIcon = {
                            if (query.isNotEmpty() && !isLoading) {
                                IconButton(onClick = vm::clear) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is ListUiState.Idle -> ItemList(items = vm.allItems, onItemClick = onItemClick)
                is ListUiState.Loading -> {
                    // Show previous item list dimmed in the background while loading
                    ItemList(
                        items = vm.allItems,
                        onItemClick = {},
                        modifier = Modifier.fillMaxSize()
                    )
                    // Semi-transparent overlay with spinner + label
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
                                text = "Searching with AI...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                is ListUiState.Success -> {
                    if (state.items.isEmpty()) {
                        Text("No results found", modifier = Modifier.align(Alignment.Center))
                    } else {
                        ItemList(items = state.items, onItemClick = onItemClick)
                    }
                }
                is ListUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error: ${state.message}")
                        TextButton(onClick = { vm.search(query) }) { Text("Retry") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemList(
    items: List<MyListItem>,
    onItemClick: (MyListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            Text(
                text = item.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(item) }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}