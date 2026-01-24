package io.github.wawakaka.basicframeworkproject

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.multidex.MultiDexApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
        initNotificationChannel()
    }

    private fun initKoin() {
        startKoin {
            // enable log for debug build
            if (BuildConfig.DEBUG) androidLogger(level = Level.DEBUG)
            // declare used Android context
            androidContext(this@App)
            // declare modules
            modules(applicationModules)
        }
    }

    private fun initNotificationChannel() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel(
                "default",
                "default",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }
}