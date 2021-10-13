package com.medico.medko.View.Activities


import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.medico.medko.Model.AllUsers
import com.medico.medko.Model.User
import com.medico.medko.databinding.ActivityUserRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class UserRegistration : AppCompatActivity() {
    private lateinit var binding : ActivityUserRegistrationBinding
    private lateinit var appUsers : DatabaseReference
    private lateinit var allUsers : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appUsers = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers").child("Users")
        allUsers = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AllUsers")

        val progress = ProgressDialog(this@UserRegistration)
        progress.setMessage("Registering")
        progress.setCancelable(false)

        binding.BtnRegister.setOnClickListener{
            val fullName = binding.TIEUsername.text.toString().trim(){ it < ' '}
            val eMail = binding.TIEEmail.text.toString().trim(){ it < ' '}
            val mobile = binding.TIEMobile.text.toString().trim(){ it < ' '}
            val password = binding.TIEPassword.text.toString().trim(){ it < ' '}
            val confirmPassword = binding.TIEConfirmPassword.text.toString().trim(){ it < ' '}
            auth = FirebaseAuth.getInstance()

            when{
                TextUtils.isEmpty(fullName) -> {
                    binding.TIEUsername.error = "Username is Required"
                }
                TextUtils.isEmpty(eMail) -> {
                    binding.TIEEmail.error = "Email is required"
                }
                TextUtils.isEmpty(mobile) -> {
                    binding.TIEMobile.error = "Mobile number is Required"
                }
                TextUtils.isEmpty(password) -> {
                    binding.TIEPassword.error = "Password is Required"
                }
                TextUtils.isEmpty(confirmPassword) -> {
                    binding.TIEConfirmPassword.error = "Confirm password is Required"
                }
                else ->{
                    if(password == confirmPassword){
                        auth.createUserWithEmailAndPassword(eMail, password).addOnCompleteListener {
                            task ->
                            run {
                                if (task.isSuccessful) {
                                    progress.show()
                                    val id = auth.currentUser!!.uid
                                    val user = User(id, fullName, eMail, mobile, password, "null")
                                    val allUser = AllUsers(id, "0")
                                    appUsers.child(id).setValue(user).addOnCompleteListener {
                                            it ->
                                        if(it.isSuccessful){
                                            allUsers.child(id).setValue(allUser).addOnCompleteListener {
                                                if(it.isSuccessful){
                                                    progress.dismiss()
                                                    functionIntent()
                                                }
                                            }
                                        }else{
                                            Toast.makeText(this, it.exception?.message, Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }else{
                                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(this@UserRegistration, "Passwords are not matching",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun functionIntent() {
        val loginIntent = Intent(this@UserRegistration, MainActivity::class.java)
        startActivity(loginIntent)
        finish()
    }
}