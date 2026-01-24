package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.base.BasePresenter
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import kotlinx.coroutines.launch

/**
 * Currency Presenter using MVP pattern
 * @deprecated Migrate to TOAD pattern with CurrencyViewModel (Milestone 6).
 * This class will be removed in Milestone 7.
 */
@Deprecated(
    message = "Migrate to TOAD pattern with CurrencyViewModel (Milestone 6)",
    replaceWith = ReplaceWith(
        expression = "CurrencyViewModel",
        imports = ["io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyViewModel"]
    ),
    level = DeprecationLevel.WARNING
)
class CurrencyPresenter(
    private val usecase: GetLatestRatesUsecase
) : BasePresenter<CurrencyContract.View>(), CurrencyContract.Presenter {

    override fun onButtonClickedEvent() {
        presenterScope.launch {
            view?.showLoading()
            try {
                val data = usecase.getLatestCurrencyRates()
                view?.onGetDataSuccess(data)
            } catch (e: Exception) {
                view?.onGetDataFailed(e)
            } finally {
                view?.hideLoading()
            }
        }
    }
}