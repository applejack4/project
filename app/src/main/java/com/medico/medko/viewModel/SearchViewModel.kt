package com.medico.medko.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.medico.medko.Model.Doctor
import com.google.firebase.database.*

class SearchViewModel : ViewModel() {

    private lateinit var docList : ArrayList<Doctor>

    private val _text = MutableLiveData<String>().apply {
        value = "This is search Fragment"
    }
    val text: LiveData<String> = _text

    private val _doctorData = MutableLiveData<ArrayList<Doctor>>().apply {
        docList = ArrayList<Doctor>()
        val refUsers = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/")
            .getReference("AppUsers").child("Doctor")
        refUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                docList.clear()
                if(snapshot.exists()){
                    for (obj in snapshot.children){
                        val doctor : Doctor? = obj.getValue(Doctor::class.java)
                        if (doctor != null) {
                            docList.add(doctor)
                        }
                    }
                    if(docList.size > 0){
                        value = docList
                    }
                }else{
                    docList.clear()
                    value = docList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    val doctorArrayList : LiveData<ArrayList<Doctor>> = _doctorData
}



