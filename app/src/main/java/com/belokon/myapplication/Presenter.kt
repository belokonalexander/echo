package com.belokon.myapplication

import android.Manifest
import android.content.Intent
import android.util.Log
import com.belokon.echo.permissions.PermissionDispatcher
import com.belokon.echo.results.ActivityResultDispatcher
import java.util.*

class Presenter(private val dispatcher: ActivityResultDispatcher, private val permissionDispatcher: PermissionDispatcher, private val activityUuid: UUID) {
    fun camera() {
        val intent = Intent("com.belokon.myapplication.TEST_ME")
        dispatcher.request(activityUuid, 1, intent).subscribe({ result ->
            Log.d("#1020", "data -> ${result.data}")
        }, { error -> Log.d("#1020", "error -> ${error}")})
    }

    fun permission() {
        permissionDispatcher.requestPermissions(activityUuid, 2, arrayOf(Manifest.permission.CAMERA))
            .subscribe { it ->
                Log.d("#1020", "PERMISSION STATUS = $it")
            }
    }
}