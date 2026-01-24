package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiEffect
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiEvent
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiState
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
                    Toast.makeText(
                        context,
                        "${effect.title}: ${effect.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                is CurrencyUiEffect.NavigateBack -> {
                    // Handle navigation
                }
            }
        }
    }

    CurrencyScreenContent(
        uiState = state,
        onRefresh = { viewModel.handleEvent(CurrencyUiEvent.OnRefresh) },
        onLoadData = { viewModel.handleEvent(CurrencyUiEvent.OnLoadRates) },
        onRetry = { viewModel.handleEvent(CurrencyUiEvent.OnRetry) },
        modifier = modifier
    )
}
