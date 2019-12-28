package io.github.wawakaka.basicframeworkproject.presentation.content

import android.util.Log
import com.jakewharton.rxbinding3.InitialValueObservable
import com.jakewharton.rxbinding3.widget.TextViewAfterTextChangeEvent
import io.github.wawakaka.openweathermap.OpenWeatherRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WeatherListPresenter(
    private val openWeatherRepository: OpenWeatherRepository,
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
) : WeatherListContract.Presenter {

    private lateinit var view: WeatherListContract.View

    override fun onApiTextChangesEvent(event: InitialValueObservable<TextViewAfterTextChangeEvent>) {
        event
            .skipInitialValue()
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { updateButtonState(it) },
                { Log.e("onApiTextChangesEvent", "error", it) }
            ).let(compositeDisposable::add)
    }

    override fun onButtonClickedEvent() {
        Observable.just(view.getApiKey())
            .doOnNext {
                Log.e("onButtonClickedEvent", "clicked")
                view.showLoading()
                view.disableButton()
            }.observeOn(Schedulers.io())
            .flatMap { openWeatherRepository.getCurrentWeatherObservable(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnTerminate {
                view.enableButton()
                view.hideLoading()
            }.subscribe(
                { view.onGetCurrentWeatherSuccess(it) },
                { view.onGetCurrentWeatherError(it) },
                { Log.e("onButtonClickedEvent", "completed") }
            ).let(compositeDisposable::add)
    }

    private fun updateButtonState(it: TextViewAfterTextChangeEvent) {
        if (it.editable?.isBlank() != true) view.enableButton()
        else view.disableButton()
    }

    override fun detach() {
        compositeDisposable.clear()
    }

    override fun attach(view: WeatherListContract.View) {
        this.view = view
    }
}