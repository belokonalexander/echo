package com.belokon.myapplication.shadows

import android.Manifest
import org.robolectric.annotation.Implements
import org.robolectric.shadows.ShadowContextImpl

@Implements(className = ShadowContextImpl.CLASS_NAME)
class ShadowContextWithPermissions : ShadowContextImpl() {
    private val allowedPermissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION)
    override fun checkPermission(permission: String?, pid: Int, uid: Int): Int {
        return if (permission in allowedPermissions) 0 else super.checkPermission(permission, pid, uid)
    }
}