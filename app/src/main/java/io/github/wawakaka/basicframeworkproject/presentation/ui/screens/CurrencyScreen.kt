package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyAction
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyEvent
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyViewModel

/**
 * Currency Screen with TOAD pattern (Milestone 6)
 * Uses CurrencyViewModel for state management
 * Delegates rendering to stateless UI module
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
        viewModel.runAction(CurrencyAction.LoadRates)
    }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CurrencyEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                is CurrencyEvent.ShowError -> {
                    Toast.makeText(
                        context,
                        "${event.title}: ${event.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                is CurrencyEvent.NavigateBack -> {
                    // Handle navigation
                }
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
