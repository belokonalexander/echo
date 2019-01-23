package com.belokon.myapplication.shadows

import android.Manifest
import android.content.pm.PackageManager
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowContextImpl

@Implements(className = ShadowContextImpl.CLASS_NAME)
class ShadowContextWithPermissions : ShadowContextImpl() {

    companion object {
        const val ALLOWED_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
    }

    private val allowedPermissions = listOf(ALLOWED_PERMISSION)
    override fun checkPermission(permission: String?, pid: Int, uid: Int): Int {
        return if (permission in allowedPermissions) PackageManager.PERMISSION_GRANTED else super.checkPermission(permission, pid, uid)
    }
}