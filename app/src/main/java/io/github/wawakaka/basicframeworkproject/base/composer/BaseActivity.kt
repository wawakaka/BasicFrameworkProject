package io.github.wawakaka.basicframeworkproject.base.composer

import android.annotation.SuppressLint
import com.tbruyelle.rxpermissions2.RxPermissions
import com.trello.navi2.NaviComponent
import com.trello.navi2.component.support.NaviAppCompatActivity

/**
 * Created by wawakaka on 17/07/18.
 */
@SuppressLint("Registered")
open class BaseActivity : NaviAppCompatActivity() {

    protected val naviComponent: NaviComponent
        get() = this

    protected val rxPermissions: RxPermissions
        get() = RxPermissions(this)
}