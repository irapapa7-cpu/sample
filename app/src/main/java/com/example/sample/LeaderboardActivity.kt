package com.example.sample

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var leaderboardRecyclerView: RecyclerView
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        leaderboardRecyclerView = findViewById(R.id.leaderboard_recycler_view)
        leaderboardRecyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.back_button).setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        refreshLeaderboard()
    }

    private fun refreshLeaderboard() {
        calculateLeaderboardData { leaderboardData ->
            leaderboardRecyclerView.adapter = LeaderboardAdapter(leaderboardData)
        }
    }

    private fun calculateLeaderboardData(onComplete: (List<Pair<String, Int>>) -> Unit) {
        val leaderboard = mutableListOf<Pair<String, Int>>()
        val usersCollection = db.collection("users")

        // 1. Get all users from the 'users' collection.
        usersCollection.get().addOnSuccessListener { usersSnapshot ->
            if (usersSnapshot.isEmpty) {
                onComplete(emptyList()) // No users, nothing to show
                return@addOnSuccessListener
            }

            val userCount = usersSnapshot.size()
            var usersProcessed = 0

            // 2. For each user, get their nickname and calculate their score.
            for (userDoc in usersSnapshot.documents) {
                val userId = userDoc.id
                val nickname = userDoc.getString("nickname")

                if (nickname != null) {
                    // 3. Get the 'progress' subcollection for this specific user.
                    usersCollection.document(userId).collection("progress")
                        .get()
                        .addOnSuccessListener { progressSnapshot ->
                            var score = 0
                            for (progressDoc in progressSnapshot) {
                                if (progressDoc.getBoolean("passed") == true) {
                                    score++
                                }
                            }
                            leaderboard.add(Pair(nickname, score))

                            usersProcessed++
                            // 4. When all users are processed, sort and display the list.
                            if (usersProcessed == userCount) {
                                onComplete(leaderboard.sortedByDescending { it.second })
                            }
                        }
                        .addOnFailureListener { 
                            // If we can't get progress for one user, treat their score as 0
                            leaderboard.add(Pair(nickname, 0))
                            usersProcessed++
                            if (usersProcessed == userCount) {
                                onComplete(leaderboard.sortedByDescending { it.second })
                            }
                        }
                } else {
                    // Nickname is null, so skip but count as processed.
                     usersProcessed++
                     if (usersProcessed == userCount) {
                        onComplete(leaderboard.sortedByDescending { it.second })
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error getting leaderboard: ${exception.message}", Toast.LENGTH_LONG).show()
            onComplete(emptyList()) // Return an empty list on failure
        }
    }
}
