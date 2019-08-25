package com.belokonalexander.example

import android.app.Application
import com.belokonalexander.echo.Echo

class TestApp : Application() {
    val echo = Echo()
    override fun onCreate() {
        super.onCreate()
        echo.bindToApplication(this)
    }
}