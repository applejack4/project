package com.medico.medko.viewModel

import androidx.lifecycle.ViewModel
import com.medico.medko.Model.AppointConstructor
import com.google.firebase.auth.FirebaseAuth

class DoctorProfileViewModel : ViewModel() {

    private val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    var list : ArrayList<AppointConstructor> = arrayListOf()


}