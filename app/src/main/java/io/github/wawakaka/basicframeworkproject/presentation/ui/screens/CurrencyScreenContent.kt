package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiState
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.CurrencyListItem
import io.github.wawakaka.ui.components.AppTopBar
import io.github.wawakaka.ui.components.ErrorMessage
import io.github.wawakaka.ui.components.LoadingIndicator
import java.math.BigDecimal

@Composable
fun CurrencyScreenContent(
    uiState: CurrencyUiState,
    onRefresh: () -> Unit,
    onLoadData: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            AppTopBar(title = "Currency Rates")
        },
        floatingActionButton = {
            if (uiState !is CurrencyUiState.Loading) {
                ExtendedFloatingActionButton(
                    onClick = onRefresh,
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
            when (uiState) {
                is CurrencyUiState.Idle -> {
                    EmptyState(onLoadData = onLoadData)
                }

                is CurrencyUiState.Loading -> {
                    LoadingIndicator()
                }

                is CurrencyUiState.Success -> {
                    CurrencyListContent(
                        currencies = uiState.rates,
                        timestamp = uiState.timestamp
                    )
                }

                is CurrencyUiState.Error -> {
                    ErrorMessage(
                        message = uiState.message,
                        onRetry = onRetry
                    )
                }
            }
        }
    }
}

@Composable
fun CurrencyListContent(
    currencies: List<Pair<String, BigDecimal>>,
    timestamp: String = "",
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        if (timestamp.isNotEmpty()) {
            item {
                Text(
                    text = "Last updated: $timestamp",
                    modifier = Modifier.padding(16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                )
            }
        }

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