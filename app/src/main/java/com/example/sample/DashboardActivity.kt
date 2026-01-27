package com.example.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import androidx.core.content.edit

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val beginGameCard: MaterialCardView = findViewById(R.id.BeginGame)
        val logoutCard: MaterialCardView = findViewById(R.id.logout_card)

        beginGameCard.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        logoutCard.setOnClickListener {
            // Clear the saved user profile data
            val sharedPrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            sharedPrefs.edit { clear() }

            // Navigate to Login screen and clear back stack
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Load and display the saved nickname
        val usernameTextView: TextView = findViewById(R.id.username)
        val sharedPrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val nickname = sharedPrefs.getString("NICKNAME", "Username") // Default to "Username"
        usernameTextView.text = nickname
    }
}
