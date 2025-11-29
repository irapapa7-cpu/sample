package com.example.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sample.data.BasicHardwareQuiz
import com.example.sample.data.CssBasicsQuiz
import com.example.sample.data.HtmlBasicsQuiz
import com.example.sample.data.IntroToITQuiz
import com.example.sample.data.IntroductionToProgrammingQuiz
import com.example.sample.data.OperatingSystemsQuiz
import com.example.sample.models.Question

class QuizActivity : AppCompatActivity() {

    private lateinit var questionNumberTextView: TextView
    private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var backButton: Button
    private lateinit var homeButton: Button
    private lateinit var nextButton: Button
    private lateinit var questionImageView: ImageView
    private lateinit var saveButton: ImageButton

    private lateinit var questions: List<Question>
    private var isAnswered = false

    private lateinit var skillName: String
    private var level: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        skillName = intent.getStringExtra("SKILL_NAME") ?: ""
        level = intent.getIntExtra("LEVEL", 1)

        val allQuestions = getQuestionsForSkill(skillName)
        questions = if (level > 0 && level <= allQuestions.size) {
            listOf(allQuestions[level - 1])
        } else {
            emptyList()
        }

        questionNumberTextView = findViewById(R.id.question_number_text)
        questionTextView = findViewById(R.id.question_text)
        optionsRadioGroup = findViewById(R.id.options_radio_group)
        backButton = findViewById(R.id.back_button)
        homeButton = findViewById(R.id.home_button)
        nextButton = findViewById(R.id.next_button)
        questionImageView = findViewById(R.id.question_image)
        saveButton = findViewById(R.id.imageButton)

        if (questions.isNotEmpty()) {
            displayQuestion(questions.first())
        } else {
            Toast.makeText(this, "No questions available for this skill yet.", Toast.LENGTH_LONG).show()
            finish()
        }

        backButton.setOnClickListener { moveToPreviousQuestion() }
        homeButton.setOnClickListener { goToSkillActivity() } // Changed to go to SkillActivity
        nextButton.setOnClickListener { moveToNextQuestion() }
        saveButton.setOnClickListener { /* Save logic can be added here if needed */ }
    }

    private fun getQuestionsForSkill(skillName: String): List<Question> {
        return when (skillName) {
            "Introduction to IT" -> IntroToITQuiz.questions
            "Basic Computer Hardware" -> BasicHardwareQuiz.questions
            "Operating Systems" -> OperatingSystemsQuiz.questions
            "Introduction to Programming" -> IntroductionToProgrammingQuiz.questions
            "HTML Basics" -> HtmlBasicsQuiz.questions
            "CSS Basics" -> CssBasicsQuiz.questions
            else -> emptyList()
        }
    }

    private fun displayQuestion(question: Question) {
        questionNumberTextView.text = "$level/10"
        questionTextView.text = question.text

        if (question.imageResId != null) {
            questionImageView.visibility = View.VISIBLE
            questionImageView.setImageResource(question.imageResId)
        } else {
            questionImageView.visibility = View.GONE
        }

        optionsRadioGroup.removeAllViews()
        question.options.forEachIndexed { index, option ->
            val radioButton = RadioButton(this)
            radioButton.text = option
            radioButton.id = index
            radioButton.textSize = 18f
            optionsRadioGroup.addView(radioButton)
            radioButton.setOnClickListener { handleAnswer(index) }
        }
    }

    private fun handleAnswer(selectedIndex: Int) {
        if (isAnswered) return
        isAnswered = true

        val correct = selectedIndex == questions.first().correctAnswerIndex
        
        // Save the result immediately to SharedPreferences
        saveLevelResult(correct)

        // Set the result for SkillActivity to receive
        val resultIntent = Intent()
        resultIntent.putExtra("level", level)
        resultIntent.putExtra("passed", correct)
        setResult(Activity.RESULT_OK, resultIntent)

        if (correct) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()
        }

        setOptionsEnabled(false)

        optionsRadioGroup.postDelayed({ moveToNextQuestion() }, 1200)
    }

    private fun saveLevelResult(passed: Boolean) {
        val sharedPref = getSharedPreferences("LevelStatus", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            val key = "${skillName}_level_${level}_passed"
            putBoolean(key, passed)
            apply()
        }
    }

    private fun moveToNextQuestion() {
        if (level < 10) {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("SKILL_NAME", skillName)
            intent.putExtra("LEVEL", level + 1)
            startActivity(intent)
        }
        finish()
    }

    private fun moveToPreviousQuestion() {
        if (level > 1) {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("SKILL_NAME", skillName)
            intent.putExtra("LEVEL", level - 1)
            startActivity(intent)
        }
        finish()
    }

    private fun setOptionsEnabled(enabled: Boolean) {
        for (i in 0 until optionsRadioGroup.childCount) {
            optionsRadioGroup.getChildAt(i).isEnabled = enabled
        }
    }

    private fun goToSkillActivity() {
        val intent = Intent(this, SkillActivity::class.java)
        intent.putExtra("SKILL_NAME", skillName)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }
}
