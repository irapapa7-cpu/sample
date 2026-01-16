package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EndingDialogActivity : AppCompatActivity() {

    private val questions = listOf(
        "So, did you enjoy your short adventure in IT?",
        "Do you want to learn more?",
        "Do you want to commit to an IT program?"
    )

    private var currentQuestionIndex = 0
    private val userAnswers = mutableMapOf<Int, Boolean>()

    private lateinit var questionTextView: TextView
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_endingdialog)

        questionTextView = findViewById(R.id.assessment_question_text)
        radioGroup = findViewById(R.id.assessment_radio_group)
        val submitButton = findViewById<Button>(R.id.submit_assessment_button)

        displayCurrentQuestion()

        submitButton.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an option.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userAnswers[currentQuestionIndex] = (selectedId == R.id.yes_radio_button)

            currentQuestionIndex++

            if (currentQuestionIndex < questions.size) {
                displayCurrentQuestion()
            } else {
                buildFinalMessageAndFinish()
            }
        }
    }

    private fun displayCurrentQuestion() {
        questionTextView.text = questions[currentQuestionIndex]
        radioGroup.clearCheck()
    }

    private fun buildFinalMessageAndFinish() {
        val finalMessage = StringBuilder()

        // Question 1
        if (userAnswers[0] == true) {
            finalMessage.append("Good! luck on your journey, ")
        } else {
            finalMessage.append("Good luck in finding your path. ")
        }

        // Question 2
        if (userAnswers[1] == true) {
            finalMessage.append("Knowledge is power, and learning leads to change. ")
        } else {
            finalMessage.append("Become one with yourself, and keep exploring. ")
        }

        // Question 3
        if (userAnswers[2] == true) {
            finalMessage.append("Future IT.")
        }

        val intent = Intent(this, YesActivity::class.java).apply {
            putExtra("USER_CHOICE", finalMessage.toString().trim())
        }
        startActivity(intent)
        finish()
    }
}
