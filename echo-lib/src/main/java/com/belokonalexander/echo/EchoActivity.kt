package com.belokonalexander.echo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import java.util.*

abstract class EchoActivity : AppCompatActivity(), UniqueActivity {
    private val uniqueActivityDelegate = UniqueActivityDelegate()

    override val uuid: UUID
        get() = uniqueActivityDelegate.uuid

    override fun onCreate(savedInstanceState: Bundle?) {
        uniqueActivityDelegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        uniqueActivityDelegate.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        provideEcho().dispatchActivityResult(uuid, requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        provideEcho().dispatchPermission(uuid, requestCode, permissions, grantResults)
    }

    abstract fun provideEcho(): Echo
}