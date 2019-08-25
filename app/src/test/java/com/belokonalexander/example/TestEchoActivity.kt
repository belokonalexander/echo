package com.belokonalexander.example

import com.belokonalexander.echo.Echo
import com.belokonalexander.echo.EchoActivity

class TestEchoActivity : EchoActivity() {
    override fun provideEcho(): Echo {
        return (applicationContext as TestApp).echo
    }
}