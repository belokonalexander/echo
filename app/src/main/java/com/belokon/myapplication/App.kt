package com.belokon.myapplication

import android.app.Application
import com.belokon.echo.ActivityProvider
import com.belokon.echo.DispatcherActivityCallback
import com.belokon.echo.Echo
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance

class App : Application(), KodeinAware {

    val di = DiContainer(this)

    override val kodein: Kodein
        get() = di.getAppScope()

    private val echo: Echo by kodein.instance()

    override fun onCreate() {
        super.onCreate()
        echo.bindToApplication(this)
    }
}