package com.belokon.myapplication

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.FrameLayout
import com.belokon.echo.Echo
import com.belokon.echo.EchoActivity
import com.belokon.echo.UniqueActivity
import com.belokon.echo.UniqueActivityDelegate
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.erased.instance
import java.util.*

class MainActivity : EchoActivity(), KodeinAware {
    private val di get() = (applicationContext as App).di
    override val kodein: Kodein get() = di.getActivityScope(this)

    private lateinit var presenter: Presenter

    override val echo: Echo
        get() = kodein.direct.instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = kodein.direct.instance(arg = uuid)
        val contentView = LayoutInflater.from(this).inflate(R.layout.activity_main, null)
        setContentView(contentView)

        contentView.setOnClickListener { foo() }

    }

    fun foo() {
        presenter.permission()
    }
}
