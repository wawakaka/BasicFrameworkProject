package io.github.wawakaka.basicframeworkproject.presentation.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import io.github.wawakaka.basicframeworkproject.presentation.ui.screens.CurrencyScreen
import io.github.wawakaka.ui.theme.BasicFrameworkTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Currency Fragment with TOAD pattern (Milestone 6)
 * Uses CurrencyViewModel for state management instead of Presenter
 */
class CurrencyFragment : Fragment() {

    // ========== TOAD Pattern (Milestone 6) ==========
    // Inject ViewModel from Koin
    private val currencyViewModel: CurrencyViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            BasicFrameworkTheme {
                // Use new TOAD-based CurrencyScreen
                CurrencyScreen(viewModel = currencyViewModel)
            }
        }
    }

    companion object {
        private val TAG = CurrencyFragment::class.java.simpleName
    }
}
