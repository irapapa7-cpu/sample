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
    private lateinit var nextButton: Button
    private lateinit var questionImageView: ImageView
    private lateinit var saveButton: ImageButton

    private var currentQuestionIndex = 0
    private lateinit var questions: List<Question>
    private var isQuizComplete = false

    private lateinit var skillName: String
    private var level: Int = 1
    private lateinit var progressKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        skillName = intent.getStringExtra("SKILL_NAME") ?: ""
        level = intent.getIntExtra("LEVEL", 1)
        progressKey = "$skillName-$level"

        val allQuestions = getQuestionsForSkill(skillName)
        questions = if (level > 0 && level <= allQuestions.size) {
            listOf(allQuestions[level - 1])
        } else {
            emptyList()
        }

        questionNumberTextView = findViewById(R.id.question_number_text)
        questionTextView = findViewById(R.id.question_text)
        optionsRadioGroup = findViewById(R.id.options_radio_group)
        backButton = findViewById(R.id.home_button)
        nextButton = findViewById(R.id.submit_button)
        questionImageView = findViewById(R.id.question_image)
        saveButton = findViewById(R.id.imageButton)

        loadProgress()

        if (currentQuestionIndex >= questions.size) {
            currentQuestionIndex = 0
        }

        if (questions.isNotEmpty()) {
            displayQuestion(questions[currentQuestionIndex])
        } else {
            Toast.makeText(this, "No questions available for this skill yet.", Toast.LENGTH_LONG).show()
            finish()
        }

        backButton.setOnClickListener { moveToPreviousQuestion() }
        nextButton.setOnClickListener { moveToNextQuestion() }
        saveButton.setOnClickListener { saveProgress(true) }
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

    override fun onPause() {
        super.onPause()
        if (!isQuizComplete) {
            saveProgress(false)
        }
    }

    private fun displayQuestion(question: Question) {
        val sharedPref = getSharedPreferences("QuizProgress", Context.MODE_PRIVATE)
        val savedAnswer = sharedPref.getInt(getAnswerKey(currentQuestionIndex), -1)
        val isQuestionAlreadyAnswered = savedAnswer != -1

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

            val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT)
            radioButton.layoutParams = layoutParams
            optionsRadioGroup.addView(radioButton)

            radioButton.setOnClickListener { handleAnswer(index) }
        }

        nextButton.text = "Next"

        if (isQuestionAlreadyAnswered) {
            optionsRadioGroup.check(savedAnswer)
            setOptionsEnabled(false)
        } else {
            optionsRadioGroup.clearCheck()
            setOptionsEnabled(true)
        }
    }

    private fun handleAnswer(selectedIndex: Int) {
        saveAnswer(selectedIndex)

        val correct = selectedIndex == questions[currentQuestionIndex].correctAnswerIndex
        if (correct) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show()
        }

        setOptionsEnabled(false)

        optionsRadioGroup.postDelayed({ moveToNextQuestion() }, 1200)
    }

    private fun moveToNextQuestion() {
        if (level < 10) {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("SKILL_NAME", skillName)
            intent.putExtra("LEVEL", level + 1)
            startActivity(intent)
            finish()
        } else {
            handleQuizCompletion()
        }
    }

    private fun moveToPreviousQuestion() {
        if (level > 1) {
            val intent = Intent(this, QuizActivity::class.java)
            intent.putExtra("SKILL_NAME", skillName)
            intent.putExtra("LEVEL", level - 1)
            startActivity(intent)
            finish()
        } else {
            finish()
        }
    }

    private fun handleQuizCompletion() {
        isQuizComplete = true
        val sharedPref = getSharedPreferences("QuizProgress", Context.MODE_PRIVATE)
        var score = 0
        for (i in questions.indices) {
            val savedAnswer = sharedPref.getInt(getAnswerKey(i), -1)
            if (savedAnswer != -1 && savedAnswer == questions[i].correctAnswerIndex) {
                score++
            }
        }

        val percentage = if (questions.isNotEmpty()) (score.toDouble() / questions.size.toDouble()) * 100 else 0.0
        val passed = percentage >= 80

        val resultIntent = Intent()
        resultIntent.putExtra("level", level)
        resultIntent.putExtra("passed", passed)
        setResult(Activity.RESULT_OK, resultIntent)

        if (!passed) {
            Toast.makeText(this, "Level failed. Try again!", Toast.LENGTH_LONG).show()
            clearLevelProgress()
        }

        finish()
    }

    private fun clearLevelProgress() {
        val sharedPref = getSharedPreferences("QuizProgress", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            remove(getProgressKey())
            for (i in questions.indices) {
                remove(getAnswerKey(i))
            }
            commit()
        }
    }

    private fun setOptionsEnabled(enabled: Boolean) {
        for (i in 0 until optionsRadioGroup.childCount) {
            optionsRadioGroup.getChildAt(i).isEnabled = enabled
        }
    }

    private fun getProgressKey(): String = "${progressKey}_progress"
    private fun getAnswerKey(questionIndex: Int): String = "${progressKey}_answer_$questionIndex"

    private fun saveAnswer(answerIndex: Int) {
        val sharedPref = getSharedPreferences("QuizProgress", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(getAnswerKey(currentQuestionIndex), answerIndex)
            apply()
        }
    }

    private fun saveProgress(showToast: Boolean) {
        val sharedPref = getSharedPreferences("QuizProgress", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(getProgressKey(), currentQuestionIndex)
            apply()
        }
        if (showToast) {
            Toast.makeText(this, "Progress saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadProgress() {
        val sharedPref = getSharedPreferences("QuizProgress", Context.MODE_PRIVATE) ?: return
        currentQuestionIndex = sharedPref.getInt(getProgressKey(), 0)
    }
}
