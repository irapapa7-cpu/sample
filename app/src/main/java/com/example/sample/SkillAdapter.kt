package com.example.sample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.models.Skill

class SkillAdapter(
    private val skills: List<Skill>,
    private val onSkillClick: (Skill) -> Unit
) : RecyclerView.Adapter<SkillAdapter.SkillViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skill_item, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val skill = skills[position]
        holder.bind(skill, onSkillClick)
    }

    override fun getItemCount() = skills.size

    class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.skill_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.skill_description)
        private val lockIcon: ImageView = itemView.findViewById(R.id.lock_icon)

        fun bind(skill: Skill, onSkillClick: (Skill) -> Unit) {
            nameTextView.text = skill.name
            descriptionTextView.text = skill.description

            if (skill.isUnlocked) {
                lockIcon.visibility = View.GONE
                itemView.alpha = 1.0f
                itemView.setOnClickListener { onSkillClick(skill) }
            } else {
                lockIcon.visibility = View.VISIBLE
                itemView.alpha = 0.5f
                itemView.setOnClickListener { Toast.makeText(itemView.context, "Skill locked!", Toast.LENGTH_SHORT).show() }
            }
        }
    }
}
