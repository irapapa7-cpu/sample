package com.example.sample

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.example.sample.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class TopicSummaryAdapter(
    private val context: Context,
    private val answeredQuestions: List<Pair<Int, Question>>,
    private val skillName: String
) : RecyclerView.Adapter<TopicSummaryAdapter.ViewHolder>() {

    private val mAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.summary_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (originalIndex, question) = answeredQuestions[position]
        holder.questionText.text = "${originalIndex + 1}. ${question.text}"

        if (question.imageResId != null) {
            holder.questionImage.visibility = View.VISIBLE
            holder.questionImage.setImageResource(question.imageResId)
        } else {
            holder.questionImage.visibility = View.GONE
        }

        val userId = mAuth.currentUser?.uid ?: return
        db.collection("users").document(userId).collection("progress").document("${skillName}_${originalIndex + 1}").get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    holder.userAnswerText.text = "Your Answer: Not Attempted"
                    holder.correctAnswerText.visibility = View.GONE
                    holder.explanationText.visibility = View.GONE
                    return@addOnSuccessListener
                }

                val isPassed = document.getBoolean("passed") ?: false
                val attempts = document.getLong("attempts")?.toInt() ?: 0
                val wrongAnswers = (document.get("wrongAnswers") as? List<Long>)?.map { it.toInt() }?.toSet() ?: emptySet()

                // Display User's Answer(s)
                if (isPassed) {
                    val summaryText = SpannableStringBuilder()
                    if (wrongAnswers.isNotEmpty()) {
                        val wrongAttemptText = wrongAnswers.mapNotNull { question.options.getOrNull(it) }.joinToString(", ")
                        summaryText.bold { color(ContextCompat.getColor(context, R.color.red)) { append("Your first attempt: $wrongAttemptText\n") } }
                    }
                    summaryText.bold { color(ContextCompat.getColor(context, R.color.green)) { append("Your final answer: ${question.options[question.correctAnswerIndex]}") } }
                    holder.userAnswerText.text = summaryText
                } else {
                    val userAttemptsText = wrongAnswers.mapNotNull { question.options.getOrNull(it) }.joinToString("\n") { "- $it" }
                    holder.userAnswerText.text = "Your Attempts:\n$userAttemptsText"
                    holder.userAnswerText.setTextColor(ContextCompat.getColor(context, R.color.red))
                }

                // Display Correct Answer and Explanation if applicable
                if (isPassed || attempts >= 2) {
                    holder.correctAnswerText.visibility = View.VISIBLE
                    holder.correctAnswerText.text = "Correct Answer: ${question.options[question.correctAnswerIndex]}"
                    holder.explanationText.visibility = View.VISIBLE
                    holder.explanationText.text = "Explanation: ${question.explanation}"
                } else {
                    holder.correctAnswerText.visibility = View.GONE
                    holder.explanationText.visibility = View.GONE
                }

            }
    }

    override fun getItemCount() = answeredQuestions.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionText: TextView = itemView.findViewById(R.id.summary_question_text)
        val questionImage: ImageView = itemView.findViewById(R.id.summary_question_image)
        val userAnswerText: TextView = itemView.findViewById(R.id.summary_user_answer_text)
        val correctAnswerText: TextView = itemView.findViewById(R.id.summary_correct_answer_text)
        val explanationText: TextView = itemView.findViewById(R.id.summary_explanation_text)
    }
}
