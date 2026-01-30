package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SkillActivity : AppCompatActivity() {

    private lateinit var levelContainer: LinearLayout
    private lateinit var scoreTextView: TextView
    private lateinit var percentageTextView: TextView

    private lateinit var skillName: String
    private val levelStatus = mutableMapOf<Int, Boolean>()

    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill)

        mAuth = FirebaseAuth.getInstance()

        skillName = intent.getStringExtra("SKILL_NAME") ?: ""
        findViewById<TextView>(R.id.skill_title).text = skillName

        levelContainer = findViewById(R.id.level_container)
        scoreTextView = findViewById(R.id.score_text_view)
        percentageTextView = findViewById(R.id.percentage_text_view)

        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }

        findViewById<Button>(R.id.reset_button).setOnClickListener {
            val score = levelStatus.values.count { it }
            val percentage = ((score.toDouble() / 10.0) * 100).toInt()

            val intent = Intent(this, ScoreActivity::class.java).apply {
                putExtra("SKILL_NAME", skillName)
                putExtra("SCORE", score)
                putExtra("PERCENTAGE", percentage)
            }
            startActivity(intent)
        }

        createLevelButtons()
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser != null) {
            loadLevelStatusFromFirestore()
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun createLevelButtons() {
        levelContainer.removeAllViews()
        for (i in 1..10) {
            val levelButtonView = LayoutInflater.from(this).inflate(R.layout.level_button, levelContainer, false)
            val levelButton: Button = levelButtonView.findViewById(R.id.level_button)
            levelButton.text = "Level $i"

            levelButton.setOnClickListener {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("SKILL_NAME", skillName)
                intent.putExtra("LEVEL", i)
                startActivity(intent)
            }
            levelContainer.addView(levelButtonView)
        }
    }

    private fun loadLevelStatusFromFirestore() {
        val userId = mAuth.currentUser!!.uid
        val progressCollection = db.collection("users").document(userId).collection("progress")

        levelStatus.clear()

        // THE FIX: Use a prefix query on the document ID to get all levels for the current skill.
        progressCollection
            .orderBy(FieldPath.documentId())
            .startAt(skillName + "_")
            .endAt(skillName + "_\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val level = document.id.substringAfterLast('_').toIntOrNull()
                    val passed = document.getBoolean("passed")
                    
                    if (level != null && passed != null) {
                        levelStatus[level] = passed
                    }
                }
                updateUI()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load progress: ${exception.message}", Toast.LENGTH_SHORT).show()
                updateUI() // Still update UI to show default state on failure
            }
    }

    private fun updateUI() {
        updateLevelButtons()
        updateScoreDisplay()
        updatePercentageDisplay()
        checkTopicCompletion()
    }

    private fun updateLevelButtons() {
        for (i in 0 until levelContainer.childCount) {
            val levelButtonView = levelContainer.getChildAt(i)
            val level = i + 1
            val statusIcon: ImageView = levelButtonView.findViewById(R.id.level_status_icon)
            val levelButton: Button = levelButtonView.findViewById(R.id.level_button)

            when (levelStatus[level]) {
                true -> {
                    levelButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.green)
                    statusIcon.setImageResource(R.drawable.ic_star)
                    statusIcon.visibility = View.VISIBLE
                }
                false -> {
                    levelButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.red)
                    statusIcon.setImageResource(R.drawable.ic_star_gray)
                    statusIcon.visibility = View.VISIBLE
                }
                null -> {
                    levelButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.celestial_blue)
                    statusIcon.visibility = View.GONE
                }
            }
        }
    }

    private fun updateScoreDisplay() {
        val score = levelStatus.values.count { it }
        scoreTextView.text = "Score: $score / 10"
    }

    private fun updatePercentageDisplay() {
        val score = levelStatus.values.count { it }
        val percentage = (score.toDouble() / 10.0) * 100
        percentageTextView.text = "(${percentage.toInt()}%)"

        val colorRes = if (percentage >= 80) R.color.green else R.color.red
        scoreTextView.setTextColor(ContextCompat.getColor(this, colorRes))
        percentageTextView.setTextColor(ContextCompat.getColor(this, colorRes))
    }

    private fun checkTopicCompletion() {
        val userId = mAuth.currentUser?.uid ?: return
        val topicStatusRef = db.collection("users").document(userId).collection("topicStatus").document(skillName)

        topicStatusRef.get().addOnSuccessListener { document ->
            if (document.exists() && document.getBoolean("shown") == true) {
                return@addOnSuccessListener
            }

            if (levelStatus.size == 10) { 
                val score = levelStatus.values.count { it }
                val percentage = (score.toDouble() / 10.0) * 100

                val intent = if (percentage >= 80) {
                    Intent(this, CompleteActivity::class.java)
                } else {
                    Intent(this, FailedActivity::class.java)
                }

                intent.apply {
                    putExtra("SKILL_NAME", skillName)
                    putExtra("SCORE", score)
                    putExtra("PERCENTAGE", percentage.toInt())
                }
                startActivity(intent)
                topicStatusRef.set(mapOf("shown" to true))
            }
        }
    }
}
