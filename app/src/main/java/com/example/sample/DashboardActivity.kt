package com.example.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        mAuth = FirebaseAuth.getInstance()
        usernameTextView = findViewById(R.id.username)

        setupUI()
    }

    override fun onResume() {
        super.onResume()
        loadUserNickname()
    }

    private fun setupUI() {
        findViewById<MaterialCardView>(R.id.BeginGame).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<MaterialCardView>(R.id.logout_card).setOnClickListener {
            mAuth.signOut()
            // No need to clear local prefs anymore as we load from Firestore
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        findViewById<CardView>(R.id.view_summary).setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        findViewById<CardView>(R.id.view_leaderboard).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }
    }

    private fun loadUserNickname() {
        val user = mAuth.currentUser
        if (user == null) {
            // If user is not logged in, redirect to Login screen
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        val db = Firebase.firestore
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nickname = document.getString("nickname")
                    usernameTextView.text = nickname ?: "User"
                } else {
                    // This case is unlikely if the login flow is correct, but handle it.
                    usernameTextView.text = "User"
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                usernameTextView.text = "User" // Default text on failure
            }
    }
}
