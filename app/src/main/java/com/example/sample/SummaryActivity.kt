package com.example.sample

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.data.SampleSkills
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SummaryActivity : AppCompatActivity() {

    private lateinit var summaryRecyclerView: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        mAuth = FirebaseAuth.getInstance()

        summaryRecyclerView = findViewById(R.id.summary_recycler_view)
        summaryRecyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser != null) {
            refreshSkillListFromFirestore()
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun refreshSkillListFromFirestore() {
        val userId = mAuth.currentUser!!.uid
        val progressCollection = db.collection("users").document(userId).collection("progress")

        progressCollection.get()
            .addOnSuccessListener { documents ->
                // From all the progress documents, find the unique skill names that have been attempted.
                val startedSkillsNames = documents.map { it.id.substringBeforeLast('_') }.toSet()

                // Filter the main SampleSkills list to get the Skill objects.
                val startedSkills = SampleSkills.skills.filter { it.name in startedSkillsNames }

                summaryRecyclerView.adapter = SummaryAdapter(startedSkills, this)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to load summary: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
