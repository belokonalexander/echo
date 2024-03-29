package com.belokonalexander.echo.results

import android.app.Activity
import android.content.Intent
import com.belokonalexander.echo.ActivityNotFoundException
import com.belokonalexander.echo.ActivityProvider
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.*

/**
 * it's correct only if current activity will not be destroyed while other activity do work (current activity's process will keep alive)
 * otherwise observer will not receive value from bus
 */
class ActivityResultDispatcher(private val bus: PublishSubject<ActivityResult>, private val activityProvider: ActivityProvider) {
    fun request(uuid: UUID, requestCode: Int, intent: Intent): Single<ActivityResult> {
        activityProvider.getItem<Activity>(uuid)?.let {
            it.startActivityForResult(intent, requestCode)
            return bus
                .filter { it.requestCode == requestCode }
                .firstElement()
                .toSingle()
        }
        return Single.error(ActivityNotFoundException())
    }
}