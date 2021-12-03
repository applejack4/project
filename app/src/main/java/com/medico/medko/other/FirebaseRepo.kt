package com.medico.medko.other

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.medico.medko.Model.AppointConstructor
import com.medico.medko.Model.ParentOnlyDate

public class FirebaseRepo(OnRealTimeDbTaskComplete: Any?) {

    private var databaseReference : DatabaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com")
        .getReference("AppUsers").child("Doctor").child("History")

    val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private lateinit var  onRealTimeTaskComplete : OnRealTimeDbTaskComplete

    fun getAllUsers(){
        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val parentItemList : ArrayList<ParentOnlyDate> = ArrayList<ParentOnlyDate>()
                if (snapshot.exists()){
                    for (obj in snapshot.children){
                        val addItems = obj.getValue(ParentOnlyDate::class.java)
                        addItems?.date  = snapshot.child("date").value.toString()
                        val ti = object : GenericTypeIndicator<ArrayList<AppointConstructor>>(){}
                        addItems?.data = snapshot.child("data").getValue(ti)!!
                        if (addItems != null) {
                            parentItemList.add(addItems)
                        }
                        onRealTimeTaskComplete.onSuccess(parentItemList)
                    }
                }else{
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onRealTimeTaskComplete.onFailure(error)
            }

        })
    }

     interface OnRealTimeDbTaskComplete{
        fun onSuccess(parentItem: ArrayList<ParentOnlyDate>)
        fun onFailure(error : DatabaseError)
    }
}