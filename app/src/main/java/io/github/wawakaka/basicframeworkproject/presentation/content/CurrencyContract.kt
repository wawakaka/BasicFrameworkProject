package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.base.BaseContract

/**
 * Currency Contract using MVP pattern
 * @deprecated Migrate to TOAD pattern with CurrencyUiState/Event/Effect (Milestone 6).
 * This class will be removed in Milestone 7.
 */
@Deprecated(
    message = "Migrate to TOAD pattern with CurrencyUiState/Event/Effect (Milestone 6)",
    replaceWith = ReplaceWith(
        expression = "CurrencyUiState, CurrencyUiEvent, CurrencyUiEffect",
        imports = [
            "io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiState",
            "io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiEvent",
            "io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyUiEffect"
        ]
    ),
    level = DeprecationLevel.WARNING
)
class CurrencyContract {

    interface View : BaseContract.View {
        fun showLoading()
        fun hideLoading()
        fun onGetDataSuccess(data: List<Pair<String, Double>>)
        fun onGetDataFailed(throwable: Throwable)
    }

    interface Presenter : BaseContract.Presenter<View> {
        fun onButtonClickedEvent()
    }
}