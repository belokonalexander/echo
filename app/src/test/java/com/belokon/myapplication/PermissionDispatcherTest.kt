package com.belokon.echo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import com.belokon.echo.permissions.PermissionDispatcher
import com.belokon.echo.permissions.PermissionStatus
import com.belokon.myapplication.shadows.ShadowContextWithPermissions
import com.belokon.myapplication.shadows.ShadowContextWithPermissions.Companion.ALLOWED_PERMISSION
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
@Config(application = TestApp::class, shadows = [ShadowContextWithPermissions::class])
class PermissionDispatcherTest {
    lateinit var activity: TestEchoActivity
    lateinit var uuid: UUID
    lateinit var permissionDispatcher: PermissionDispatcher

    private val allowed = PackageManager.PERMISSION_GRANTED
    private val disabled = PackageManager.PERMISSION_DENIED

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
    fun `permission dispatcher returns ALLOWED when TWO permissions was REQUESTED and ALL GRANTED`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed, allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.ALLOWED)
    }

    @Test
    fun `permission dispatcher returns DISABLED when TWO permissions was REQUESTED and NOT ALL GRANTED`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed, disabled))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.DISABLED)
    }

    @Test
    fun `permission dispatcher returns DISABLED when TWO permissions was REQUESTED and NOT ALL IN GRANTED array`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.DISABLED)
    }

    @Test
    fun `permission dispatcher returns ALLOWED when TWO permissions was REQUESTED and ONE WERE GRANTED EARLY and OTHER GRANTED NOW`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(ALLOWED_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE)
        val reallyRequestedPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, reallyRequestedPermissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.ALLOWED)
    }

    @Test
    fun `permission dispatcher returns DISABLED when TWO permissions was REQUESTED and ONE WERE GRANTED EARLY and OTHER DISABLED NOW`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(ALLOWED_PERMISSION, Manifest.permission.READ_EXTERNAL_STORAGE)
        val reallyRequestedPermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, reallyRequestedPermissions, intArrayOf(disabled))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.DISABLED)
    }

    @Test
    fun `permission dispatcher THROW ERROR for UNKNOWN UUID`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(UUID.randomUUID(), 1, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(disabled))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertError(ActivityNotFoundException::class.java)
    }

    @Test
    fun `permission dispatcher IGNORES events for other REQUEST CODE`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 2, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertNotTerminated()
    }

    @Test
    fun `permission dispatcher THROW ERROR for UNKNOWN UUID and other REQUEST CODE`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(UUID.randomUUID(), 2, permissions).subscribe(testObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertError(ActivityNotFoundException::class.java)
    }

    @Test
    fun `TWO permission dispatcher CLIENTS react only for THEIR unique UUID`() {
        val otherActivity = Robolectric.setupActivity(TestEchoActivity::class.java)
        val otherUuid = otherActivity.uuid
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val otherTestObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        otherActivity.provideEcho().permissionDispatcher.requestPermissions(otherUuid, 1, permissions).subscribe(otherTestObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        otherTestObserver.await(100, TimeUnit.MILLISECONDS)

        testObserver.assertValue(PermissionStatus.ALLOWED)
        otherTestObserver.assertNotTerminated()
    }

    @Test
    fun `TWO permission dispatcher CLIENTS react only for THEIR request codes`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val otherTestObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        permissionDispatcher.requestPermissions(uuid, 2, permissions).subscribe(otherTestObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        otherTestObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.ALLOWED)
        otherTestObserver.assertNotTerminated()
    }

    @Test
    fun `TWO permission dispatcher CLIENTS will get ONE EVENT for same REQUESTS`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val otherTestObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(otherTestObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        otherTestObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.ALLOWED)
        otherTestObserver.assertValue(PermissionStatus.ALLOWED)
    }

    @Test
    fun `TWO permission dispatcher CLIENTS will get ONE EVENT for equal REQUESTS and will ignore NEXT EVENTS`() {
        val testObserver = TestEnv.getTestObserver<PermissionStatus>()
        val otherTestObserver = TestEnv.getTestObserver<PermissionStatus>()
        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(testObserver)
        permissionDispatcher.requestPermissions(uuid, 1, permissions).subscribe(otherTestObserver)
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(disabled))
        activity.onRequestPermissionsResult(1, permissions, intArrayOf(allowed))

        testObserver.await(100, TimeUnit.MILLISECONDS)
        otherTestObserver.await(100, TimeUnit.MILLISECONDS)
        testObserver.assertValue(PermissionStatus.DISABLED)
        otherTestObserver.assertValue(PermissionStatus.DISABLED)
    }
}