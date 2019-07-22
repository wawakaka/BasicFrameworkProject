package io.github.wawakaka.basicframeworkproject.base

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    protected lateinit var activityCallbacks: FragmentActivityCallbacks

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentActivityCallbacks) {
            activityCallbacks = context
        } else {
            throw RuntimeException("$context must implement FragmentActivityCallbacks")
        }
    }
}