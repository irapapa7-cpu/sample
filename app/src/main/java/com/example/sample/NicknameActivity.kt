package com.example.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NicknameActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)

        mAuth = FirebaseAuth.getInstance()

        val saveButton: Button = findViewById(R.id.create_nickname_popup_button)
        val closeButton: ImageView = findViewById(R.id.nickname_popup_close_button)
        val nicknameLayout: TextInputLayout = findViewById(R.id.nickname_box)

        saveButton.setOnClickListener {
            val nickname = nicknameLayout.editText?.text.toString().trim()
            if (nickname.isNotEmpty()) {
                saveNicknameToFirestore(nickname)
            } else {
                nicknameLayout.error = "Nickname is required"
            }
        }

        closeButton.setOnClickListener {
            // As the user has no nickname, logging out is a safe default action.
            mAuth.signOut()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun saveNicknameToFirestore(nickname: String) {
        val user = mAuth.currentUser
        if (user == null) {
            // This should not happen if the user is coming from Login/Register
            // but as a safeguard, send them back to the login screen.
            Toast.makeText(this, "User not authenticated. Please log in again.", Toast.LENGTH_LONG).show()
            mAuth.signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Get the user's Unique ID (UID)
        val userId = user.uid

        // Get a reference to the Firestore database
        val db = Firebase.firestore

        // Create a data object for the user's profile
        val userProfile = hashMapOf(
            "nickname" to nickname
            // You can add other user data here in the future
        )

        // Save the profile to the "users" collection with the UID as the document ID
        db.collection("users").document(userId)
            .set(userProfile)
            .addOnSuccessListener { 
                // Now that the nickname is saved online, go to the dashboard
                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving nickname: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
