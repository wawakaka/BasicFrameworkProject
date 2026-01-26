package io.github.wawakaka.basicframeworkproject.presentation.ui.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyState
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI integration tests for CurrencyScreenContent.
 *
 * Uses Medium-style TOAD state model (CurrencyState).
 */
@RunWith(AndroidJUnit4::class)
class CurrencyScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun idleState_shouldShowEmptyState() {
        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyScreenContent(
                    uiState = CurrencyState(),
                    onRefresh = {},
                    onLoadData = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No currency data available").assertIsDisplayed()
        composeTestRule.onNodeWithText("Load Currency Rates").assertIsDisplayed()
    }

    @Test
    fun loadingState_shouldShowLoadingIndicator() {
        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyScreenContent(
                    uiState = CurrencyState(isLoading = true),
                    onRefresh = {},
                    onLoadData = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Loading").assertIsDisplayed()
    }

    @Test
    fun successState_shouldShowCurrencyList() {
        val mockCurrencies = listOf(
            "USD" to java.math.BigDecimal("1.0"),
            "EUR" to java.math.BigDecimal("0.92"),
            "GBP" to java.math.BigDecimal("0.78"),
            "JPY" to java.math.BigDecimal("149.56")
        )

        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyScreenContent(
                    uiState = CurrencyState(
                        rates = mockCurrencies,
                        timestamp = "2026-01-01 00:00:00"
                    ),
                    onRefresh = {},
                    onLoadData = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("USD").assertIsDisplayed()
        composeTestRule.onNodeWithText("EUR").assertIsDisplayed()
        composeTestRule.onNodeWithText("GBP").assertIsDisplayed()
        composeTestRule.onNodeWithText("JPY").assertIsDisplayed()
        composeTestRule.onNodeWithText("Last updated: 2026-01-01 00:00:00").assertIsDisplayed()
    }

    @Test
    fun errorState_shouldShowErrorMessage() {
        val errorMessage = "Network error occurred"

        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyScreenContent(
                    uiState = CurrencyState(errorMessage = errorMessage),
                    onRefresh = {},
                    onLoadData = {},
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun loadButton_whenClicked_shouldTriggerLoadAction() {
        var loadActionTriggered = false

        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyScreenContent(
                    uiState = CurrencyState(),
                    onRefresh = {},
                    onLoadData = { loadActionTriggered = true },
                    onRetry = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Load Currency Rates").performClick()

        assert(loadActionTriggered)
    }

    @Test
    fun retryButton_whenClicked_shouldTriggerRetryAction() {
        var retryTriggered = false

        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyScreenContent(
                    uiState = CurrencyState(errorMessage = "Error"),
                    onRefresh = {},
                    onLoadData = {},
                    onRetry = { retryTriggered = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Retry").performClick()

        assert(retryTriggered)
    }
}
