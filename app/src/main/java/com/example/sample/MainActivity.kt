package com.example.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.data.SampleSkills

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val skillTreeRecyclerView: RecyclerView = findViewById(R.id.skill_tree_recycler_view)
        skillTreeRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SkillAdapter(SampleSkills.skills) { skill ->
            val intent = Intent(this, SkillActivity::class.java)
            intent.putExtra("SKILL_NAME", skill.name)
            startActivity(intent)
        }
        skillTreeRecyclerView.adapter = adapter

        val resetButton: Button = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            // Clear all quiz progress
            val quizProgressPrefs = getSharedPreferences("QuizProgress", Context.MODE_PRIVATE)
            quizProgressPrefs.edit().clear().apply()

            // Clear all completed level statuses
            val levelStatusPrefs = getSharedPreferences("LevelStatus", Context.MODE_PRIVATE)
            levelStatusPrefs.edit().clear().apply()

            Toast.makeText(this, "All progress has been reset.", Toast.LENGTH_SHORT).show()

            // Recreate the activity to refresh the UI
            recreate()
        }
    }
}
