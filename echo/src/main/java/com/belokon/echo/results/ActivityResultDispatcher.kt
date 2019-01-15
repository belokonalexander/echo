package com.belokon.echo.results

import android.app.Activity
import android.content.Intent
import com.belokon.echo.ActivityNotFoundException
import com.belokon.echo.ActivityProvider
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import java.util.*

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