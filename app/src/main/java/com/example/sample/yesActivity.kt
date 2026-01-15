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
        val userChoice = intent.getStringExtra("USER_CHOICE")

        val message = if (userChoice == "Yes") {
            "Good! luck on your journey Future IT"
        } else {
            "Good luck in finding your path."
        }

        finalMessageTextView.text = message

        // *** CRITICAL FIX: Make the screen exitable on tap ***
        rootLayout.setOnClickListener {
            finish()
        }
    }
}
