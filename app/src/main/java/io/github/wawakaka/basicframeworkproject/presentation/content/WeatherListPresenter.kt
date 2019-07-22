package io.github.wawakaka.basicframeworkproject.presentation.content

import android.util.Log
import com.jakewharton.rxbinding3.InitialValueObservable
import com.jakewharton.rxbinding3.widget.TextViewAfterTextChangeEvent
import io.github.wawakaka.basicframeworkproject.data.repositories.openweathermap.OpenWeatherRepository
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
            .filter { it.editable?.isBlank() != true }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.editable?.isBlank() != true) view.enableButton()
                else view.disableButton()
            }, {
                Log.e("onApiTextChangesEvent", "error", it)
            }).let(compositeDisposable::add)
    }

    override fun onButtonClickedEvent(event: Observable<Unit>) {
        event
            .observeOn(AndroidSchedulers.mainThread())
            .map { view.getApiKey() }
            .observeOn(Schedulers.io())
            .flatMap { openWeatherRepository.getCurrentWeatherObservable(it) }
            .map { it }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { view.onGetCurrentWeatherSuccess(it) },
                { view.onGetCurrentWeatherError(it) }
            ).let(compositeDisposable::add)
    }

    override fun detach() {
        compositeDisposable.clear()
    }

    override fun attach(view: WeatherListContract.View) {
        this.view = view
    }
}