package com.example.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class SkillActivity : AppCompatActivity() {

    private lateinit var levelContainer: LinearLayout
    private val levelStatus = mutableMapOf<Int, Boolean?>()
    private lateinit var skillName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill)

        // The default action bar is hidden, so this is no longer needed.
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)

        skillName = intent.getStringExtra("SKILL_NAME") ?: ""
        val skillTitle: TextView = findViewById(R.id.skill_title)
        skillTitle.text = skillName

        levelContainer = findViewById(R.id.level_container)

        // Find and set up the back button
        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish() // Close this activity and go back
        }

        loadLevelStatus()
        createLevelButtons()
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
                quizResultLauncher.launch(intent)
            }
            levelContainer.addView(levelButtonView)
        }
        updateLevelButtons()
    }

    private val quizResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val level = data?.getIntExtra("level", 0) ?: 0
            val passed = data?.getBooleanExtra("passed", false) ?: false
            levelStatus[level] = passed
            saveLevelStatus()
            updateLevelButtons()
        }
    }

    private fun updateLevelButtons() {
        for (i in 0 until levelContainer.childCount) {
            val levelButtonView = levelContainer.getChildAt(i)
            val level = i + 1
            val passed = levelStatus[level]

            val levelButton: Button = levelButtonView.findViewById(R.id.level_button)
            val statusIcon: ImageView = levelButtonView.findViewById(R.id.level_status_icon)

            when (passed) {
                true -> {
                    levelButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
                    statusIcon.setImageResource(R.drawable.ic_star)
                    statusIcon.visibility = View.VISIBLE
                }
                false -> {
                    levelButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
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

    private fun saveLevelStatus() {
        val sharedPref = getSharedPreferences("LevelStatus", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            levelStatus.forEach { (level, passed) ->
                if (passed != null) {
                    putBoolean("${skillName}_${level}_passed", passed)
                }
            }
            apply()
        }
    }

    private fun loadLevelStatus() {
        val sharedPref = getSharedPreferences("LevelStatus", Context.MODE_PRIVATE) ?: return
        for (i in 1..10) {
            if (sharedPref.contains("${skillName}_${i}_passed")) {
                levelStatus[i] = sharedPref.getBoolean("${skillName}_${i}_passed", false)
            }
        }
    }

    // This is no longer needed as we have a custom back button
    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
    */
}
