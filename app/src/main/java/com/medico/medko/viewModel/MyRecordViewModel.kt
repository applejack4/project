package com.medico.medko.viewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.medico.medko.Model.DetailImageModel
import com.medico.medko.Model.Doctor
import com.medico.medko.Model.ImageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyRecordViewModel : ViewModel() {
    private var list : ArrayList<ImageModel> = arrayListOf()
    private var entireList : ArrayList<DetailImageModel> = arrayListOf()
    private var specificDoctorArrayList : ArrayList<Doctor> = arrayListOf()
    private var myDoctorList : ArrayList<Doctor> = arrayListOf()
    private var userRecordsHistory : ArrayList<ImageModel> = arrayListOf()
    private lateinit var userId : String
    private lateinit var speciality : String
    private lateinit var curretuserid : String
    private lateinit var doctorIntentId : String

    private val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var firebaseDatabase : DatabaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
    private var fieldReference : DatabaseReference =FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("Fields")


    private val imageList : MutableLiveData<ArrayList<ImageModel>> by lazy {
        MutableLiveData<ArrayList<ImageModel>>().also {
            val myRef = firebaseDatabase.child("Users").child(userId).child("Medical_Records").child(auth)
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        list.clear()
                        for(obj in snapshot.children){
                            val imageMode = obj.getValue(ImageModel::class.java)
                            if(imageMode != null){
                                list.add(imageMode)
                            }else{
                                return
                            }
                        }
                        if(list.size > 0){
                            imageList.value = list
                        }
                    }else{
                       list.clear()
                        imageList.value = list
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.message)
                }
            })
        }
    }


    private val detailImageModel : MutableLiveData<ArrayList<DetailImageModel>> by lazy {
        MutableLiveData<ArrayList<DetailImageModel>>().also {
            val myRef = firebaseDatabase.child("Users").child(userId).child("Medical_Records").child("Entire_Records")
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        entireList.clear()
                        for(obj in snapshot.children){
                            val imageMode = obj.getValue(DetailImageModel::class.java)
                            if(imageMode != null){
                                entireList.add(imageMode)
                            }else{
                                return
                            }
                        }
                        if(entireList.size > 0){
                            detailImageModel.value = entireList
                        }
                    }else{
                        entireList.clear()
                        detailImageModel.value = entireList
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.message)
                }
            })
        }
    }


    private val specificDoctorList : MutableLiveData<ArrayList<Doctor>> by lazy {
        MutableLiveData<ArrayList<Doctor>>().also {
            val myRef = fieldReference.child(speciality)
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        specificDoctorArrayList.clear()
                        for(obj in snapshot.children){
                            val imageMode = obj.getValue(Doctor::class.java)
                            if(imageMode != null){
                                specificDoctorArrayList.add(imageMode)
                            }else{
                                return println("This has to be printed if the values is null")
                            }
                        }
                        if(specificDoctorArrayList.size > 0){
                            specificDoctorList.value = specificDoctorArrayList
                            println("ArrayList size is not empty.")
                        }
                    }else{
                        specificDoctorArrayList.clear()
                        specificDoctorList.value = specificDoctorArrayList
                        println("snapshot Size of the cleared Arraylist is ${specificDoctorArrayList.size}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.message)
                }
            })
        }
    }


    private val myDoctors : MutableLiveData<ArrayList<Doctor>> by lazy {
        MutableLiveData<ArrayList<Doctor>>().also {
            val myRef = firebaseDatabase.child("Users").child(curretuserid).child("Visited_Doctors")
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        myDoctorList.clear()
                        for(obj in snapshot.children){
                            val imageMode = obj.getValue(Doctor::class.java)
                            if(imageMode != null){
                                myDoctorList.add(imageMode)
                                myDoctors.value = myDoctorList
                            }else{
                                return
                            }
                        }
                    }else{
                        return
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.message)
                }
            })
        }
    }


    private val userRecord : MutableLiveData<ArrayList<ImageModel>> by lazy {
        MutableLiveData<ArrayList<ImageModel>>().also {
            val myRef = firebaseDatabase.child("Users").child(auth).child("Medical_Records").child(doctorIntentId)
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        list.clear()
                        for(obj in snapshot.children){
                            val imageMode = obj.getValue(ImageModel::class.java)
                            if(imageMode != null){
                                userRecordsHistory.add(imageMode)
                            }else{
                                return
                            }
                        }
                        if(userRecordsHistory.size > 0){
                            userRecord.value = userRecordsHistory
                        }
                    }else{
                        userRecordsHistory.clear()
                        userRecord.value = userRecordsHistory
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.message)
                }
            })
        }
    }

    private val noRegisteredDoctors : MutableLiveData<String> by lazy {
        MutableLiveData<String>().also {
            noRegisteredDoctors.value = "No Doctors Available"
        }
    }

    fun getData(id : String) : LiveData<ArrayList<ImageModel>>{
        userId = id
        return imageList
    }

    fun entireData(id : String) : LiveData<ArrayList<DetailImageModel>>{
        userId = id
        return detailImageModel
    }

    fun specificDoctor(spec : String) : LiveData<ArrayList<Doctor>>{
        speciality = spec
        return specificDoctorList
    }

    fun myDoctors(CurrentUserid : String) : LiveData<ArrayList<Doctor>>{
        curretuserid = CurrentUserid
        return myDoctors
    }

    fun myDoctorsHistory(doctorId : String) : LiveData<ArrayList<ImageModel>>{
        doctorIntentId = doctorId
        return userRecord
    }


}