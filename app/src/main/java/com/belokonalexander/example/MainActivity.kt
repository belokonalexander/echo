package com.belokonalexander.example

import android.os.Bundle
import com.belokonalexander.echo.Echo
import com.belokonalexander.echo.EchoActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : EchoActivity() {
    private lateinit var presenter: Presenter

    override fun provideEcho(): Echo {
        return (applicationContext as App).echo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter = (applicationContext as App).getPresenter(uuid)
        cameraPermissionButton.setOnClickListener {
            presenter.requestPermission(this)
        }
        activityResultButton.setOnClickListener {
            presenter.goForResult(this)
        }
    }
}
