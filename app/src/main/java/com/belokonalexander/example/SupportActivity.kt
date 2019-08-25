package com.belokonalexander.example

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.belokonalexander.example.Presenter.Companion.RESULT_EXTRA_TAG
import kotlinx.android.synthetic.main.activity_support.*

class SupportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        button.setOnClickListener {
            setResult(RESULT_OK, Intent().apply {
                putExtra(RESULT_EXTRA_TAG, "Hello!")
            })
            finish()
        }
    }
}