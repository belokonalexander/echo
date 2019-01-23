package com.belokon.echo

class TestEchoActivity : EchoActivity() {
    override fun provideEcho(): Echo {
        return (applicationContext as TestApp).echo
    }
}