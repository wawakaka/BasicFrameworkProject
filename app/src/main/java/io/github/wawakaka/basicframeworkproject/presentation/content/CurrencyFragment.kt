package io.github.wawakaka.basicframeworkproject.presentation.content

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import io.github.wawakaka.basicframeworkproject.presentation.ui.screens.CurrencyScreen
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.koin.androidx.scope.createScope
import org.koin.core.scope.Scope

class CurrencyFragment : Fragment(), CurrencyContract.View {

    private val scope: Scope by lazy { createScope(this) }
    private val presenter: CurrencyPresenter by scope.inject()

    // State for UI updates from presenter
    private var isLoading by mutableStateOf(false)
    private var currencies by mutableStateOf<List<Pair<String, Double>>>(emptyList())
    private var error by mutableStateOf<Throwable?>(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            BasicFrameworkTheme {
                CurrencyScreen(
                    isLoading = isLoading,
                    currencies = currencies,
                    error = error,
                    onLoadData = {
                        presenter.onButtonClickedEvent()
                    },
                    onRetry = {
                        error = null
                        presenter.onButtonClickedEvent()
                    }
                )
            }
        }
    }

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

    companion object {
        private val TAG = CurrencyFragment::class.java.simpleName
    }
}
