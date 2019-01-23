package com.belokon.echo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.belokon.echo.permissions.PermissionDispatcher
import com.belokon.echo.permissions.PermissionStatus
import com.belokon.myapplication.shadows.ShadowContextWithPermissions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowContextImpl
import java.util.*
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(application = TestApp::class, shadows = arrayOf(ShadowContextWithPermissions::class))
class CoreTests {
    lateinit var activity: TestEchoActivity
    lateinit var uuid: UUID
    lateinit var permissionDispatcher: PermissionDispatcher

    val allowed = PackageManager.PERMISSION_GRANTED
    val disabled = PackageManager.PERMISSION_DENIED

    @Before
    fun beforeEach() {
        activity = Robolectric.setupActivity(TestEchoActivity::class.java)
        uuid = activity.uuid
        permissionDispatcher = activity.provideEcho().permissionDispatcher
    }

    @Test
    fun `permission dispatcher returns ALLOWED when ONE permission is GRANTED`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.CAMERA)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.ALLOWED)
    }

    @Test
    fun `permission dispatcher returns ALLOWED when MANY permissions was REQUESTED and ALL GRANTED`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed, allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.ALLOWED)
    }

    @Test
    fun `permission dispatcher returns DISABLED when MANY permissions was REQUESTED and NOT ALL GRANTED`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed, disabled))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.DISABLED)
    }

    @Test
    fun `permission dispatcher returns DISABLED when MANY permissions was REQUESTED and NOT ALL IN GRANTED array`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.DISABLED)
    }

    @Test
    fun `permission dispatcher returns ALLOWED when MANY permissions was REQUESTED and SOME WERE GRANTED EARLY and OTHER GRANTED NOW`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
        val reallyRequestedPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, reallyRequestedPermissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.ALLOWED)
    }

    @Test
    fun `permission dispatcher returns DISABLED when MANY permissions was REQUESTED and SOME WERE GRANTED EARLY and OTHER DISABLED NOW`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
        val reallyRequestedPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, reallyRequestedPermissions, intArrayOf(disabled))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.DISABLED)
    }
}