package io.github.wawakaka.basicframeworkproject.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import io.github.wawakaka.feature.currencyexchange.ui.CurrencyExchangeRoute
import io.github.wawakaka.ui.theme.BasicFrameworkTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BasicFrameworkTheme {
                CurrencyExchangeRoute()
            }
        }
    }
}
