package com.medico.medko.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.medico.medko.Model.Token

class NotificationsViewModel : ViewModel() {
    private val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var firebaseDatabase : DatabaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
    var  list : ArrayList<Token> = arrayListOf()

//    private val tokenList : MutableLiveData<ArrayList<Token>> by lazy {
//        MutableLiveData<ArrayList<Token>>().also {
//            val ref = firebaseDatabase.child("Doctor").child(auth).child("Token")
//            ref.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()){
//                        list.clear()
//                        for(obj in snapshot.children){
//                            val token = obj.getValue(Token::class.java)
//                            if(token != null){
//                                list.add(token)
//                            }else{
//                                return
//                            }
//                        }
//                        if(list.size > 0){
//                            tokenList.value = list
//                        }
//                    }else{
//                        list.clear()
//                        tokenList.value = list
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.i("TAG","Error"+error.message)
//                }
//            })
//        }
//    }


    fun deleteToken(item : Int){
        list.removeAt(item)
    }

//    fun getTitleHistory() : LiveData<ArrayList<Token>>{
////        return tokenList
//    }
}