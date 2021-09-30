package com.example.project.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.project.Model.DetailImageModel
import com.example.project.Model.QrCode
import com.example.project.Model.ReviewMode
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.FieldPosition

class ReviewViewModel : ViewModel(){
    private val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var firebaseDatabase : DatabaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
    var list : ArrayList<ReviewMode> = arrayListOf()
    var customList : ArrayList<ReviewMode> = arrayListOf()
    private lateinit var userId : String

    private val reviewList : MutableLiveData<ArrayList<ReviewMode>> by lazy {
        MutableLiveData<ArrayList<ReviewMode>>().also {
            val myRef = firebaseDatabase.child("Doctor").child(auth).child("Review")
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        list.clear()
                        for (obj in snapshot.children){
                            val review = obj.getValue(ReviewMode::class.java)
                            if(review != null){
                                list.add(review)
                            }else{
                                return
                            }
                        }
                        if(list.size > 0){
                            reviewList.value = list
                        }
                    }else{
                        list.clear()
                        reviewList.value = list
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.message)
                }

            })
        }
    }


    private val reviewCustom :MutableLiveData<ArrayList<ReviewMode>> by lazy {
        MutableLiveData<ArrayList<ReviewMode>>().also {
            val myRef = firebaseDatabase.child("Doctor").child(userId).child("Review")
            myRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        customList.clear()
                        for (obj in snapshot.children){
                            val review = obj.getValue(ReviewMode::class.java)
                            if(review != null){
                                customList.add(review)
                            }else{
                                return
                            }
                        }
                        if(customList.size > 0){
                            reviewCustom.value = customList
                        }
                    }else{
                        customList.clear()
                        reviewCustom.value = customList
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("Error", error.message)
                }

            })
        }
    }





    fun getReviews() : LiveData<ArrayList<ReviewMode>>{
        return reviewList
    }

    fun bookingReview(id : String) : LiveData<ArrayList<ReviewMode>>{
        userId = id
        return reviewCustom
    }

    fun deleteReview(position: Int){
        list.removeAt(position)
    }
}