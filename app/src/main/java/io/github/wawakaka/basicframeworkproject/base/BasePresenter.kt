package io.github.wawakaka.basicframeworkproject.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Base Presenter for MVP pattern
 * @deprecated Migrate to TOAD pattern with ViewModel (Milestone 6).
 * Use androidx.lifecycle.ViewModel instead. This class will be removed in Milestone 7.
 */
@Deprecated(
    message = "Migrate to TOAD pattern with ViewModel (Milestone 6)",
    replaceWith = ReplaceWith(
        expression = "ViewModel",
        imports = ["androidx.lifecycle.ViewModel"]
    ),
    level = DeprecationLevel.WARNING
)
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
