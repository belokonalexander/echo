package com.belokonalexander.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.belokonalexander.echo.permissions.PermissionDispatcher
import com.belokonalexander.echo.results.ActivityResultDispatcher
import java.util.*

class Presenter(
    private val activityDispatcher: ActivityResultDispatcher,
    private val permissionDispatcher: PermissionDispatcher,
    private val activityUuid: UUID
) {
    companion object {
        const val RESULT_EXTRA_TAG = "RESULT"
    }

    @SuppressLint("CheckResult")
    fun requestPermission(context: Context) {
        permissionDispatcher.requestPermissions(activityUuid, 1, arrayOf(Manifest.permission.CAMERA))
            .subscribe { result -> showResult(context, result.toString()) }
    }

    @SuppressLint("CheckResult")
    fun goForResult(context: Context) {
        val intent = Intent()
        intent.action = "com.belokon.myapplication.TEST_ME"
        activityDispatcher.request(activityUuid, 1, intent).subscribe { result ->
            showResult(context, "" +
                "code: ${result.resultCode}, " +
                "result: ${result.data?.getStringExtra(RESULT_EXTRA_TAG)}"
            )
        }
    }

    private fun showResult(context: Context, result: String) {
        Toast.makeText(context, "Result: $result", LENGTH_LONG).show()
    }
}