package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EndingDialogActivity : AppCompatActivity() {

    private var currentQuestionIndex = 0
    private val questions = listOf(
        "So, did you enjoy your short adventure in IT?",
        "Do you want to learn more?",
        "Do you want to commit to an IT program?"
    )
    private val userResponses = mutableListOf<String>()

    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup

    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endingdialog)

        mAuth = FirebaseAuth.getInstance()

        questionTextView = findViewById(R.id.assessment_question_text)
        radioGroup = findViewById(R.id.assessment_radio_group)
        findViewById<Button>(R.id.submit_assessment_button).setOnClickListener {
            handleSubmission()
        }

        displayCurrentQuestion()
    }

    private fun displayCurrentQuestion() {
        questionTextView.text = questions[currentQuestionIndex]
        radioGroup.clearCheck() // Clear selection for the new question
    }

    private fun handleSubmission() {
        val selectedId = radioGroup.checkedRadioButtonId
        if (selectedId == -1) {
            Toast.makeText(this, "Please make a selection", Toast.LENGTH_SHORT).show()
            return
        }

        val isYes = findViewById<RadioButton>(selectedId).text.toString().equals("YES", ignoreCase = true)
        collectResponse(isYes)

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            displayCurrentQuestion()
        } else {
            finishAssessment()
        }
    }

    private fun collectResponse(isYes: Boolean) {
        val response = when (currentQuestionIndex) {
            0 -> if (isYes) "Good! luck on your journey, " else "Good luck in finding your path."
            1 -> if (isYes) "Knowledge is power, and learning leads to change." else "Become one with yourself, and keep exploring."
            2 -> if (isYes) "Future IT" else ""
            else -> ""
        }
        if (response.isNotEmpty()) {
            userResponses.add(response)
        }
    }

    private fun finishAssessment() {
        val userId = mAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not authenticated. Cannot save assessment.", Toast.LENGTH_LONG).show()
            finish() // Can't proceed without a user
            return
        }

        // Save the assessment completion flag to Firestore.
        val assessmentRef = db.collection("users").document(userId).collection("assessmentStatus").document("Final Assessment")
        assessmentRef.set(mapOf("hasTakenAssessment" to true))
            .addOnSuccessListener { 
                // Now that the flag is saved, navigate to the final screen.
                val combinedMessage = userResponses.joinToString(" ")
                val intent = Intent(this, YesActivity::class.java).apply {
                    putExtra("USER_CHOICE", combinedMessage.trim())
                }
                startActivity(intent)
                finish() // Finish this activity so the user can't come back to it
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save assessment status: ${e.message}", Toast.LENGTH_LONG).show()
                // Still proceed to the final screen even if saving failed, so the user is not stuck.
                val combinedMessage = userResponses.joinToString(" ")
                val intent = Intent(this, YesActivity::class.java).apply {
                    putExtra("USER_CHOICE", combinedMessage.trim())
                }
                startActivity(intent)
                finish()
            }
    }
}
