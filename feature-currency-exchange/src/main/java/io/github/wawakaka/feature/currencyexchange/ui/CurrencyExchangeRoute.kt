package io.github.wawakaka.feature.currencyexchange.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyAction
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyEvent
import io.github.wawakaka.feature.currencyexchange.presentation.CurrencyViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Feature entry point. The host app should only call this composable.
 */
@Composable
fun CurrencyExchangeRoute(
    modifier: Modifier = Modifier
) {
    val viewModel: CurrencyViewModel = koinViewModel()

    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.runAction(CurrencyAction.LoadRates)
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CurrencyEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is CurrencyEvent.ShowError -> {
                    Toast.makeText(context, "${event.title}: ${event.message}", Toast.LENGTH_LONG)
                        .show()
                }

                CurrencyEvent.NavigateBack -> Unit
            }
        }
    }

    CurrencyScreenContent(
        uiState = state,
        onRefresh = { viewModel.runAction(CurrencyAction.RefreshRates) },
        onLoadData = { viewModel.runAction(CurrencyAction.LoadRates) },
        onRetry = { viewModel.runAction(CurrencyAction.RetryLoad) },
        modifier = modifier
    )
}
