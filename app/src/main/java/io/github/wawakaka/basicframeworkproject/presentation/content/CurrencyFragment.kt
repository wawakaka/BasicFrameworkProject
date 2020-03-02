package io.github.wawakaka.basicframeworkproject.presentation.content

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.jakewharton.rxbinding3.view.clicks
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseFragment
import io.github.wawakaka.basicframeworkproject.utilities.makeInvisible
import io.github.wawakaka.basicframeworkproject.utilities.makeVisible
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_weather_list.*
import org.koin.androidx.scope.currentScope
import java.util.concurrent.TimeUnit

class CurrencyFragment : BaseFragment(), CurrencyContract.View {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val presenter: CurrencyPresenter by currentScope.inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attach(this)
        init()
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        presenter.detach()
        super.onDestroy()
    }

    override fun showLoading() {
        progress_loading.makeVisible()
    }

    override fun hideLoading() {
        progress_loading.makeInvisible()
    }

    override fun onGetDataSuccess(string: String) {
        result.text = string
        Log.d(TAG, "onGetCurrentWeather next")
    }

    override fun onGetDataFailed(throwable: Throwable) {
        Toast.makeText(activity, throwable.toString(), Toast.LENGTH_LONG).show()
        Log.e(TAG, "error onGetCurrentWeather", throwable)
    }

    private fun init() {
        initGoButton()
        initProgressbar()
    }

    private fun initGoButton() {
        button_go.clicks()
            .observeOn(Schedulers.io())
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { presenter.onButtonClickedEvent() },
                { Log.e(TAG, "initGoButton", it) }
            )
            .let(compositeDisposable::add)
    }

    private fun initProgressbar() {
        context?.let {
            progress_loading.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(
                    it,
                    R.color.colorPrimary
                ), PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    companion object {
        private val TAG = CurrencyFragment::class.java.simpleName
    }
}
