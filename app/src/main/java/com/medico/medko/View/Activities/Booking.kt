package com.medico.medko.View.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentResolver
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.medico.medko.Constants.AppConstants
import com.medico.medko.Model.AppointConstructor
import com.medico.medko.Model.Doctor
import com.medico.medko.Model.ReviewMode
import com.medico.medko.R
import com.medico.medko.View.Fragments.Qrcode
import com.medico.medko.View.Fragments.Review_fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.view.ViewGroup
import com.medico.medko.Model.Token
import com.medico.medko.databinding.ActivityBookingBinding


class Booking : AppCompatActivity() {

    private val review = Review_fragment()
    private val qrCode = Qrcode()

    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appointList : ArrayList<AppointConstructor>
    private lateinit var myId: String

    private lateinit var dialog : Dialog
    private lateinit var pendingdialog : Dialog
    private lateinit var cancelledDialog : Dialog
    private lateinit var _binding : ActivityBookingBinding
    private lateinit var hisId : String

    private var name : String ?= null
    private var image : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
        _binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        changeFragment(review)

        hisId = intent.getStringExtra("id_firebase").toString()

        myId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val view = View.inflate(this@Booking, R.layout.successfull, null)

        val builder = AlertDialog.Builder(this@Booking)
        builder.setView(view)

        firebaseDatabase.child("Users").child(myId).get().addOnSuccessListener {
            if(it.exists()){
                name = it.child("firstname").value.toString()
                image =it.child("profilePicture").value.toString()
            }
        }

        dialog = Dialog(this@Booking)
        dialog.setContentView(R.layout.successfull)

        pendingdialog = Dialog(this@Booking)
        pendingdialog.setContentView(R.layout.pending)

        cancelledDialog = Dialog(this@Booking)
        cancelledDialog.setContentView(R.layout.offlinedialog)

        _binding.Navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.Menu_QRcode -> changeFragment(qrCode)
                R.id.Menu_Review -> changeFragment(review)
            }
            true
        }

        val doctorName : String = intent.getStringExtra("Doctor_name").toString()
        val clinicName : String = intent.getStringExtra("Clinic_name").toString()
        val speciality : String = intent.getStringExtra("Speciality").toString()
        val profilePicture: String?
        profilePicture = intent.getStringExtra("profilePic").toString()
        val status : String = intent.getStringExtra("StatusToday").toString()

        _binding.BookingDoctorName.text = doctorName
        _binding.BookingClinicName.text = clinicName
        _binding.BookingSpeciality.text = speciality
        _binding.DoctorStatus.text = status
        Picasso.get().load(profilePicture)?.fit()?.centerInside()?.placeholder(R.drawable.ic_baseline_account_circle_24)
            ?.into(_binding.BookingProfileImage)

        appointList = ArrayList<AppointConstructor>()
        val doctorUserid = intent.getStringExtra("id").toString()

        firebaseDatabase.child("Doctor").child(doctorUserid).
        child("Online_Appointment").addValueEventListener(object  : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                appointList.clear()
                for(obj in snapshot.children){
                    val appoint : AppointConstructor? = obj.getValue(AppointConstructor::class.java)
                    if (appoint != null) {
                        appointList.add(appoint)
                    }
                }
                println(appointList + "This is the appointment list")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "ERROR"+ error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun changeFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FrameLayout_Container, fragment).commit()
    }

    @SuppressLint("SimpleDateFormat", "InflateParams")
    override fun onResume() {
        super.onResume()
        val tf = SimpleDateFormat("hh:mm:ss")
        val currentTime = tf.format(Date())
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
        val doctorUserid = intent.getStringExtra("id_firebase").toString()

        _binding.BookAppointment.setOnClickListener {
            firebaseDatabase.child("Doctor").child(doctorUserid).get().addOnSuccessListener {
                if(it.exists()){
                    when(it.child("appointmentStatus").value){
                        "1" ->{
                            val currentUserId = firebaseAuth.currentUser?.uid.toString()
                            val checkRef = firebaseDatabase.child("Doctor").child(doctorUserid).child("Online_Appointment").orderByChild("id")
                            checkRef.equalTo(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    when(snapshot){
                                        snapshot ->{
                                            if(snapshot.exists()){
                                                println("Yes it Exists.")
                                                pendingdialog.show()
                                            }
                                            if(!snapshot.exists()){
                                                firebaseDatabase.child("Users").child(currentUserId).get().addOnSuccessListener {
                                                        value ->
                                                    run {
                                                        if (value.exists()) {
                                                            val name = value.child("firstname").value.toString()
                                                            val phone = value.child("mobile").value.toString()
                                                            val id = value.child("id").value.toString()
                                                            val profilePic = value.child("profilePicture").value.toString()
                                                            val appointment  = AppointConstructor(id, name, phone, profilePic, currentTime)
                                                            firebaseDatabase.child("Doctor").child(doctorUserid).child("Online_Appointment").child(currentUserId).
                                                            setValue(appointment).addOnCompleteListener {
                                                                    task ->
                                                                run {
                                                                    if (task.isSuccessful) {
                                                                        saveToUsersVisitedDoctors(currentUserId, doctorUserid)
                                                                        dialog.show()
                                                                        getToken()
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

                                override fun onCancelled(error: DatabaseError) {
                                    Log.i("Error", "Error:"+ error.message)
                                }

                            })
                        }
                        "0" ->{
                            val currentUserId = firebaseAuth.currentUser?.uid.toString()
                            val checkRef = firebaseDatabase.child("Doctor").child(doctorUserid).child("Offline_Appointment").orderByChild("id")
                            checkRef.equalTo(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    when(snapshot){
                                        snapshot ->{
                                            if(snapshot.exists()){
                                                println("Yes it Exists.")
                                                pendingdialog.show()
                                            }
                                            if(!snapshot.exists()){
                                                println("No it Doesn't Exist.")
                                                firebaseDatabase.child("Users").child(currentUserId).get().addOnSuccessListener {
                                    value ->
                                run {
                                    if (value.exists()) {
                                        val name = value.child("firstname").value.toString()
                                        val phone = value.child("mobile").value.toString()
                                        val id = value.child("id").value.toString()
                                        val profilePic = value.child("profilePicture").value.toString()
                                        val appointment = AppointConstructor(id, name, phone, profilePic, currentTime)
                                        firebaseDatabase.child("Doctor").child(doctorUserid).child("Offline_Appointment").child(currentUserId).
                                        setValue(appointment).addOnCompleteListener {
                                                task ->
                                            run {
                                                if (task.isSuccessful) {
                                                    saveToUsersVisitedDoctors(currentUserId, doctorUserid)
                                                    cancelledDialog.show()
                                                    getToken()
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

                                override fun onCancelled(error: DatabaseError) {
                                    Log.i("Error", "Error:"+ error.message)
                                }
                            })
                        }
                    }
                }
            }
        }

        val builder = AlertDialog.Builder(this)
        val inflater : LayoutInflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.review_popup, null)
        val editText : TextInputEditText = dialogLayout.findViewById(R.id.TIE_POPUP_ET)
        val currentUser = firebaseAuth.currentUser?.uid.toString()


        _binding.Review.setOnClickListener {
            with(builder){
                setPositiveButton("Submit"){ dialog, _ ->
                    val result : String = editText.text.toString()
                    val reference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
                    when{
                        TextUtils.isEmpty(result) ->{
                            Toast.makeText(context, "You cannot submit empty query.", Toast.LENGTH_LONG).show()
                        }
                        else ->{
                            reference.child("Users").child(currentUser).get().addOnSuccessListener {
                                if(it.exists()){
                                    if(result.isNotEmpty()) {
                                        val name: String = it.child("firstname").value.toString()
                                        val id: String = it.child("id").value.toString()
                                        val image : String = it.child("profilePicture").value.toString()
                                        val reviewMode = ReviewMode(name, result, image, id)
                                        reference.child("Doctor").child(doctorUserid)
                                            .child("Review").child(id).setValue(reviewMode)
                                            .addOnSuccessListener {
                                                dialog.dismiss()
                                                Toast.makeText(context, "Your review has been Updated successfully.", Toast.LENGTH_LONG).show()
                                                getTokenToSendReview()
                                            }
                                    }
                                }
                            }
                        }
                    }
                }

                setNegativeButton("Cancel"){ dialog, _ ->
                    dialog.dismiss()
                }
                if (dialogLayout.parent != null) {
                    (dialogLayout.parent as ViewGroup).removeView(dialogLayout)
                }
                setView(dialogLayout).show()
            }
        }
    }

    private fun saveToUsersVisitedDoctors(currentUserId: String, doctorUserid: String) {
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
        val myRef = firebaseDatabase.child("Doctor").child(doctorUserid)
            myRef.get().addOnSuccessListener {
            val name : String = it.child("doctorName").value.toString()
            val profilePic : String = it.child("profilePicture").value.toString()
            val mobile : String = it.child("mobile").value.toString()
            val clinic : String = it.child("clinicName").value.toString()
            val speciality : String = it.child("speciality").value.toString()
                val id : String = it.child("id").value.toString()
            val doctor = Doctor(id, name, clinic, "null", mobile, speciality, "null", profilePic, "null", "null")

            val userRef = firebaseDatabase.child("Users").child(currentUserId)
                userRef.child("Visited_Doctors").child(doctorUserid).setValue(doctor).addOnSuccessListener {
                Log.i("Saved", "Saved to database.")
            }
        }
    }



    private fun getToken() {
        val databaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").
        getReference("AppUsers").child("Doctor")
            .child(hisId!!).child("Token")

        val list : ArrayList<Token> = arrayListOf()
        val friends: MutableList<String?> = ArrayList()
        val ref = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers").
        child("Doctor").child(hisId.toString()).child("Token")

        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friends.clear()
                if(dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        val friend : String = ds.child("token").value.toString()
                        friends.add(friend)
                    }
                }else{
                    return
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Booking, databaseError.message, Toast.LENGTH_LONG).show()
            }
        }

        ref.addListenerForSingleValueEvent(eventListener)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    list.clear()
                        for (obj in snapshot.children){
                            val token : Token? = obj.getValue(Token::class.java)
                            if(token != null){
                                list.add(token)
                            }else{
                                return
                            }
                        }

                    val to = JSONObject()
                    val data = JSONObject()

                        data.put("hisId", myId)
                        data.put("title", name)
                        data.put("message", "Booked an appointment")

                    to.put("data", data)
                    for (tokens in friends){
                        to.put("to", tokens)
                        println(tokens)
                        sendNotification(to)
                    }
                }else{
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Booking, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getTokenToSendReview(){
        val databaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").
        getReference("AppUsers").child("Doctor")
            .child(hisId!!).child("Token")

        val list : ArrayList<Token> = arrayListOf()
        val friends: MutableList<String?> = ArrayList()
        val ref = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers").
        child("Doctor").child(hisId.toString()).child("Token")

        val eventListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                friends.clear()
                if(dataSnapshot.exists()){
                    for (ds in dataSnapshot.children) {
                        val friend : String = ds.child("token").value.toString()
                        friends.add(friend)
                    }
                }else{
                    println("This has no snapshot so it gets null.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@Booking, databaseError.message, Toast.LENGTH_LONG).show()
            }
        }

        ref.addListenerForSingleValueEvent(eventListener)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    list.clear()
                    for (obj in snapshot.children){
                        val token : Token? = obj.getValue(Token::class.java)
                        if(token != null){
                            list.add(token)
                        }else{
                            return
                        }
                    }

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("hisId", myId)
                    data.put("title", name)
                    data.put("message", "Added a Review")

                    to.put("data", data)
                    for (tokens in friends){
                        to.put("to", tokens)
                        println(tokens)
                        sendNotification(to)
                    }
                }else{
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Booking, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun sendNotification(to: JSONObject) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->

                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {

                Log.d("TAG", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }


}