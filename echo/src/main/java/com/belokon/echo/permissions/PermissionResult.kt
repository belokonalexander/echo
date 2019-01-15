package com.belokon.echo.permissions

import java.util.*

data class PermissionResult(
    val uuid: UUID,
    val requestCode: Int,
    val permissionStatus: PermissionStatus
)