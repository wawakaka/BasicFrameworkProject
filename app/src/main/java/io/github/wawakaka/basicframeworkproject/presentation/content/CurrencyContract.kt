package io.github.wawakaka.basicframeworkproject.presentation.content

import io.github.wawakaka.basicframeworkproject.base.BaseContract

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