package io.github.wawakaka.basicframeworkproject.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.wawakaka.basicframeworkproject.theme.BasicFrameworkTheme

/**
 * Currency list item component
 * Displays a single currency exchange rate with code and value
 *
 * @param currencyCode The currency code (e.g., "USD", "EUR")
 * @param currencyValue The exchange rate value
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun CurrencyListItem(
    currencyCode: String,
    currencyValue: Double,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = currencyCode,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "%.4f".format(currencyValue),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview
@Composable
fun CurrencyListItemPreview() {
    BasicFrameworkTheme {
        CurrencyListItem(
            currencyCode = "USD",
            currencyValue = 1.2345
        )
    }
}

@Preview
@Composable
fun CurrencyListItemDarkPreview() {
    BasicFrameworkTheme(isDarkTheme = true) {
        CurrencyListItem(
            currencyCode = "EUR",
            currencyValue = 0.8765
        )
    }
}
