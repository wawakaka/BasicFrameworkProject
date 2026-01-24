package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.CurrencyListItem
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.ErrorMessage
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.LoadingIndicator
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme

@Composable
fun CurrencyScreen(
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
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
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

@Preview(showBackground = true)
@Composable
private fun CurrencyScreenLoadingPreview() {
    BasicFrameworkTheme {
        CurrencyScreen(
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
        CurrencyScreen(
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
        CurrencyScreen(
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
        CurrencyScreen(
            isLoading = false,
            currencies = emptyList(),
            error = null,
            onLoadData = {},
            onRetry = {}
        )
    }
}
