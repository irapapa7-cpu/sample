package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.data.*
import com.example.sample.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TopicSummaryActivity : AppCompatActivity() {

    private lateinit var skillName: String
    private lateinit var recyclerView: RecyclerView

    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore

    private var currentScore = 0
    private var currentPercentage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_summary)

        mAuth = FirebaseAuth.getInstance()
        skillName = intent.getStringExtra("SKILL_NAME") ?: ""

        findViewById<TextView>(R.id.topic_summary_title).text = skillName
        recyclerView = findViewById(R.id.topic_summary_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }
        findViewById<Button>(R.id.show_score_button).setOnClickListener { showScore() }
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser != null) {
            refreshAnsweredQuestionsFromFirestore()
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun refreshAnsweredQuestionsFromFirestore() {
        val userId = mAuth.currentUser!!.uid
        val allQuestionsForSkill = getQuestionsForSkill(skillName)
        val progressCollection = db.collection("users").document(userId).collection("progress")

        progressCollection
            .orderBy(FieldPath.documentId())
            .startAt(skillName + "_")
            .endAt(skillName + "_\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                val answeredQuestions = mutableListOf<Pair<Int, Question>>()
                var score = 0

                for (doc in documents) {
                    val level = doc.id.substringAfterLast('_').toIntOrNull()
                    if (level != null && level - 1 < allQuestionsForSkill.size) {
                        answeredQuestions.add(Pair(level - 1, allQuestionsForSkill[level - 1]))
                        if (doc.getBoolean("passed") == true) {
                            score++
                        }
                    }
                }

                currentScore = score
                currentPercentage = (score.toDouble() / allQuestionsForSkill.size.coerceAtLeast(1) * 100).toInt()

                // THE FIX: Sort the list numerically by the question index (the first item in the Pair).
                val sortedQuestions = answeredQuestions.sortedBy { it.first }

                recyclerView.adapter = TopicSummaryAdapter(this, sortedQuestions, skillName)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load summary: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
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

    private fun showScore() {
        val intent = Intent(this, ScoreActivity::class.java).apply {
            putExtra("SKILL_NAME", skillName)
            putExtra("SCORE", currentScore)
            putExtra("PERCENTAGE", currentPercentage)
            putExtra("SOURCE", "TopicSummaryActivity")
        }
        startActivity(intent)
    }
}
