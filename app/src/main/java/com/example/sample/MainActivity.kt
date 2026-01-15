package com.example.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.data.SampleSkills

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val skillTreeRecyclerView: RecyclerView = findViewById(R.id.skill_tree_recycler_view)
        skillTreeRecyclerView.layoutManager = LinearLayoutManager(this)

        val resetButton: Button = findViewById(R.id.reset_button)
        resetButton.setOnClickListener {
            getSharedPreferences("LevelStatus", Context.MODE_PRIVATE).edit().clear().commit()
            getSharedPreferences("TopicStatus", Context.MODE_PRIVATE).edit().clear().commit()
            getSharedPreferences("WrongAnswers", Context.MODE_PRIVATE).edit().clear().commit()
            getSharedPreferences("AttemptCounter", Context.MODE_PRIVATE).edit().clear().commit()
            getSharedPreferences("TopicUnlockStatus", Context.MODE_PRIVATE).edit().clear().commit()

            Toast.makeText(this, "All progress has been reset.", Toast.LENGTH_SHORT).show()

            recreate()
        }

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val skillTreeRecyclerView: RecyclerView = findViewById(R.id.skill_tree_recycler_view)
        skillTreeRecyclerView.adapter = SkillAdapter(SampleSkills.skills, this)
    }
}
