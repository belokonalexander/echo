package com.belokon.echo

import android.app.Activity
import android.app.Application
import android.os.Bundle

class DispatcherActivityCallback(private val activityProvider: ActivityProvider) : Application.ActivityLifecycleCallbacks {
    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        activityProvider.unbind(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityProvider.bind(activity)
    }
}