package com.example.project.View.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.project.Constants.AppConstants
import com.example.project.Model.AppointConstructor
import com.example.project.Model.Doctor
import com.example.project.Model.ReviewMode
import com.example.project.R
import com.example.project.View.Fragments.Qrcode
import com.example.project.View.Fragments.Review_fragment
import com.example.project.databinding.ActivityBookingBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.view.ViewGroup




class Booking : AppCompatActivity() {

    private val review = Review_fragment()
    private val qrCode = Qrcode()

    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var appointList : ArrayList<AppointConstructor>
    private lateinit var myId: String

    private lateinit var dialog : Dialog
    private lateinit var pendingdialog : Dialog
    private lateinit var _binding : ActivityBookingBinding
    private var hisId : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
        _binding = ActivityBookingBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        changeFragment(review)

        hisId = intent.getStringExtra("id_firebase").toString()
        println("His id : $hisId")

        myId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val view = View.inflate(this@Booking, R.layout.successfull, null)

        val builder = AlertDialog.Builder(this@Booking)
        builder.setView(view)


        dialog = Dialog(this@Booking)
        dialog.setContentView(R.layout.successfull)


        pendingdialog = Dialog(this@Booking)
        pendingdialog.setContentView(R.layout.pending)

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
        Picasso.get().load(profilePicture).placeholder(R.drawable.ic_baseline_account_circle_24)
            .into(_binding.BookingProfileImage)



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

        getToken()

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
                                                println("No it Doesn't Exist.")
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
                setPositiveButton("Submit"){ dialog, which ->
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
                                        val reviewMode = ReviewMode(name, result, "null", id)
                                        reference.child("Doctor").child(doctorUserid)
                                            .child("Review").child(id).setValue(reviewMode)
                                            .addOnSuccessListener {
                                                dialog.dismiss()
                                                Toast.makeText(
                                                    context,
                                                    "Your review has been Updated successfully.",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                    }
                                }
                            }
                        }
                    }
                }

                setNegativeButton("Cancel"){ dialog, which ->
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
            val doctor = Doctor(id, name, clinic, "null", mobile, speciality, "null", profilePic, "null", "null", "null")

            val userRef = firebaseDatabase.child("Users").child(currentUserId)
                userRef.child("Visited_Doctors").child(doctorUserid).setValue(doctor).addOnSuccessListener {
                Log.i("Saved", "Saved to database.")
            }
        }
    }

    private fun getToken() {
        val databaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers").child("Doctor").child(hisId!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("hisId", myId)
                    data.put("hisImage", "Image")
                    data.put("title", "Suhail Mohammad")
                    data.put("message", "Booked an appointment")

                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
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