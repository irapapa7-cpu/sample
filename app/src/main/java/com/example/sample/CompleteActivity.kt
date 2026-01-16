package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class CompleteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete)

        val skillName = intent.getStringExtra("SKILL_NAME")
        val score = intent.getIntExtra("SCORE", 0)
        val percentage = intent.getIntExtra("PERCENTAGE", 0)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, ScoreActivity::class.java).apply {
                putExtra("SKILL_NAME", skillName)
                putExtra("SCORE", score)
                putExtra("PERCENTAGE", percentage)
            }
            startActivity(intent)
            finish()
        }, 2000) // Keep the 2-second delay
    }
}
