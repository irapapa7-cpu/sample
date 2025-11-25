package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sample.data.IntroToITQuiz
import com.example.sample.models.Question

class QuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var option1RadioButton: RadioButton
    private lateinit var option2RadioButton: RadioButton
    private lateinit var option3RadioButton: RadioButton
    private lateinit var backButton: Button
    private lateinit var nextButton: Button

    private var currentQuestionIndex = 0
    private val questions = IntroToITQuiz.questions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val skillName = intent.getStringExtra("SKILL_NAME")
        val level = intent.getIntExtra("LEVEL", 1)
        title = "$skillName - Level $level"

        questionTextView = findViewById(R.id.question_text)
        optionsRadioGroup = findViewById(R.id.options_radio_group)
        option1RadioButton = findViewById(R.id.option1_radio_button)
        option2RadioButton = findViewById(R.id.option2_radio_button)
        option3RadioButton = findViewById(R.id.option3_radio_button)
        backButton = findViewById(R.id.back_button)
        nextButton = findViewById(R.id.next_button)

        displayQuestion(questions[currentQuestionIndex])

        backButton.setOnClickListener { showPreviousQuestion() }
        nextButton.setOnClickListener { showNextQuestion() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun displayQuestion(question: Question) {
        questionTextView.text = question.text
        option1RadioButton.text = question.options[0]
        option2RadioButton.text = question.options[1]
        option3RadioButton.text = question.options[2]
        optionsRadioGroup.clearCheck()
    }

    private fun showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
            displayQuestion(questions[currentQuestionIndex])
        } else {
            Toast.makeText(this, "This is the first question.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showNextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            displayQuestion(questions[currentQuestionIndex])
        } else {
            Toast.makeText(this, "This is the last question.", Toast.LENGTH_SHORT).show()
        }
    }
}
