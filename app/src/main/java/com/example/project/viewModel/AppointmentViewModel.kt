package com.example.project.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project.Model.AppointConstructor
import com.example.project.Model.Doctor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AppointmentViewModel : ViewModel() {
    private val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private var firebaseDatabase : DatabaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")

    var list : ArrayList<AppointConstructor> = arrayListOf()
    var offlineList : ArrayList<AppointConstructor> = arrayListOf()

    private val appointList : MutableLiveData<ArrayList<AppointConstructor>> by lazy {
        MutableLiveData<ArrayList<AppointConstructor>>().also {
            val myRef = firebaseDatabase.child("Doctor").child(auth).child("Online_Appointment")
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        list.clear()
                        for (obj in snapshot.children) {
                            val appoint = obj.getValue(AppointConstructor::class.java)
                            if (appoint != null) {
                                list.add(appoint)
                            }else{
                                return
                            }
                        }
                        if(list.size > 0){
                            appointList.value = list
                        }
                    }else{
                        list.clear()
                        appointList.value = list
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.i("TAG","Error"+error.message)
                }
            })

        }
    }


    private val appointListOffline : MutableLiveData<ArrayList<AppointConstructor>> by lazy {
        MutableLiveData<ArrayList<AppointConstructor>>().also {
            val myRef = firebaseDatabase.child("Doctor").child(auth).child("Offline_Appointment")
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()) {
                        offlineList.clear()
                        for (obj in snapshot.children) {
                            val appoint = obj.getValue(AppointConstructor::class.java)
                            if (appoint != null) {
                                offlineList.add(appoint)
                            }else{
                               return
                            }
                        }
                        if(offlineList.size > 0){
                            appointListOffline.value = offlineList
                        }
                    }else{
                        offlineList.clear()
                        appointListOffline.value = offlineList
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.i("TAG","Error"+error.message)
                }
            })

        }
    }

    private val _text = MutableLiveData<String>().apply {
        value = "You have no Online Appointments"
    }


    private val noAppointmentsOffline : MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            noAppointmentsOffline.value = "You have no offline Appointments"
        }
    }

    private val noAppointmentsOnline : MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            noAppointmentsOffline.value = "You have no online Appointments"
        }
    }

    fun getUsers() : LiveData<ArrayList<AppointConstructor>>{
        return appointList
    }

    fun getOfflineUsers() : LiveData<ArrayList<AppointConstructor>>{
        return appointListOffline
    }

    fun deleteUser(position : Int) {
        list.removeAt(position)
    }

    fun deleteOfflineUser(position: Int){
        offlineList.removeAt(position)
    }

    fun getOnlineApps() : LiveData<String> {
        return noAppointmentsOnline
    }

    fun getOfflineApps() : LiveData<String>{
        return noAppointmentsOffline
    }

}