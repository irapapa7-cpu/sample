package com.example.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import androidx.core.content.edit

class NicknameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)

        val saveButton: Button = findViewById(R.id.create_nickname_popup_button)
        val closeButton: ImageView = findViewById(R.id.nickname_popup_close_button)
        val nicknameLayout: TextInputLayout = findViewById(R.id.nickname_box)

        val sharedPrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val currentNickname = sharedPrefs.getString("NICKNAME", "")
        nicknameLayout.editText?.setText(currentNickname)

        saveButton.setOnClickListener {
            // 1. Get the text and trim whitespace (so "   " counts as empty)
            val newNickname = nicknameLayout.editText?.text.toString().trim()

            // 2. CHECK: Is the nickname empty?
            if (newNickname.isEmpty()) {
                // FAIL: Show an error message on the box and stop here
                nicknameLayout.error = "Nickname is required to login"
                nicknameLayout.isErrorEnabled = true // Ensures the red message shows
            } else {
                // SUCCESS: Clear error, Save, and Proceed
                nicknameLayout.error = null
                nicknameLayout.isErrorEnabled = false

                sharedPrefs.edit {
                    putString("NICKNAME", newNickname)
                }

                val intent = Intent(this, DashboardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        closeButton.setOnClickListener {
            // OPTIONAL: If they click X, do you want to close the app?
            // If nickname is required, they shouldn't be able to just close this popup and enter.
            // For now, finishing the activity is fine if this is a popup.
            finish()
        }
    }
}