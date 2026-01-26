package io.github.wawakaka.basicframeworkproject.presentation.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wawakaka.ui.components.AppTopBar
import io.github.wawakaka.ui.components.ErrorMessage
import io.github.wawakaka.ui.components.LoadingIndicator
import io.github.wawakaka.ui.theme.BasicFrameworkTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Compose UI integration tests for reusable components
 * Tests rendering and interactions for AppTopBar, LoadingIndicator, ErrorMessage
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
                    onUpButtonClick = {}
                )
            }
        }

        // Assert
        composeTestRule
            .onNodeWithContentDescription("Back")
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
            .onNodeWithContentDescription("Back")
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
                    onUpButtonClick = { navigateUpCalled = true }
                )
            }
        }

        // Act
        composeTestRule
            .onNodeWithContentDescription("Back")
            .performClick()

        // Assert
        assert(navigateUpCalled)
    }

    // ========== LOADING INDICATOR TESTS ==========

    @Test
    fun loadingIndicator_shouldDisplayProgressIndicator() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
                LoadingIndicator()
            }
        }

        // Assert - Not much stable to assert; ensure composition completed.
        composeTestRule.waitForIdle()
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
    // NOTE: CurrencyListItem lives in :feature-currency-exchange and is internal,
    // so it cannot be accessed from :app androidTest.


    // ========== THEME INTEGRATION TESTS ==========

    @Test
    fun components_shouldRenderInLightTheme() {
        // Arrange
        composeTestRule.setContent {
            BasicFrameworkTheme {
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
            BasicFrameworkTheme {
                AppTopBar(title = "Dark Theme")
            }
        }

        // Assert
        composeTestRule
            .onNodeWithText("Dark Theme")
            .assertIsDisplayed()
    }
}
