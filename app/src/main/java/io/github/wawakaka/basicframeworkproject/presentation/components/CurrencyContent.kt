package io.github.wawakaka.basicframeworkproject.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.wawakaka.basicframeworkproject.theme.BasicFrameworkTheme

/**
 * Currency content screen
 * Displays loading state, error messages, or list of currency rates
 *
 * @param isLoading Whether the content is currently loading
 * @param errorMessage Error message to display, if any
 * @param currencyRates List of currency rates as pairs of (code, value)
 * @param onRefresh Callback when refresh button is clicked
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun CurrencyContent(
    isLoading: Boolean,
    errorMessage: String?,
    currencyRates: List<Pair<String, Double>>,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading && currencyRates.isEmpty()) {
            LoadingIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            )
        } else if (errorMessage != null) {
            ErrorMessage(
                message = errorMessage,
                modifier = Modifier.padding(top = 16.dp)
            )

            Button(
                onClick = onRefresh,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Retry")
            }
        } else if (currencyRates.isNotEmpty()) {
            Button(
                onClick = onRefresh,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Refresh")
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(currencyRates) { (code, value) ->
                    CurrencyListItem(
                        currencyCode = code,
                        currencyValue = value
                    )
                }
            }
        } else {
            Button(
                onClick = onRefresh,
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Load Rates")
            }
        }
    }
}

@Preview
@Composable
fun CurrencyContentLoadingPreview() {
    BasicFrameworkTheme {
        CurrencyContent(
            isLoading = true,
            errorMessage = null,
            currencyRates = emptyList(),
            onRefresh = {}
        )
    }
}

@Preview
@Composable
fun CurrencyContentWithDataPreview() {
    BasicFrameworkTheme {
        CurrencyContent(
            isLoading = false,
            errorMessage = null,
            currencyRates = listOf(
                "USD" to 1.2345,
                "EUR" to 0.8765,
                "GBP" to 0.7654,
                "JPY" to 130.45
            ),
            onRefresh = {}
        )
    }
}

@Preview
@Composable
fun CurrencyContentErrorPreview() {
    BasicFrameworkTheme {
        CurrencyContent(
            isLoading = false,
            errorMessage = "Failed to load currency rates. Please check your internet connection.",
            currencyRates = emptyList(),
            onRefresh = {}
        )
    }
}
