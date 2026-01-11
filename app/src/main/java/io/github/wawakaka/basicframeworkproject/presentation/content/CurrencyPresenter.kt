package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.base.BasePresenter
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import kotlinx.coroutines.launch

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