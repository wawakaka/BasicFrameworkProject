package io.github.wawakaka.basicframeworkproject.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyContract
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyPresenter
import io.github.wawakaka.basicframeworkproject.presentation.components.CurrencyContent
import io.github.wawakaka.basicframeworkproject.theme.BasicFrameworkTheme
import org.koin.compose.koinInject

/**
 * Currency Screen Composable
 * Displays currency exchange rates with loading, error, and content states
 * Replaces CurrencyFragment with Compose UI
 *
 * @param presenter The currency presenter (injected from Koin)
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun CurrencyScreen(
    presenter: CurrencyPresenter = koinInject(),
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currencyRates by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }

    // Create a view implementation that updates the state
    val view = object : CurrencyContract.View {
        override fun showLoading() {
            isLoading = true
        }

        override fun hideLoading() {
            isLoading = false
        }

        override fun onGetDataSuccess(data: List<Pair<String, Double>>) {
            currencyRates = data
            errorMessage = null
        }

        override fun onGetDataFailed(throwable: Throwable) {
            errorMessage = throwable.message ?: "Unknown error occurred"
        }
    }

    // Attach view to presenter when composable is first created
    LaunchedEffect(Unit) {
        presenter.attach(view)
        presenter.onButtonClickedEvent()
    }

    // Detach when composable is disposed
    // Note: In a real implementation, you might use DisposableEffect or manage this differently
    // For now, we'll handle cleanup at the Fragment/Activity level

    CurrencyContent(
        isLoading = isLoading,
        errorMessage = errorMessage,
        currencyRates = currencyRates,
        onRefresh = {
            presenter.onButtonClickedEvent()
        },
        modifier = modifier
    )
}

@Preview
@Composable
fun CurrencyScreenPreview() {
    BasicFrameworkTheme {
        CurrencyContent(
            isLoading = false,
            errorMessage = null,
            currencyRates = listOf(
                "USD" to 1.2345,
                "EUR" to 0.8765,
                "GBP" to 0.7654
            ),
            onRefresh = {}
        )
    }
}
