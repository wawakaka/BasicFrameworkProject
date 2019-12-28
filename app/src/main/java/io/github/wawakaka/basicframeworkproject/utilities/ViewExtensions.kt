package io.github.wawakaka.basicframeworkproject.utilities

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

fun View.makeVisible() {
    this.visibility = View.VISIBLE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.makeGone() {
    this.visibility = View.GONE
}

fun View.hideKeyboard(context: Context) {
    val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}