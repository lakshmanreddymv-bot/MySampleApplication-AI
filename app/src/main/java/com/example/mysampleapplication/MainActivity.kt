package com.example.mysampleapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mysampleapplication.BuildConfig
import com.example.mysampleapplication.data.api.GeminiApiImpl
import com.example.mysampleapplication.data.repository.ItemRepositoryImpl
import com.example.mysampleapplication.ui.detail.DetailScreen
import com.example.mysampleapplication.ui.detail.DetailViewModel
import com.example.mysampleapplication.ui.detail.DetailViewModelFactory
import com.example.mysampleapplication.ui.list.ListScreen
import com.example.mysampleapplication.ui.list.ListViewModel
import com.example.mysampleapplication.ui.list.ListViewModelFactory
import com.example.mysampleapplication.ui.theme.MySampleApplicationTheme

// S: Single Responsibility — wires the dependency graph and hosts the navigation graph
// D: Dependency Inversion — constructs concrete impls here; all downstream code depends on interfaces

/**
 * Single activity host for the app.
 *
 * Constructs the dependency graph manually (no DI framework) using [remember] so that
 * the repository and API client survive recomposition but are scoped to the activity lifetime.
 *
 * Navigation graph:
 * - `list` → [ListScreen] — shows the searchable item catalogue
 * - `detail/{itemId}` → [DetailScreen] — shows the AI-generated description for a selected item
 */
class MainActivity : ComponentActivity() {

    /**
     * Initialises edge-to-edge display, builds the dependency graph, and sets up
     * the Compose navigation host.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySampleApplicationTheme {
                val repository = remember {
                    ItemRepositoryImpl(GeminiApiImpl(BuildConfig.GEMINI_API_KEY))
                }
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "list") {
                    composable("list") {
                        val vm: ListViewModel = viewModel(factory = ListViewModelFactory(repository))
                        ListScreen(vm, onItemClick = { item ->
                            navController.navigate("detail/${item.id}")
                        })
                    }
                    composable("detail/{itemId}") { backStackEntry ->
                        val itemId = backStackEntry.arguments?.getString("itemId")?.toIntOrNull()
                            ?: return@composable
                        val vm: DetailViewModel =
                            viewModel(factory = DetailViewModelFactory(repository))
                        androidx.compose.runtime.LaunchedEffect(itemId) { vm.loadDetail(itemId) }
                        DetailScreen(vm, onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
