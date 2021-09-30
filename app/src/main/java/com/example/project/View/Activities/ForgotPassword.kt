package com.example.project.View.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.project.R
import com.example.project.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var _binding : ActivityForgotPasswordBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    override fun onResume() {
        super.onResume()

        auth = FirebaseAuth.getInstance()

        val cred = FirebaseAuth.getInstance().currentUser

        val forgottenEmail : String = _binding.TIEEmail.text.toString()

        _binding.SubmitEmail.setOnClickListener {
            when{
                TextUtils.isEmpty(forgottenEmail) ->{
                    _binding.TIEEmail.error = "Email is Required"
                } else ->{
                    auth.sendPasswordResetEmail(forgottenEmail).addOnCompleteListener {
                        task ->
                        run {
                            if (task.isSuccessful) {
                                Toast.makeText(applicationContext, "Check your email :$forgottenEmail", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@ForgotPassword, MainActivity::class.java))
                                finish()
                            }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(applicationContext, "Error : ${it.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}