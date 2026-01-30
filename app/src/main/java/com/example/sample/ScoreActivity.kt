package com.example.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ScoreActivity : AppCompatActivity() {

    private lateinit var skillName: String
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        skillName = intent.getStringExtra("SKILL_NAME") ?: ""
        val score = intent.getIntExtra("SCORE", 0)
        val percentage = intent.getIntExtra("PERCENTAGE", 0)
        val source = intent.getStringExtra("SOURCE") ?: ""

        // --- Setup Views ---
        findViewById<TextView>(R.id.score_popup_title).text = skillName
        val scoreTextView = findViewById<TextView>(R.id.score_popup_score)
        val percentageTextView = findViewById<TextView>(R.id.score_popup_percentage)
        scoreTextView.text = "Score: $score / 10"
        percentageTextView.text = "($percentage%)"

        val colorRes = if (percentage >= 80) R.color.green else R.color.red
        scoreTextView.setTextColor(ContextCompat.getColor(this, colorRes))
        percentageTextView.setTextColor(ContextCompat.getColor(this, colorRes))

        val (level, description) = getKnowledgeLevel(percentage)
        findViewById<TextView>(R.id.knowledge_level_title).text = level
        findViewById<TextView>(R.id.knowledge_level_description).text = description
        findViewById<TextView>(R.id.text_know).text = "Knowledge Level: "

        // --- Button Visibility ---
        val resetButton: Button = findViewById(R.id.score_popup_reset_button)
        val summaryButton: Button = findViewById(R.id.score_popup_summary_button)
        val backToLevelsButton: Button = findViewById(R.id.score_popup_back_to_levels_button)

        if (source == "TopicSummaryActivity") {
            resetButton.visibility = View.GONE
            summaryButton.visibility = View.GONE
            backToLevelsButton.visibility = View.VISIBLE
        } else {
            resetButton.visibility = View.VISIBLE
            summaryButton.visibility = View.VISIBLE
            backToLevelsButton.visibility = View.GONE
        }

        // --- Button Listeners ---
        resetButton.setOnClickListener { resetCurrentTopicProgress() }
        summaryButton.setOnClickListener { 
            val intent = Intent(this, TopicSummaryActivity::class.java).apply {
                putExtra("SKILL_NAME", skillName)
            }
            startActivity(intent)
            finish()
        }

        backToLevelsButton.setOnClickListener { 
            val intent = Intent(this, SkillActivity::class.java).apply {
                putExtra("SKILL_NAME", skillName)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
            finish()
        } 

        findViewById<ImageView>(R.id.score_popup_close_button).setOnClickListener { finish() }
    }

    private fun getKnowledgeLevel(percentage: Int): Pair<String, String> {
        return when {
            percentage <= 49 -> "Smooth Brain" to "no lumps just smooth."
            percentage <= 69 -> "Brain Rot" to "lumpier than smooth brain."
            percentage <= 79 -> "Chicken Head" to "Just a little bit further."
            percentage <= 89 -> "Average Joe" to "well, well, well welcome Joe !"
            percentage <= 99 -> "Super Brain" to "I eat books in the morning."
            else -> "Megamind" to "I don\'t seek knowledge, knowledge seek me."
        }
    }

    private fun resetCurrentTopicProgress() {
        val userId = mAuth.currentUser?.uid
        if (userId == null || skillName.isEmpty()) {
            Toast.makeText(this, "Cannot reset progress. User not logged in or skill is invalid.", Toast.LENGTH_LONG).show()
            return
        }

        Toast.makeText(this, "Resetting progress for '$skillName'...", Toast.LENGTH_SHORT).show()

        val userRef = db.collection("users").document(userId)
        val progressCollection = userRef.collection("progress")
        val topicStatusRef = userRef.collection("topicStatus").document(skillName)

        val batch = db.batch()

        // 1. Delete all 10 level documents for the current skill.
        for (i in 1..10) {
            val docRef = progressCollection.document("${skillName}_$i")
            batch.delete(docRef)
        }

        // 2. THE FIX: Delete the "pop-up already shown" flag for this topic.
        batch.delete(topicStatusRef)

        batch.commit()
            .addOnSuccessListener {
                Toast.makeText(this, "Progress for '$skillName' has been reset.", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK) // This can notify the previous activity to refresh
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to reset progress: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
