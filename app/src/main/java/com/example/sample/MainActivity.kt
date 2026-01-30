package com.example.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.data.SampleSkills
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var skillTreeRecyclerView: RecyclerView
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        skillTreeRecyclerView = findViewById(R.id.skill_tree_recycler_view)
        skillTreeRecyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.reset_button).setOnClickListener {
            resetAllProgressForCurrentUser()
        }

        findViewById<Button>(R.id.back_button).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mAuth.currentUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
        } else {
            updateTopicUnlockStatusAndRefreshAdapter()
        }
    }

    private fun updateTopicUnlockStatusAndRefreshAdapter() {
        val userId = mAuth.currentUser!!.uid
        val userRef = db.collection("users").document(userId)
        val progressCollection = userRef.collection("progress")
        val unlockStatusCollection = userRef.collection("unlockStatus")

        // Step 1: Get all currently unlocked topics from the persistent unlock status collection.
        unlockStatusCollection.get().addOnSuccessListener { unlockSnapshot ->
            val currentlyUnlocked = unlockSnapshot.documents.map { it.id }.toMutableSet()
            // The first topic is always considered unlocked.
            currentlyUnlocked.add(SampleSkills.skills.first().name)

            // Step 2: Calculate scores to see if any NEW topics should be unlocked.
            progressCollection.get().addOnSuccessListener { progressSnapshot ->
                val skillScores = mutableMapOf<String, Int>()
                for (document in progressSnapshot) {
                    val skillName = document.id.substringBeforeLast('_', "")
                    if (skillName.isNotEmpty() && document.getBoolean("passed") == true) {
                        skillScores[skillName] = (skillScores.getOrDefault(skillName, 0)) + 1
                    }
                }

                var madeChanges = false
                // Step 3: Add any newly unlocked topics to our master set.
                for (i in 0 until SampleSkills.skills.size - 1) {
                    val currentSkillName = SampleSkills.skills[i].name
                    val nextSkillName = SampleSkills.skills[i + 1].name
                    val score = skillScores.getOrDefault(currentSkillName, 0)

                    if ((score.toDouble() / 10.0) * 100 >= 80) {
                        // .add() returns true if the item was not already in the set.
                        if (currentlyUnlocked.add(nextSkillName)) {
                            madeChanges = true
                        }
                    }
                }

                // Step 4: If we unlocked a new topic, save the new complete set back to Firestore.
                if (madeChanges) {
                    val batch = db.batch()
                    for (skillName in currentlyUnlocked) {
                        batch.set(unlockStatusCollection.document(skillName), mapOf("unlocked" to true))
                    }
                    batch.commit()
                }
                
                // Step 5: Convert the final set to the map format the adapter needs and update the UI.
                val finalUnlockStatus = currentlyUnlocked.associateWith { true }
                skillTreeRecyclerView.adapter = SkillAdapter(SampleSkills.skills, this, finalUnlockStatus)

            }.addOnFailureListener {
                // On failure, just use the unlocks we loaded initially.
                 val finalUnlockStatus = currentlyUnlocked.associateWith { true }
                 skillTreeRecyclerView.adapter = SkillAdapter(SampleSkills.skills, this, finalUnlockStatus)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to load unlock status.", Toast.LENGTH_SHORT).show()
            skillTreeRecyclerView.adapter = SkillAdapter(SampleSkills.skills, this, mapOf(SampleSkills.skills.first().name to true))
        }
    }

    private fun resetAllProgressForCurrentUser() {
        val userId = mAuth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(userId)

        Toast.makeText(this, "Resetting all progress...", Toast.LENGTH_SHORT).show()

        // THE FIX: Add "unlockStatus" to the list of collections to be deleted.
        val subcollectionsToDelete = listOf("progress", "topicStatus", "assessmentStatus", "unlockStatus")
        var collectionsDeleted = 0

        for (collectionName in subcollectionsToDelete) {
            userRef.collection(collectionName).get().addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { batch.delete(it.reference) }
                batch.commit().addOnCompleteListener { task ->
                    collectionsDeleted++
                    if (collectionsDeleted == subcollectionsToDelete.size) {
                        Toast.makeText(this, "All progress has been reset.", Toast.LENGTH_SHORT).show()
                        recreate()
                    }
                }
            }.addOnFailureListener { 
                 collectionsDeleted++
                 if (collectionsDeleted == subcollectionsToDelete.size) {
                    Toast.makeText(this, "Reset complete, but some data may have failed to clear.", Toast.LENGTH_LONG).show()
                    recreate()
                 }
            }
        }
    }
}
