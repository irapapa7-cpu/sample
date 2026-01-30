package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Implement the listener interface from the DialogFragment
class Login : AppCompatActivity(), NicknameDialogFragment.OnNicknameSavedListener {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        // If user is already logged in, check their profile and proceed
        mAuth.currentUser?.let {
            checkNicknameAndProceed(it)
            return // Skip showing the login layout
        }

        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.input_email)
        etPassword = findViewById(R.id.input_password)

        findViewById<Button>(R.id.confirm_login).setOnClickListener {
            validateAndLogin()
        }

        findViewById<TextView>(R.id.textViewRegister).setOnClickListener {
            startActivity(Intent(this, Register::class.java))
        }
    }

    private fun validateAndLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Invalid email format"
            etEmail.requestFocus()
            return
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return
        }

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    task.result.user?.let { checkNicknameAndProceed(it) }
                } else {
                    Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun checkNicknameAndProceed(user: FirebaseUser) {
        val db = Firebase.firestore
        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists() && document.getString("nickname") != null) {
                    // Nickname exists, go to Dashboard
                    goToDashboard()
                } else {
                    // THE FIX: Nickname does not exist, show the DialogFragment pop-up.
                    val nicknameDialog = NicknameDialogFragment()
                    nicknameDialog.show(supportFragmentManager, "NicknameDialogFragment")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to check user profile: ${exception.message}", Toast.LENGTH_LONG).show()
                mAuth.signOut() // Sign out to be safe
            }
    }

    // This is the new method required by the listener interface.
    // It will be called by the DialogFragment when the nickname is successfully saved.
    override fun onNicknameSaved() {
        goToDashboard()
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
