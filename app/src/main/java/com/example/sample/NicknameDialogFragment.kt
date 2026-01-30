package com.example.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NicknameDialogFragment : DialogFragment() {

    interface OnNicknameSavedListener {
        fun onNicknameSaved()
    }

    private var listener: OnNicknameSavedListener? = null

    private lateinit var mAuth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNicknameSavedListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnNicknameSavedListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_nickname, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        val saveButton: Button = view.findViewById(R.id.create_nickname_popup_button)
        val closeButton: ImageView = view.findViewById(R.id.nickname_popup_close_button)
        val nicknameLayout: TextInputLayout = view.findViewById(R.id.nickname_box)

        // THE FIX: The line that made the background transparent has been removed.
        // The background is now controlled by the XML layout file.

        saveButton.setOnClickListener {
            val nickname = nicknameLayout.editText?.text.toString().trim()
            if (nickname.isNotEmpty()) {
                saveNicknameToFirestore(nickname)
            } else {
                nicknameLayout.error = "Nickname is required"
            }
        }

        closeButton.setOnClickListener {
            mAuth.signOut()
            dismiss()
        }
    }

    private fun saveNicknameToFirestore(nickname: String) {
        val userId = mAuth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "User not authenticated. Please log in again.", Toast.LENGTH_LONG).show()
            dismiss()
            return
        }

        val userProfile = hashMapOf("nickname" to nickname)

        db.collection("users").document(userId).set(userProfile)
            .addOnSuccessListener { 
                listener?.onNicknameSaved()
                dismiss()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error saving nickname: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
