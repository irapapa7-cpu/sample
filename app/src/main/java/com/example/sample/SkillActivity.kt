package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SkillActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val skillName = intent.getStringExtra("SKILL_NAME")
        val skillTitle: TextView = findViewById(R.id.skill_title)
        skillTitle.text = skillName

        val levelContainer: LinearLayout = findViewById(R.id.level_container)

        for (i in 1..10) {
            val levelButton = Button(this)
            levelButton.text = "Level $i"
            levelButton.setOnClickListener {
                val intent = Intent(this, QuizActivity::class.java)
                intent.putExtra("SKILL_NAME", skillName)
                intent.putExtra("LEVEL", i)
                startActivity(intent)
            }
            levelContainer.addView(levelButton)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
