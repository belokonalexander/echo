package com.belokon.echo

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import com.belokon.echo.permissions.PermissionDispatcher
import com.belokon.echo.permissions.PermissionResult
import com.belokon.echo.permissions.PermissionStatus
import com.belokon.echo.results.ActivityResult
import com.belokon.echo.results.ActivityResultDispatcher
import io.reactivex.subjects.PublishSubject
import java.lang.RuntimeException
import java.util.*

class Echo(
    private val resultBus: PublishSubject<ActivityResult> = PublishSubject.create<ActivityResult>(),
    private val permissionBus: PublishSubject<PermissionResult> = PublishSubject.create<PermissionResult>(),
    private val activityProvider: ActivityProvider = ActivityProvider()
) {
    val permissionDispatcher = PermissionDispatcher(permissionBus, activityProvider)
    val activityResultDispatcher = ActivityResultDispatcher(resultBus, activityProvider)

    fun bindToApplication(application: Application) {
        application.registerActivityLifecycleCallbacks(DispatcherActivityCallback(activityProvider))
    }

    fun dispatchPermission(uuid: UUID, requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        permissionBus.let { bus ->
            if (grantResults.isNotEmpty()) {
                val status = when (permissions.size == grantResults.size && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    true -> PermissionStatus.ALLOWED
                    else -> PermissionStatus.DISABLED
                }
                bus.onNext(PermissionResult(uuid, requestCode, status))
            }
        }
    }

    fun dispatchActivityResult(uuid: UUID, requestCode: Int, resultCode: Int, data: Intent?) {
        resultBus.onNext(ActivityResult(uuid, requestCode, resultCode, data))
    }
}