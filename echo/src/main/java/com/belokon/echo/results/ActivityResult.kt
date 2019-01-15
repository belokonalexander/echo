package com.belokon.echo.results

import android.content.Intent
import java.util.*

data class ActivityResult(
    val uuid: UUID,
    val requestCode: Int,
    val resultCode: Int,
    val data: Intent?
)