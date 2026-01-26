package io.github.wawakaka.basicframeworkproject.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.wawakaka.ui.theme.BasicFrameworkTheme
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun CurrencyListItem(
    currencyCode: String,
    rate: BigDecimal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = currencyCode,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = rate.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyListItemPreview() {
    BasicFrameworkTheme {
        CurrencyListItem(
            currencyCode = "USD",
            rate = BigDecimal("1.2345")
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyListItemMultiplePreview() {
    BasicFrameworkTheme {
        androidx.compose.foundation.layout.Column {
            CurrencyListItem(currencyCode = "USD", rate = BigDecimal(1.0000))
            CurrencyListItem(currencyCode = "EUR", rate = BigDecimal(0.9234))
            CurrencyListItem(currencyCode = "GBP", rate = BigDecimal(0.7812))
            CurrencyListItem(currencyCode = "JPY", rate = BigDecimal(149.5600))
        }
    }
}
