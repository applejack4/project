package com.medico.medko.View.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.medico.medko.databinding.ActivityChangePasswordBinding
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChangePassword : AppCompatActivity() {

    private lateinit var _binding : ActivityChangePasswordBinding
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var firebase : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var credential: AuthCredential

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        setContentView(_binding.root)

        val cred = FirebaseAuth.getInstance().currentUser
        val currentUser : String = auth.currentUser?.uid.toString()
        firebase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AllUsers").child(currentUser)
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")

        _binding.BtnSubmitPasswordChange.setOnClickListener {
            firebase.get().addOnSuccessListener {
                when(it.child("default_number").value){
                    "1" ->{
                        val myRef = firebaseDatabase.child("Doctor").child(currentUser)
                        myRef.get().addOnSuccessListener {
                            val firebasePassword : String = it.child("password").value.toString()
                            val oldPassword : String = _binding.TIOldPass.text.toString()
                            val newPassword : String = _binding.TINewPass.text.toString()
                            val confirmNewPassword : String = _binding.TIConfirmNewPass.text.toString()
                            when{
                                TextUtils.isEmpty(oldPassword) ->{
                                    _binding.TIOldPass.error = "Please fill this field."
                                }
                                TextUtils.isEmpty(newPassword) ->{
                                    _binding.TINewPass.error = "Please fill this field."
                                }
                                TextUtils.isEmpty(confirmNewPassword) ->{
                                    _binding.TIConfirmNewPass.error = "Please fill this field"
                                }
                                else ->{
                                    if(firebasePassword != oldPassword){
                                        Toast.makeText(this, "Entered old password doesn't match with actual old password.", Toast.LENGTH_LONG).show()
                                    }else if(newPassword != confirmNewPassword){
                                        Toast.makeText(this, "New password doesn't match Confirm password.", Toast.LENGTH_LONG).show()
                                    }else if(firebasePassword == oldPassword){
                                        cred?.updatePassword(newPassword)?.addOnSuccessListener {
                                            firebaseDatabase.child("Doctor").child(currentUser).child("password").setValue(newPassword).addOnSuccessListener {
                                                startActivity(Intent(this, DoctorMainPage::class.java))
                                                Toast.makeText(this, "Password Updated Successfully", Toast.LENGTH_LONG).show()
                                                finish()
                                            }
                                        }?.addOnFailureListener {
                                          Toast.makeText(applicationContext, "Error:${it.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }else{
                                        onBackPressed()
                                    }
                                }
                            }
                        }
                    }

                    "0" ->{
                        val myRef = firebaseDatabase.child("Users").child(currentUser)
                        myRef.get().addOnSuccessListener {
                            val firebasePassword : String = it.child("password").value.toString()
                            val oldPassword : String = _binding.TIOldPass.text.toString()
                            val newPassword : String = _binding.TINewPass.text.toString()
                            val confirmNewPassword : String = _binding.TIConfirmNewPass.text.toString()

                            when{
                                TextUtils.isEmpty(oldPassword) ->{
                                    _binding.TIOldPass.error = "Please fill this field."
                                }
                                TextUtils.isEmpty(newPassword) ->{
                                    _binding.TINewPass.error = "Please fill this field."
                                }
                                TextUtils.isEmpty(confirmNewPassword) ->{
                                    _binding.TIConfirmNewPass.error = "Please fill this field"
                                }
                                else ->{
                                    if(firebasePassword != oldPassword){
                                        Toast.makeText(this, "Entered old password doesn't match with actual old password.", Toast.LENGTH_LONG).show()
                                    }else if(newPassword != confirmNewPassword){
                                        Toast.makeText(this, "New password doesn't match Confirm password.", Toast.LENGTH_LONG).show()
                                    }else if(firebasePassword == oldPassword){
                                        cred?.updatePassword(newPassword)?.addOnSuccessListener {
                                            firebaseDatabase.child("Users").child(currentUser).child("password").setValue(newPassword).addOnSuccessListener {
                                                startActivity(Intent(this, DoctorMainPage::class.java))
                                                Toast.makeText(this, "Password Updated Successfully", Toast.LENGTH_LONG).show()
                                                finish()
                                            }
                                        }?.addOnFailureListener {
                                            Toast.makeText(applicationContext, "Error:${it.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}