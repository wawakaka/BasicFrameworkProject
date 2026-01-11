package io.github.wawakaka.basicframeworkproject.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BasePresenter<V : BaseContract.View> : BaseContract.Presenter<V> {
    protected var view: V? = null

    // Coroutine scope tied to presenter lifecycle
    protected val presenterScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun attach(view: V) {
        this.view = view
    }

    override fun detach() {
        presenterScope.cancel() // Cancel all ongoing coroutines
        this.view = null
    }
}
