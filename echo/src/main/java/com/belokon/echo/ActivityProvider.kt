package com.belokon.echo

import android.app.Activity
import java.util.*

class ActivityProvider() {
    private val activities = mutableMapOf<UUID, UniqueActivity>()

    fun <T> getItem(uuid: UUID): T? = activities[uuid] as? T

    fun bind(activity: Activity) {
        if (activity is UniqueActivity) {
            activities[activity.uuid] = activity
        }
    }

    fun unbind(activity: Activity) {
        if (activity is UniqueActivity) {
            activities.remove(activity.uuid)
        }
    }
}