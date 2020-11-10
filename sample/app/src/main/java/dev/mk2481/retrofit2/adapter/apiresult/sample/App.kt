package dev.mk2481.retrofit2.adapter.apiresult.sample

import android.app.Application
import android.content.Context
import dev.mk2481.retrofit2.adapter.apiresult.sample.di.Modules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(Modules.appModule)
        }

    }

    companion object {
        fun of(context: Context) = context as App
    }
}