package com.example.pinabug

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        findViewById<Button>(R.id.backBtn).setOnClickListener {
            finish() // closes this activity and returns to MainActivity
        }

    }
}