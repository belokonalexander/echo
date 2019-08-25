package com.belokonalexander.echo

import android.os.Bundle
import java.util.*

class UniqueActivityDelegate {
    companion object {
        const val UUID_KEY = "UNIQUE_ACTIVITY_UID"
    }

    lateinit var uuid: UUID
        private set

    private fun generateUUID(): UUID = UUID.randomUUID()

    fun onCreate(savedInstanceState: Bundle?) {
        uuid = savedInstanceState?.getString(UUID_KEY)?.let { UUID.fromString(it) } ?: generateUUID()
    }

    fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(UUID_KEY, uuid.toString())
    }
}