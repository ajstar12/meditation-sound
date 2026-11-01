package com.arjun.meso

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.arjun.meso.databinding.ActivityFeedbackBinding
import com.arjun.meso.utils.Constant
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class FeedbackActivity : AppCompatActivity() {
    lateinit var binding: ActivityFeedbackBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inIt()
        setClick()
    }

    private fun inIt() {

    }

    private fun setClick() {
        binding.btnSubmit.setOnClickListener{
            if (binding.edtFeedback.text.toString().isEmpty()){
                binding.edtFeedback.setError("Please add feedback")
            }else{
                saveFeedback()
            }
        }
    }

    fun saveFeedback(){
        val db = Firebase.firestore
        val user = hashMapOf(
            "email" to binding.edtEmail.text.toString(),
            "feedback" to binding.edtFeedback.text.toString(),
            "dateTime" to Constant.getCurrentDate("yyyy-MM-dd H:mm:ss")
        )

        db.collection("Feedback")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.e("Arjun_Test", "Arjun_Test DocumentSnapshot added with ID: ${documentReference.id}")
                binding.edtEmail.setText("")
                binding.edtFeedback.setText("")
                Toast.makeText(this, "Thank you for your feedback. Your feedback matters to us.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Arjun_Test", "Arjun_Test Error adding document", e)
                Toast.makeText(this, "Failed to submit, Please try again.", Toast.LENGTH_SHORT).show()
            }
    }
}