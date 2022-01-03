package com.medico.medko.View.Activities

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.Model.AllUsers
import com.medico.medko.Model.Doctor
import com.medico.medko.View.Adapters.CustomItemListAdapter
import com.medico.medko.databinding.ActivityDoctorRegistrationBinding
import com.medico.medko.databinding.DialogCustomListBinding
import com.medico.medko.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class DoctorRegistration : AppCompatActivity() {
    private lateinit var binding : ActivityDoctorRegistrationBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var appUsers : DatabaseReference
    private lateinit var allUsers : DatabaseReference
    private lateinit var fields : DatabaseReference
    private lateinit var customListDialog: Dialog
    private lateinit var token : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val progress = ProgressDialog(this@DoctorRegistration)
        progress.setMessage("Registering")
        progress.setCancelable(false)

        binding = ActivityDoctorRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        appUsers = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers").child("Doctor")
        allUsers = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AllUsers")
        fields = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("Fields")
        binding.TIESpeciality.setOnClickListener {
            customItemListDialog("Select speciality", Constants.speciality(), Constants.speciality)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                token =  it.result.toString()
                println("Token is $token")
            }
        }.addOnFailureListener {
            println("Failed Message:${it.message}")
        }

        binding.BtnDoctorRegistration.setOnClickListener {
            val fullName = binding.TIEDoctorFullName.text.toString().trim(){ it < ' '}
            val clinicName = binding.TIEClinicName.text.toString().trim(){ it < ' '}
            val email = binding.TIEEmail.text.toString().trim(){ it < ' '}
            val mobile = binding.TIEPhone.text.toString().trim(){ it < ' '}
            val speciality = binding.TIESpeciality.text.toString().trim(){ it < ' '}
            val password = binding.TIEPassword.text.toString().trim(){ it < ' '}
            val confirmPassword = binding.TIEConfirmPassword.text.toString().trim() { it < ' '}
            when{
                TextUtils.isEmpty(fullName) -> {
                    binding.TIEDoctorFullName.error = "Doctor Name id required"
                }
                TextUtils.isEmpty(clinicName) -> {
                    binding.TIEClinicName.error  ="Clinic Name is required"
                }
                TextUtils.isEmpty(email) -> {
                    binding.TIEEmail.error = "Email is required"
                }
                TextUtils.isEmpty(mobile) -> {
                    binding.TIEPhone.error = "Phone is required"
                }
                TextUtils.isEmpty(speciality) -> {
                    if(speciality == "Select speciality"){
                        binding.TIESpeciality.error = "Speciality is required"
                    }
                }
                TextUtils.isEmpty(password) -> {
                    binding.TIEPassword.error = "Password is required"
                }
                TextUtils.isEmpty(confirmPassword) -> {
                    binding.TIEConfirmPassword.error = "Confirm password is required"
                }
                else ->{
                    if(password == confirmPassword){
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                                task ->
                            run {
                                if (task.isSuccessful) {
                                    progress.show()
                                    val id = auth.currentUser!!.uid
                                    val doctor = Doctor(id, fullName, clinicName, email, mobile, speciality, password,"null",  "0", "Available", "null")
                                    val all_Users = AllUsers(id, "1")
                                    fields.child(speciality).child(id).setValue(doctor)
                                    appUsers.child(id).setValue(doctor).addOnCompleteListener {
                                        if (it.isSuccessful) {
                                            allUsers.child(id).setValue(all_Users).addOnCompleteListener {
                                                    task1 ->
                                                run {
                                                    if (task1.isSuccessful) {
                                                        progress.dismiss()
                                                        functionIntent()
                                                    }else{
                                                        progress.dismiss()
                                                        Toast.makeText(this@DoctorRegistration,
                                                            task1.exception?.message,Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        }else{
                                            Toast.makeText(this@DoctorRegistration,
                                                it.exception?.message,Toast.LENGTH_LONG).show()
                                        }
                                    }
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this@DoctorRegistration, it.message,Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(this@DoctorRegistration, "Passwords are not matching",Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    private fun functionIntent() {
        val loginIntent = Intent(this@DoctorRegistration, MainActivity::class.java)
        startActivity(loginIntent)
        finish()
    }

    private fun customItemListDialog(title : String, itemList : List<String>, selection : String ){
        customListDialog = Dialog(this)
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        customListDialog.setContentView(binding.root)
        binding.tvDialogList.text = title

        binding.rvListDialog.layoutManager = LinearLayoutManager(this)
        val adapter = CustomItemListAdapter(this, itemList, selection)
        binding.rvListDialog.adapter = adapter
        customListDialog.show()
    }

    fun selectedListItem(item : String, selection : String){
        when(selection){
            Constants.speciality ->{
                customListDialog.dismiss()
                binding.TIESpeciality.setText(item)
            }
            else ->{
                customListDialog.dismiss()
            }
        }
    }
}

