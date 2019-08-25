package com.belokonalexander.example

import android.app.Application
import com.belokonalexander.echo.Echo
import java.util.*

class App : Application() {
    lateinit var echo: Echo
    private val presenters = hashMapOf<UUID, Presenter>()

    fun getPresenter(clientUuid: UUID) = presenters[clientUuid] ?: createPresenter(clientUuid)

    private fun createPresenter(clientUuid: UUID): Presenter {
        return Presenter(echo.activityResultDispatcher, echo.permissionDispatcher, clientUuid)
            .also { presenters.put(clientUuid, it)  }
    }

    override fun onCreate() {
        super.onCreate()
        echo = Echo()
        echo.bindToApplication(this)
    }
}