package com.belokon.echo

import android.app.Activity
import com.belokon.echo.permissions.PermissionResult
import com.belokon.echo.permissions.PermissionStatus
import com.belokon.echo.results.ActivityResult
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
class CoreTests {
    private val resultBus = PublishSubject.create<ActivityResult>()
    private val permissionBus = PublishSubject.create<PermissionResult>()



    @Test
    fun test() {
        val activityProvider = ActivityProvider()
        val echo = Echo(resultBus = resultBus, permissionBus = permissionBus, activityProvider = activityProvider)

        val activity = Robolectric.setupActivity(EchoActivity::class.java)
        activityProvider.bind(activity)
        val uuid = activity.uuid


        val permissionDispatcher = echo.permissionDispatcher
        val testObserver = TestObserver.create<PermissionStatus>(TestEnv.onNextLogDelegate)
        permissionDispatcher.requestPermissions(uuid, 1, arrayOf()).subscribe(testObserver)
        testObserver.await(1000, TimeUnit.MILLISECONDS)
        testObserver.assertComplete()
    }
}