package com.example.sample

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
import com.example.sample.data.*
import com.example.sample.models.Question
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuizActivity : AppCompatActivity() {

    // Views
    private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var questionImageView: ImageView
    private lateinit var previousLevelButton: MaterialButton
    private lateinit var nextLevelButton: MaterialButton
    private lateinit var homeButton: MaterialButton
    private lateinit var attemptCounterTextView: TextView

    // Data
    private lateinit var questions: List<Question>
    private lateinit var skillName: String
    private var currentQuestionIndex: Int = 0

    // Firebase
    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser == null) {
            Toast.makeText(this, "User not logged in. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        skillName = intent.getStringExtra("SKILL_NAME") ?: ""
        currentQuestionIndex = (intent.getIntExtra("LEVEL", 1) - 1).coerceAtLeast(0)

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

        if (question.imageResId != null) {
            questionImageView.setImageResource(question.imageResId)
            questionImageView.visibility = View.VISIBLE
        } else {
            questionImageView.visibility = View.GONE
        }

        // Fetch progress from Firestore before rendering options
        loadProgressAndDisplayOptions()

        previousLevelButton.visibility = if (currentQuestionIndex > 0) View.VISIBLE else View.INVISIBLE
        nextLevelButton.visibility = if (currentQuestionIndex < questions.size - 1) View.VISIBLE else View.INVISIBLE
    }

    private fun getProgressDocRef(levelIndex: Int) = db.collection("users").document(mAuth.currentUser!!.uid)
        .collection("progress").document("${skillName}_${levelIndex + 1}")

    private fun loadProgressAndDisplayOptions() {
        getProgressDocRef(currentQuestionIndex).get().addOnSuccessListener { document ->
            val question = questions[currentQuestionIndex]
            optionsRadioGroup.removeAllViews()
            optionsRadioGroup.setOnCheckedChangeListener(null)

            val isPassed = document.getBoolean("passed") ?: false
            val attempts = document.getLong("attempts")?.toInt() ?: 0
            val wrongAnswers = (document.get("wrongAnswers") as? List<Long>)?.map { it.toInt() }?.toSet() ?: emptySet()

            // --- RENDER RADIO BUTTONS ---
            val customFont = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.roboto_bold)
            val colorStateList = ColorStateList(
                arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
                intArrayOf(ContextCompat.getColor(this, android.R.color.darker_gray), ContextCompat.getColor(this, R.color.celestial_blue))
            )

            question.options.forEachIndexed { index, option ->
                val radioButton = RadioButton(this).apply {
                    text = option
                    id = index
                    textSize = 23f
                    typeface = customFont
                    buttonTintList = colorStateList
                    background = ContextCompat.getDrawable(context, R.drawable.rounded_white_background)
                    setPadding(48, 48, 48, 48)
                    layoutParams = RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT).apply {
                        setMargins(0, 16, 0, 16)
                    }
                }
                optionsRadioGroup.addView(radioButton)
            }
            // --- END RENDER ---

            // Update UI based on the loaded progress
            attemptCounterTextView.visibility = if (attempts > 0) View.VISIBLE else View.GONE
            attemptCounterTextView.text = "Attempts: $attempts/2"

            if (isPassed || attempts >= 2) {
                showAnswerFeedback(wrongAnswers, question.correctAnswerIndex, showCorrect = true)
                setOptionsEnabled(false)
            } else {
                showAnswerFeedback(wrongAnswers, -1, showCorrect = false)
                setOptionsEnabled(true)
                optionsRadioGroup.setOnCheckedChangeListener { _, checkedId -> handleAnswerSubmission(checkedId) }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load progress: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleAnswerSubmission(checkedId: Int) {
        val question = questions[currentQuestionIndex]
        val isCorrect = checkedId == question.correctAnswerIndex
        val progressRef = getProgressDocRef(currentQuestionIndex)

        val progressData = mutableMapOf<String, Any>()

        if (isCorrect) {
            progressData["passed"] = true
            progressRef.set(progressData, SetOptions.merge()).addOnSuccessListener {
                displayQuestion() // Re-render the UI in its final state
                optionsRadioGroup.postDelayed({ moveToNextQuestion() }, 1200)
            }
        } else {
            progressData["wrongAnswers"] = FieldValue.arrayUnion(checkedId)
            progressData["attempts"] = FieldValue.increment(1)
            
            progressRef.get().addOnSuccessListener { doc ->
                val currentAttempts = doc.getLong("attempts")?.toInt() ?: 0
                if (currentAttempts + 1 >= 2) {
                    progressData["passed"] = false // Explicitly mark as failed
                }

                progressRef.set(progressData, SetOptions.merge()).addOnSuccessListener { 
                    displayQuestion() // Re-render the UI
                    if (currentAttempts + 1 >= 2) {
                         optionsRadioGroup.postDelayed({ moveToNextQuestion() }, 1200)
                    }
                }
            }
        }
    }

    private fun showAnswerFeedback(wronglySelectedIds: Set<Int>, correctId: Int, showCorrect: Boolean) {
        for (i in 0 until optionsRadioGroup.childCount) {
            val radioButton = optionsRadioGroup.getChildAt(i) as RadioButton
            when {
                showCorrect && i == correctId -> {
                    radioButton.setTextColor(ContextCompat.getColor(this, R.color.green))
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_green, 0)
                }
                wronglySelectedIds.contains(i) -> {
                    radioButton.setTextColor(ContextCompat.getColor(this, R.color.red))
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_clear_red, 0)
                }
                else -> {
                    radioButton.setTextColor(ContextCompat.getColor(this, android.R.color.black))
                    radioButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                }
            }
        }
    }

    private fun setupNavigationListeners() {
        previousLevelButton.setOnClickListener { navigateToQuestion(currentQuestionIndex - 1) }
        nextLevelButton.setOnClickListener { navigateToQuestion(currentQuestionIndex + 1) }
        homeButton.setOnClickListener { finish() }
    }

    private fun navigateToQuestion(index: Int) {
        if (index in 0 until questions.size) {
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

    private fun setOptionsEnabled(enabled: Boolean) {
        for (i in 0 until optionsRadioGroup.childCount) {
            optionsRadioGroup.getChildAt(i).isEnabled = enabled
        }
    }
}
