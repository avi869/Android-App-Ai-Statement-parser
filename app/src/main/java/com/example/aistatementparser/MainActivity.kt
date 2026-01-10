package com.example.aistatementparser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // Load fragment ONLY once
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, StatementFragment())
            }
        }
    }
}