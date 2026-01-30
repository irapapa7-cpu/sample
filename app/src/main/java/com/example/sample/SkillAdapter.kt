package com.example.sample

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.models.Skill
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SkillAdapter(
    private val skills: List<Skill>,
    private val context: Context,
    private val unlockStatus: Map<String, Boolean>
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skill_item, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skills[position]
        holder.skillName.text = skill.name
        holder.skillDescription.text = skill.description

        val isUnlocked = unlockStatus[skill.name] ?: (position == 0)

        // Default state, will be updated by specific handlers
        holder.itemView.alpha = if (isUnlocked) 1.0f else 0.5f
        holder.starIcon.visibility = View.GONE
        holder.lockIcon.visibility = if (isUnlocked) View.GONE else View.VISIBLE

        if (skill.name == "Final Assessment") {
            handleFinalAssessment(holder, isUnlocked)
        } else {
            setupSkillClickListener(holder, skill, isUnlocked, position)
        }
    }

    private fun handleFinalAssessment(holder: SkillViewHolder, isUnlocked: Boolean) {
        if (!isUnlocked) {
            setupSkillClickListener(holder, skills.last(), false, skills.size - 1)
            return
        }

        val userId = mAuth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("assessmentStatus").document("Final Assessment").get()
            .addOnSuccessListener { document ->
                // Check holder binding to prevent race condition
                if (holder.adapterPosition != skills.indexOfFirst { it.name == "Final Assessment" }) return@addOnSuccessListener

                if (document.exists() && document.getBoolean("hasTakenAssessment") == true) {
                    // ASSESSMENT TAKEN: Show star, lock item
                    holder.itemView.alpha = 0.5f
                    holder.starIcon.visibility = View.VISIBLE
                    holder.lockIcon.visibility = View.GONE
                    holder.itemView.setOnClickListener {
                        Toast.makeText(context, "You have already completed the final assessment.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // ASSESSMENT NOT TAKEN: Set up normal click listener
                    setupSkillClickListener(holder, skills.last(), true, skills.size - 1)
                }
            }.addOnFailureListener {
                 if (holder.adapterPosition == skills.indexOfFirst { it.name == "Final Assessment" }) {
                    setupSkillClickListener(holder, skills.last(), true, skills.size - 1)
                 }
            }
    }

    private fun setupSkillClickListener(holder: SkillViewHolder, skill: Skill, isUnlocked: Boolean, position: Int) {
        if (isUnlocked) {
            holder.itemView.alpha = 1.0f
            holder.lockIcon.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent = if (skill.name == "Final Assessment") {
                    Intent(context, EndingDialogActivity::class.java)
                } else {
                    Intent(context, SkillActivity::class.java).apply {
                        putExtra("SKILL_NAME", skill.name)
                    }
                }
                context.startActivity(intent)
            }
        } else {
            holder.itemView.alpha = 0.5f
            holder.lockIcon.visibility = View.VISIBLE
            holder.itemView.setOnClickListener {
                val previousSkillName = skills.getOrNull(position - 1)?.name ?: "the previous topic"
                Toast.makeText(context, "Complete '$previousSkillName' to unlock.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = skills.size

    class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val skillName: TextView = itemView.findViewById(R.id.skill_name)
        val skillDescription: TextView = itemView.findViewById(R.id.skill_description)
        val lockIcon: ImageView = itemView.findViewById(R.id.lock_icon)
        val starIcon: ImageView = itemView.findViewById(R.id.star_icon)
    }
}
