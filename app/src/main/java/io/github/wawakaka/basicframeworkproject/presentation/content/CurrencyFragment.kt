package io.github.wawakaka.basicframeworkproject.presentation.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import io.github.wawakaka.basicframeworkproject.presentation.ui.screens.CurrencyScreen
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
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

    // ========== MVP Pattern (Deprecated - commented out) ==========
    /*
    // Old implementation with Presenter
    private val scope: Scope by lazy { createScope(this) }
    private val presenter: CurrencyPresenter by scope.inject()
    private var isLoading by mutableStateOf(false)
    private var currencies by mutableStateOf<List<Pair<String, Double>>>(emptyList())
    private var error by mutableStateOf<Throwable?>(null)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
    }

    override fun onDestroy() {
        presenter.detach()
        scope.close()
        super.onDestroy()
    }

    // CurrencyContract.View implementation
    override fun showLoading() {
        isLoading = true
        error = null
    }

    override fun hideLoading() {
        isLoading = false
    }

    override fun onGetDataSuccess(data: List<Pair<String, Double>>) {
        currencies = data
        error = null
        Log.d(TAG, "Successfully loaded ${data.size} currency rates")
    }

    override fun onGetDataFailed(throwable: Throwable) {
        error = throwable
        Log.e(TAG, "Failed to load currency rates", throwable)
    }
    */

    companion object {
        private val TAG = CurrencyFragment::class.java.simpleName
    }
}
