package com.example.sample

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.data.SampleSkills

class MainActivity : AppCompatActivity() {

    private lateinit var nickname: String
    private lateinit var skillTreeRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        skillTreeRecyclerView = findViewById(R.id.skill_tree_recycler_view)
        skillTreeRecyclerView.layoutManager = LinearLayoutManager(this)

        val topicText: TextView = findViewById(R.id.topic_text)
        topicText.text = "Topic"

        val resetButton: Button = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            resetAllProgressForCurrentUser()
            Toast.makeText(this, "All progress for $nickname has been reset.", Toast.LENGTH_SHORT).show()
            recreate()
        }

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun resetAllProgressForCurrentUser() {
        val levelStatusPrefs = getSharedPreferences("LevelStatus", Context.MODE_PRIVATE).edit()
        val topicStatusPrefs = getSharedPreferences("TopicStatus", Context.MODE_PRIVATE).edit()
        val wrongAnswersPrefs = getSharedPreferences("WrongAnswers", Context.MODE_PRIVATE).edit()
        val attemptCounterPrefs = getSharedPreferences("AttemptCounter", Context.MODE_PRIVATE).edit()
        val topicUnlockPrefs = getSharedPreferences("TopicUnlockStatus", Context.MODE_PRIVATE).edit()
        val assessmentStatusPrefs = getSharedPreferences("AssessmentStatus", Context.MODE_PRIVATE).edit()

        for (skill in SampleSkills.skills) {
            topicUnlockPrefs.remove("${nickname}_${skill.name}")
            topicStatusPrefs.remove("${nickname}_${skill.name}_shown")
            for (i in 1..10) {
                levelStatusPrefs.remove("${nickname}_${skill.name}_${i}_passed")
                wrongAnswersPrefs.remove("${nickname}_${skill.name}_${i}_wrong_answers")
                attemptCounterPrefs.remove("${nickname}_${skill.name}_${i}_attempts")
            }
        }
        assessmentStatusPrefs.remove("${nickname}_hasTakenAssessment")

        levelStatusPrefs.apply()
        topicStatusPrefs.apply()
        wrongAnswersPrefs.apply()
        attemptCounterPrefs.apply()
        topicUnlockPrefs.apply()
        assessmentStatusPrefs.apply()
    }

    private fun updateTopicUnlockStatus() {
        val unlockPrefs = getSharedPreferences("TopicUnlockStatus", Context.MODE_PRIVATE)
        val levelStatusPrefs = getSharedPreferences("LevelStatus", Context.MODE_PRIVATE)
        val editor = unlockPrefs.edit()
        var changed = false

        for (i in 1 until SampleSkills.skills.size) {
            val currentSkill = SampleSkills.skills[i]
            val previousSkill = SampleSkills.skills[i - 1]

            if (!unlockPrefs.getBoolean("${nickname}_${currentSkill.name}", false)) {
                if (isTopicPassed(levelStatusPrefs, previousSkill.name)) {
                    editor.putBoolean("${nickname}_${currentSkill.name}", true)
                    changed = true
                }
            }
        }

        if (changed) {
            editor.apply()
        }
    }

    private fun isTopicPassed(prefs: SharedPreferences, topicName: String): Boolean {
        var passedLevels = 0
        for (i in 1..10) {
            if (prefs.getBoolean("${nickname}_${topicName}_${i}_passed", false)) {
                passedLevels++
            }
        }
        val score = (passedLevels.toDouble() / 10.0) * 100
        return score >= 80
    }

    override fun onResume() {
        super.onResume()
        val userProfilePrefs = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        nickname = userProfilePrefs.getString("NICKNAME", "") ?: ""

        updateTopicUnlockStatus()
        skillTreeRecyclerView.adapter = SkillAdapter(SampleSkills.skills, this)
    }
}
