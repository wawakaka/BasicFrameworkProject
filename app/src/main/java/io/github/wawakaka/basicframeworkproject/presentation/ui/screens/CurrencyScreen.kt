package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiEffect
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiEvent
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiState
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyViewModel
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.AppTopBar
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.CurrencyListItem
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.ErrorMessage
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.LoadingIndicator
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme

/**
 * Currency Screen with TOAD pattern (Milestone 6)
 * Uses CurrencyViewModel for state management
 */
@Composable
fun CurrencyScreen(
    viewModel: CurrencyViewModel,
    modifier: Modifier = Modifier
) {
    // Collect state with lifecycle awareness
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Load initial data on first composition
    LaunchedEffect(Unit) {
        viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
    }

    // Handle one-time effects
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CurrencyUiEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is CurrencyUiEffect.ShowError -> {
                    // Show error dialog (can be enhanced in M7)
                    Toast.makeText(
                        context,
                        "${effect.title}: ${effect.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is CurrencyUiEffect.NavigateBack -> {
                    // Handle navigation (will implement with Compose Navigation)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(title = "Currency Rates")
        },
        floatingActionButton = {
            // Show refresh FAB when not loading
            if (state !is CurrencyUiState.Loading) {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.handleEvent(CurrencyUiEvent.OnRefresh)
                    },
                    icon = { Icon(Icons.Filled.Refresh, "Refresh") },
                    text = { Text("Refresh") }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Render UI based on state
            when (state) {
                is CurrencyUiState.Idle -> {
                    // Initial state - show loading or empty
                    EmptyState(
                        onLoadData = {
                            viewModel.handleEvent(CurrencyUiEvent.OnLoadRates)
                        }
                    )
                }
                is CurrencyUiState.Loading -> {
                    LoadingIndicator()
                }
                is CurrencyUiState.Success -> {
                    val successState = state as CurrencyUiState.Success
                    CurrencyListContent(
                        currencies = successState.rates,
                        timestamp = successState.timestamp
                    )
                }
                is CurrencyUiState.Error -> {
                    val errorState = state as CurrencyUiState.Error
                    ErrorMessage(
                        message = errorState.message,
                        onRetry = {
                            viewModel.handleEvent(CurrencyUiEvent.OnRetry)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Legacy CurrencyScreen for backward compatibility
 * @Deprecated("Use CurrencyScreen(viewModel) with TOAD pattern")
 */
@Deprecated("Use CurrencyScreen(viewModel) with TOAD pattern")
@Composable
fun CurrencyScreenLegacy(
    isLoading: Boolean,
    currencies: List<Pair<String, Double>>,
    error: Throwable?,
    onLoadData: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            isLoading -> {
                LoadingIndicator()
            }
            error != null -> {
                ErrorMessage(
                    message = error.message ?: "Unknown error occurred",
                    onRetry = onRetry
                )
            }
            currencies.isEmpty() -> {
                EmptyState(onLoadData = onLoadData)
            }
            else -> {
                CurrencyListContent(currencies = currencies)
            }
        }
    }
}

@Composable
fun CurrencyListContent(
    currencies: List<Pair<String, Double>>,
    timestamp: String = "",
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Show timestamp header if available
        if (timestamp.isNotEmpty()) {
            item {
                Text(
                    text = "Last updated: $timestamp",
                    modifier = Modifier.padding(16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
        }

        // Show currency items
        items(currencies) { (code, rate) ->
            CurrencyListItem(
                currencyCode = code,
                rate = rate
            )
        }
    }
}

@Composable
fun EmptyState(
    onLoadData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("No currency data available")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onLoadData) {
                Text("Load Currency Rates")
            }
        }
    }
}

// ========== PREVIEWS ==========

@Preview(showBackground = true)
@Composable
private fun CurrencyScreenLoadingPreview() {
    BasicFrameworkTheme {
        CurrencyScreenLegacy(
            isLoading = true,
            currencies = emptyList(),
            error = null,
            onLoadData = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyScreenSuccessPreview() {
    BasicFrameworkTheme {
        CurrencyScreenLegacy(
            isLoading = false,
            currencies = listOf(
                "USD" to 1.0,
                "EUR" to 0.92,
                "GBP" to 0.78,
                "JPY" to 149.56
            ),
            error = null,
            onLoadData = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyScreenErrorPreview() {
    BasicFrameworkTheme {
        CurrencyScreenLegacy(
            isLoading = false,
            currencies = emptyList(),
            error = Exception("Network error"),
            onLoadData = {},
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyScreenEmptyPreview() {
    BasicFrameworkTheme {
        CurrencyScreenLegacy(
            isLoading = false,
            currencies = emptyList(),
            error = null,
            onLoadData = {},
            onRetry = {}
        )
    }
}
