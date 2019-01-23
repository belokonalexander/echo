package com.belokon.echo

import android.app.Application

class TestApp : Application() {
    val echo = Echo()
    override fun onCreate() {
        super.onCreate()
        echo.bindToApplication(this)
    }
}