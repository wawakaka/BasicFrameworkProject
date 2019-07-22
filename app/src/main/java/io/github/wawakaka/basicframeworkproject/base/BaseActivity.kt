package io.github.wawakaka.basicframeworkproject.base

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    protected val rxPermissions: RxPermissions
        get() = RxPermissions(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rxPermissions.setLogging(true)
    }
}