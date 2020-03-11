package io.github.wawakaka.domain.usecase

import io.github.wawakaka.repository.currencyrates.CurrencyRatesRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class GetLatestRatesUsecase(private val currencyRatesRepository: CurrencyRatesRepository) {

    fun getLatestCurrencyRates(): Observable<List<Pair<String, Double>>> {
        return currencyRatesRepository
            .getLatestCurrencyRates()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.rates?.toList() }
    }
}