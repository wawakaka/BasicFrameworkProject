package io.github.wawakaka.basicframeworkproject.base

open class BaseContract {

    interface View

    interface Presenter<in T> {
        fun detach()
        fun attach(view: T)
    }
}