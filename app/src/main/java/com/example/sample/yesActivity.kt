package com.example.sample

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class YesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yes)

        val finalMessageTextView = findViewById<TextView>(R.id.final_message_text)
        val rootLayout = findViewById<ConstraintLayout>(R.id.root_layout)

        // *** CRITICAL FIX: Get the combined message from the Intent and display it ***
        val combinedMessage = intent.getStringExtra("USER_CHOICE")
        finalMessageTextView.text = combinedMessage

        rootLayout.setOnClickListener {
            finish()
        }
    }
}
