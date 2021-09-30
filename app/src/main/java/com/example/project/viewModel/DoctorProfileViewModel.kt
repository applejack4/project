package com.example.project.viewModel

import androidx.lifecycle.ViewModel
import com.example.project.Model.AppointConstructor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DoctorProfileViewModel : ViewModel() {

    private val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    var list : ArrayList<AppointConstructor> = arrayListOf()


}