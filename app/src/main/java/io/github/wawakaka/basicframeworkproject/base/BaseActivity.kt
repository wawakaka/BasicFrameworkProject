package io.github.wawakaka.basicframeworkproject.base

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions

/**
 * Created by wawakaka on 17/07/18.
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected val rxPermissions: RxPermissions
        get() = RxPermissions(this)
}