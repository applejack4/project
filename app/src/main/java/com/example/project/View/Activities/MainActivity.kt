package com.example.project.View.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.project.R
import com.example.project.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.HashMap

 class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var allUsersDefaultValue : DatabaseReference
    private lateinit var currentUse : String

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        currentUse = auth.currentUser?.uid.toString()
        allUsersDefaultValue = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AllUsers")

        binding.BtnDoctorRegister.setOnClickListener {
            val docIntent = Intent(this@MainActivity, DoctorRegistration::class.java)
            startActivity(docIntent)
        }

        binding.BtnUserRegister.setOnClickListener {
            val userIntent = Intent(this@MainActivity, UserRegistration::class.java)
            startActivity(userIntent)
        }

        binding.BtnLogin.setOnClickListener {
            val email = binding.TIEEmail.text.toString().trim() { it < ' ' }
            val password = binding.TIEPassword.text.toString().trim(){ it < ' '}

            when {
                TextUtils.isEmpty(email) ->{
                    binding.TIEEmail.error = "Email is Required"
                }
                TextUtils.isEmpty(password) ->{
                    binding.TIEPassword.error = "Password is Required"
                }
                else ->{
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        task ->
                        run {
                            if (task.isSuccessful) {
                                val currentUser = auth.currentUser!!.uid
                                allUsersDefaultValue.child(currentUser).get().addOnSuccessListener {
                                    if(it.exists()){
                                        val defaultNumber = it.child("default_number").value
                                        if (defaultNumber != null) {
                                            if(defaultNumber == "1"){
                                                doctorIntent()
                                            }else{
                                                userIntent()
                                            }
                                        }
                                    }
                                }
                            }else{
                                Toast.makeText(this@MainActivity, task.exception?.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }

        binding.ForgotPasswordUnderlined.setOnClickListener{
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }

   fun userIntent(){
       finish()
       val loginIntent = Intent(this@MainActivity, MainUserPage::class.java)
       startActivity(loginIntent)
   }

    fun doctorIntent(){
        finish()
        val docLogin = Intent(this@MainActivity, DoctorMainPage::class.java)
        startActivity(docLogin)
    }


}