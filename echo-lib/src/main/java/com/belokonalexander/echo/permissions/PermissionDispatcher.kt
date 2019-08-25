package com.belokonalexander.echo.permissions

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.belokonalexander.echo.ActivityNotFoundException
import com.belokonalexander.echo.ActivityProvider
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.*

class PermissionDispatcher(private val bus: PublishSubject<PermissionResult>, private val activityProvider: ActivityProvider) {
    fun checkPermission(context: Context, permissions: Array<String>): Single<PermissionStatus> = Single.fromCallable {
        getDeniedPermissions(context, permissions)
    }.map {
        if (it.isEmpty()) PermissionStatus.ALLOWED else PermissionStatus.DISABLED
    }

    fun requestPermissions(uuid: UUID, requestCode: Int, permissions: Array<String>): Single<PermissionStatus> {
        activityProvider.getItem<Activity>(uuid)?.let { activity ->
            return Single.fromCallable { getDeniedPermissions(activity, permissions) }.flatMap { denied ->
                when (denied.isEmpty()) {
                    true -> Single.just(PermissionStatus.ALLOWED)
                    else -> askPermissionFromUser(activity, denied, requestCode, uuid)
                }
            }
        }

        return Single.error(ActivityNotFoundException())
    }

    private fun getDeniedPermissions(context: Context, permissions: Array<String>): List<String> = permissions.filter {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_DENIED
    }

    private fun askPermissionFromUser(activity: Activity, permissions: List<String>, requestCode: Int, uuid: UUID): Single<PermissionStatus> =
       Completable.fromAction { ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestCode) }
            .andThen(waitActivityResult(requestCode, uuid))

    private fun waitActivityResult(requestCode: Int, uuid: UUID): Single<PermissionStatus> =
        bus.filter { it.uuid == uuid && it.requestCode == requestCode }
            .firstElement()
            .map { it.permissionStatus }
            .toSingle()
}