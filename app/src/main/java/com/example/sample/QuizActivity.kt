package com.example.sample

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sample.data.BasicHardwareQuiz
import com.example.sample.data.CssBasicsQuiz
import com.example.sample.data.HtmlBasicsQuiz
import com.example.sample.data.IntroToITQuiz
import com.example.sample.data.IntroductionToProgrammingQuiz
import com.example.sample.data.OperatingSystemsQuiz
import com.example.sample.models.Question
import com.google.android.material.button.MaterialButton

class QuizActivity : AppCompatActivity() {

    private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var questionImageView: ImageView
    private lateinit var previousLevelButton: MaterialButton
    private lateinit var nextLevelButton: MaterialButton
    private lateinit var homeButton: MaterialButton
    private lateinit var attemptCounterTextView: TextView

    private lateinit var questions: List<Question>
    private lateinit var skillName: String
    private lateinit var nickname: String
    private var currentQuestionIndex: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        skillName = intent.getStringExtra("SKILL_NAME") ?: ""
        currentQuestionIndex = (intent.getIntExtra("LEVEL", 1) - 1).coerceAtLeast(0)

        val userProfilePrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        nickname = userProfilePrefs.getString("NICKNAME", "") ?: ""

        initializeViews()
        loadQuestionsForSkill()
        setupNavigationListeners()

        if (questions.isNotEmpty()) {
            displayQuestion()
        } else {
            Toast.makeText(this, "No questions available for this topic.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews() {
        questionTextView = findViewById(R.id.question_text)
        optionsRadioGroup = findViewById(R.id.options_radio_group)
        questionImageView = findViewById(R.id.question_image)
        previousLevelButton = findViewById(R.id.previous_level_button)
        nextLevelButton = findViewById(R.id.next_level_button)
        homeButton = findViewById(R.id.home_button)
        attemptCounterTextView = findViewById(R.id.attempt_counter_text)
    }

    private fun loadQuestionsForSkill() {
        questions = when (skillName) {
            "Introduction to IT" -> IntroToITQuiz.questions
            "Basic Computer Hardware" -> BasicHardwareQuiz.questions
            "Operating Systems" -> OperatingSystemsQuiz.questions
            "Introduction to Programming" -> IntroductionToProgrammingQuiz.questions
            "HTML Basics" -> HtmlBasicsQuiz.questions
            "CSS Basics" -> CssBasicsQuiz.questions
            else -> emptyList()
        }
    }

    private fun displayQuestion() {
        val question = questions[currentQuestionIndex]
        questionTextView.text = "${currentQuestionIndex + 1}. ${question.text}"

        val imageRes = question.imageResId
        if (imageRes != null) {
            questionImageView.visibility = View.VISIBLE
            questionImageView.setImageResource(imageRes)
        } else {
            questionImageView.visibility = View.GONE
        }

        optionsRadioGroup.removeAllViews()
        optionsRadioGroup.setOnCheckedChangeListener(null)

        val customFont = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.roboto_bold)

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked), // Unchecked
                intArrayOf(android.R.attr.state_checked)  // Checked
            ),
            intArrayOf(
                ContextCompat.getColor(this, android.R.color.darker_gray), // Default color for unchecked
                ContextCompat.getColor(this, R.color.celestial_blue)     // celestial_blue for checked
            )
        )

        question.options.forEachIndexed { index, option ->
            val radioButton = RadioButton(this).apply {
                text = option
                id = index
                textSize = 23f
                typeface = customFont
                buttonTintList = colorStateList
                background = ContextCompat.getDrawable(context, R.drawable.rounded_white_background)
                setPadding(20, 20, 20, 20)
            }

            val layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 16, 0, 16)
            }
            radioButton.layoutParams = layoutParams

            optionsRadioGroup.addView(radioButton)
        }

        val isPassed = getPassedStatus(currentQuestionIndex)
        val attempts = getAttemptCount(currentQuestionIndex)
        val wrongAnswers = getWrongAnswers(currentQuestionIndex)

        if (isPassed) {
            showAnswerFeedback(wrongAnswers, question.correctAnswerIndex, showCorrect = true)
            setOptionsEnabled(false)
            if (attempts > 0) {
                attemptCounterTextView.text = "Attempt: $attempts/2"
                attemptCounterTextView.visibility = View.VISIBLE
            } else {
                attemptCounterTextView.visibility = View.GONE
            }
        } else if (attempts >= 2) {
            showAnswerFeedback(wrongAnswers, question.correctAnswerIndex, showCorrect = true)
            setOptionsEnabled(false)
            attemptCounterTextView.text = "Attempts: 2/2"
            attemptCounterTextView.visibility = View.VISIBLE
        } else {
            showAnswerFeedback(wrongAnswers, -1, showCorrect = false)
            if (attempts > 0) {
                attemptCounterTextView.text = "Attempt: $attempts/2"
                attemptCounterTextView.visibility = View.VISIBLE
            } else {
                attemptCounterTextView.visibility = View.GONE
            }
            setOptionsEnabled(true)
            optionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                handleAnswerSubmission(checkedId)
            }
        }

        previousLevelButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.INVISIBLE
        nextLevelButton.visibility = if (currentQuestionIndex < questions.size - 1) View.VISIBLE else View.INVISIBLE
    }

    private fun setupNavigationListeners() {
        previousLevelButton.setOnClickListener { navigateToQuestion(currentQuestionIndex - 1) }
        nextLevelButton.setOnClickListener { navigateToQuestion(currentQuestionIndex + 1) }
        homeButton.setOnClickListener { finish() }
    }

    private fun handleAnswerSubmission(checkedId: Int) {
        val question = questions[currentQuestionIndex]
        val passed = checkedId == question.correctAnswerIndex

        savePassedStatus(currentQuestionIndex, passed)

        if (passed) {
            val wrongAnswers = getWrongAnswers(currentQuestionIndex)
            showAnswerFeedback(wrongAnswers, checkedId, showCorrect = true)
            setOptionsEnabled(false)

            val attempts = getAttemptCount(currentQuestionIndex)
            if (attempts > 0) {
                attemptCounterTextView.text = "Attempt: ${attempts}/2"
                attemptCounterTextView.visibility = View.VISIBLE
            }
            optionsRadioGroup.postDelayed({ moveToNextQuestion() }, 1200)
        } else {
            incrementAttemptCount(currentQuestionIndex)
            val newAttemptCount = getAttemptCount(currentQuestionIndex)

            addWrongAnswer(currentQuestionIndex, checkedId)
            val wrongAnswers = getWrongAnswers(currentQuestionIndex)

            if (newAttemptCount >= 2) {
                savePassedStatus(currentQuestionIndex, false)
                showAnswerFeedback(wrongAnswers, question.correctAnswerIndex, showCorrect = true)
                setOptionsEnabled(false)
                attemptCounterTextView.text = "Attempts: 2/2"
                attemptCounterTextView.visibility = View.VISIBLE
                optionsRadioGroup.postDelayed({ moveToNextQuestion() }, 1200)
            } else {
                showAnswerFeedback(wrongAnswers, -1, showCorrect = false)
                attemptCounterTextView.text = "Attempt: 1/2"
                attemptCounterTextView.visibility = View.VISIBLE
                Toast.makeText(this, "Wrong! Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAnswerFeedback(wronglySelectedIds: Set<Int>, correctId: Int, showCorrect: Boolean) {
        for (i in 0 until optionsRadioGroup.childCount) {
            val radioButton = optionsRadioGroup.getChildAt(i) as RadioButton
            val id = radioButton.id

            radioButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            if (showCorrect && id == correctId) {
                radioButton.setTextColor(ContextCompat.getColor(this, R.color.green))
                radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0)
            } else if (wronglySelectedIds.contains(id)) {
                radioButton.setTextColor(ContextCompat.getColor(this, R.color.red))
                radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_red, 0)
            }
        }
    }

    private fun navigateToQuestion(index: Int) {
        if (index >= 0 && index < questions.size) {
            currentQuestionIndex = index
            displayQuestion()
        }
    }

    private fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            navigateToQuestion(currentQuestionIndex + 1)
        } else {
            finish()
        }
    }

    private fun getPassedKey(index: Int): String = "${nickname}_${skillName}_${index + 1}_passed"
    private fun getWrongAnswersKey(index: Int): String = "${nickname}_${skillName}_${index + 1}_wrong_answers"
    private fun getAttemptKey(index: Int): String = "${nickname}_${skillName}_${index + 1}_attempts"

    private fun savePassedStatus(index: Int, passed: Boolean) {
        getSharedPreferences("LevelStatus", Context.MODE_PRIVATE).edit()
            .putBoolean(getPassedKey(index), passed).apply()
    }

    private fun getPassedStatus(index: Int): Boolean {
        return getSharedPreferences("LevelStatus", Context.MODE_PRIVATE)
            .getBoolean(getPassedKey(index), false)
    }

    private fun addWrongAnswer(index: Int, selectedAnswer: Int) {
        val prefs = getSharedPreferences("WrongAnswers", Context.MODE_PRIVATE)
        val wrongAnswers = prefs.getStringSet(getWrongAnswersKey(index), emptySet())?.toMutableSet() ?: mutableSetOf()
        wrongAnswers.add(selectedAnswer.toString())
        prefs.edit().putStringSet(getWrongAnswersKey(index), wrongAnswers).apply()
    }

    private fun getWrongAnswers(index: Int): Set<Int> {
        val prefs = getSharedPreferences("WrongAnswers", Context.MODE_PRIVATE)
        return prefs.getStringSet(getWrongAnswersKey(index), emptySet())?.mapNotNull { it.toIntOrNull() }?.toSet() ?: emptySet()
    }

    private fun incrementAttemptCount(index: Int) {
        val prefs = getSharedPreferences("AttemptCounter", Context.MODE_PRIVATE)
        val currentAttempts = prefs.getInt(getAttemptKey(index), 0)
        prefs.edit().putInt(getAttemptKey(index), currentAttempts + 1).apply()
    }

    private fun getAttemptCount(index: Int): Int {
        return getSharedPreferences("AttemptCounter", Context.MODE_PRIVATE).getInt(getAttemptKey(index), 0)
    }

    private fun setOptionsEnabled(enabled: Boolean) {
        for (i in 0 until optionsRadioGroup.childCount) {
            optionsRadioGroup.getChildAt(i).isEnabled = enabled
        }
    }
}
