package io.github.wawakaka.basicframeworkproject.presentation.content

import android.util.Log
import io.github.wawakaka.domain.usecase.GetLatestRatesUsecase
import io.reactivex.disposables.CompositeDisposable

class CurrencyPresenter(
    private val usecase: GetLatestRatesUsecase,
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
) : CurrencyContract.Presenter {

    private lateinit var view: CurrencyContract.View

    override fun onButtonClickedEvent() {
        usecase.getLatestCurrencyRates()
            .doOnTerminate { view.hideLoading() }
            .subscribe(
                { view.onGetDataSuccess(it) },
                { view.onGetDataFailed(it) },
                { Log.e("onButtonClickedEvent", "completed") }
            ).let(compositeDisposable::add)
    }

    override fun detach() {
        compositeDisposable.clear()
    }

    override fun attach(view: CurrencyContract.View) {
        this.view = view
    }
}