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

class Echo {
    companion object {
        private var isInitialized = false
        private var resultBus: PublishSubject<ActivityResult>? = null
        private var permissionBus: PublishSubject<PermissionResult>? = null
        private var permissionDispatcher: PermissionDispatcher? = null
        private var activityResultDispatcher: ActivityResultDispatcher? = null
        private val notInitializedException = RuntimeException("Seems like Echo was not initialized")

        fun dispatchPermission(uuid: UUID, requestCode: Int, grantResults: IntArray) {
            permissionBus?.let { bus ->
                if (grantResults.isNotEmpty()) {
                    val status = when (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                        true -> PermissionStatus.ALLOWED
                        else -> PermissionStatus.DISABLED
                    }
                    bus.onNext(PermissionResult(uuid, requestCode, status))
                }
            }
        }

        fun dispatchActivityResult(uuid: UUID, requestCode: Int, resultCode: Int, data: Intent?) {
            resultBus?.onNext(ActivityResult(uuid, requestCode, resultCode, data))
        }

        fun getPermissionDispatcher(): PermissionDispatcher {
            return permissionDispatcher ?: throw notInitializedException
        }

        fun getActivityResultDispatcher(): ActivityResultDispatcher {
            return activityResultDispatcher ?: throw notInitializedException
        }

        @Synchronized
        fun initialize(
            application: Application? = null,
            resultBus: PublishSubject<ActivityResult> = PublishSubject.create<ActivityResult>(),
            permissionBus: PublishSubject<PermissionResult> = PublishSubject.create<PermissionResult>(),
            activityProvider: ActivityProvider = ActivityProvider()
        ) {
            if (isInitialized) return
            isInitialized = true
            application?.registerActivityLifecycleCallbacks(DispatcherActivityCallback(activityProvider))
            this.resultBus = resultBus
            this.permissionBus = permissionBus
            activityResultDispatcher = ActivityResultDispatcher(resultBus, activityProvider)
            permissionDispatcher = PermissionDispatcher(permissionBus, activityProvider)

        }
    }
}