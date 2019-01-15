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
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.*
import java.util.concurrent.TimeUnit


class CoreTests {
    private val resultBus = PublishSubject.create<ActivityResult>()
    private val permissionBus = PublishSubject.create<PermissionResult>()
    private val testUid = TestEnv.testUid
    private val activityMock: Activity = TestEnv.activityMock
    private val activityProvider = ActivityProvider().apply {
        bind(activityMock)
    }


    @Test
    fun test() {
        val echo = Echo(resultBus = resultBus, permissionBus = permissionBus, activityProvider = activityProvider)
        val permissionDispatcher = echo.permissionDispatcher
        val testObserver = TestObserver.create<PermissionStatus>(TestEnv.onNextLogDelegate)

        permissionDispatcher.requestPermissions(testUid, 1, arrayOf()).subscribe(testObserver)

        testObserver.await(1000, TimeUnit.MILLISECONDS)
        testObserver.assertComplete()
    }
}