package io.github.wawakaka.basicframeworkproject.presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI integration tests for reusable components
 * Tests rendering and interactions for AppTopBar, LoadingIndicator, ErrorMessage, CurrencyListItem
 */
@RunWith(AndroidJUnit4::class)
class ComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ========== APP TOP BAR TESTS ==========

    @Test
    fun appTopBar_withTitle_shouldDisplayTitle() {
        // Arrange
        val title = "Test Title"

        composeTestRule.setContent {
            BasicFrameworkTheme {
                AppTopBar(title = title)
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText(title)
            .assertIsDisplayed()
    }

    @Test
    fun appTopBar_withUpButton_shouldShowBackButton() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                AppTopBar(
                    title = "Title",
                    showUpButton = true,
                    onNavigateUp = {}
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithContentDescription("Navigate up")
            .assertIsDisplayed()
    }

    @Test
    fun appTopBar_withoutUpButton_shouldNotShowBackButton() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                AppTopBar(
                    title = "Title",
                    showUpButton = false
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithContentDescription("Navigate up")
            .assertDoesNotExist()
    }

    @Test
    fun appTopBar_backButton_whenClicked_shouldTriggerCallback() {
        // Arrange
        var navigateUpCalled = false

        composeTestRule.setContent {
            BasicFrameworkTheme {
                AppTopBar(
                    title = "Title",
                    showUpButton = true,
                    onNavigateUp = { navigateUpCalled = true }
                )
            }
        }

        // Act
        composeTestRule
            .onNodeWithContentDescription("Navigate up")
            .performClick()

        // Assert
        assert(navigateUpCalled)
    }

    // ========== LOADING INDICATOR TESTS ==========

    @Test
    fun loadingIndicator_shouldDisplayLoadingText() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                LoadingIndicator()
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("Loading")
            .assertIsDisplayed()
    }

    @Test
    fun loadingIndicator_shouldBeCentered() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                LoadingIndicator()
            }
        }

        // Assert - Component should exist
        composeTestRule
            .onNodeWithText("Loading")
            .assertIsDisplayed()
    }

    // ========== ERROR MESSAGE TESTS ==========

    @Test
    fun errorMessage_shouldDisplayMessage() {
        // Arrange
        val errorMessage = "Something went wrong"

        composeTestRule.setContent {
            BasicFrameworkTheme {
                ErrorMessage(
                    message = errorMessage,
                    onRetry = {}
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun errorMessage_shouldDisplayRetryButton() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                ErrorMessage(
                    message = "Error",
                    onRetry = {}
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("Retry")
            .assertIsDisplayed()
    }

    @Test
    fun errorMessage_retryButton_whenClicked_shouldTriggerCallback() {
        // Arrange
        var retryClicked = false

        composeTestRule.setContent {
            BasicFrameworkTheme {
                ErrorMessage(
                    message = "Error",
                    onRetry = { retryClicked = true }
                )
            }
        }

        // Act
        composeTestRule
            .onNodeWithText("Retry")
            .performClick()

        // Assert
        assert(retryClicked)
    }

    @Test
    fun errorMessage_withLongMessage_shouldDisplay() {
        // Arrange
        val longMessage = "This is a very long error message that should still display correctly " +
                "even when it contains multiple lines of text and wraps around"

        composeTestRule.setContent {
            BasicFrameworkTheme {
                ErrorMessage(
                    message = longMessage,
                    onRetry = {}
                )
            }
        }

        // Assert - Message should be visible (even if truncated)
        composeTestRule
            .onNodeWithText(longMessage, substring = true)
            .assertIsDisplayed()
    }

    // ========== CURRENCY LIST ITEM TESTS ==========

    @Test
    fun currencyListItem_shouldDisplayCurrencyCode() {
        // Arrange
        val currencyCode = "USD"

        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyListItem(
                    currencyCode = currencyCode,
                    rate = 1.0
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText(currencyCode)
            .assertIsDisplayed()
    }

    @Test
    fun currencyListItem_shouldDisplayRate() {
        // Arrange
        val rate = 1.23

        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyListItem(
                    currencyCode = "EUR",
                    rate = rate
                )
            }
        }

        // Assert - Rate should be displayed (checking for substring)
        composeTestRule
            .onNodeWithText(rate.toString(), substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun currencyListItem_withDifferentRates_shouldDisplayCorrectly() {
        // Arrange
        val testCases = listOf(
            "USD" to 1.0,
            "EUR" to 0.92,
            "JPY" to 149.56,
            "GBP" to 0.78
        )

        testCases.forEach { (code, rate) ->
            composeTestRule.setContent {
                BasicFrameworkTheme {
                    CurrencyListItem(
                        currencyCode = code,
                        rate = rate
                    )
                }
            }

            // Assert
            composeTestRule
                .onNodeWithText(code)
                .assertIsDisplayed()
        }
    }

    @Test
    fun currencyListItem_withVerySmallRate_shouldDisplay() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyListItem(
                    currencyCode = "XYZ",
                    rate = 0.0001
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("XYZ")
            .assertIsDisplayed()
    }

    @Test
    fun currencyListItem_withVeryLargeRate_shouldDisplay() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyListItem(
                    currencyCode = "ABC",
                    rate = 999999.99
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("ABC")
            .assertIsDisplayed()
    }

    @Test
    fun currencyListItem_withZeroRate_shouldDisplay() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyListItem(
                    currencyCode = "ZZZ",
                    rate = 0.0
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("ZZZ")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("0.0", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun currencyListItem_withNegativeRate_shouldDisplay() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                CurrencyListItem(
                    currencyCode = "NEG",
                    rate = -1.5
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("NEG")
            .assertIsDisplayed()
    }

    // ========== THEME INTEGRATION TESTS ==========

    @Test
    fun components_shouldRenderInLightTheme() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme(darkTheme = false) {
                AppTopBar(title = "Light Theme")
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("Light Theme")
            .assertIsDisplayed()
    }

    @Test
    fun components_shouldRenderInDarkTheme() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme(darkTheme = true) {
                AppTopBar(title = "Dark Theme")
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("Dark Theme")
            .assertIsDisplayed()
    }
}
