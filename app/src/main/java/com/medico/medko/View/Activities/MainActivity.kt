package com.medico.medko.View.Activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.medico.medko.databinding.ActivityMainBinding
import java.util.*

 class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var allUsersDefaultValue : DatabaseReference
    private lateinit var currentUse : String

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val progress = ProgressDialog(this@MainActivity)
        progress.setMessage("Logging in.")
        progress.setCancelable(false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        currentUse = auth.currentUser?.uid.toString()
        allUsersDefaultValue = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AllUsers")

        binding.BtnDoctorRegister.setOnClickListener {
            val docIntent = Intent(this@MainActivity, DoctorRegistration::class.java)
            startActivity(docIntent)
        }

        binding.BtnUserRegister.setOnClickListener {
            val userIntent = Intent(this@MainActivity, UserRegistration::class.java)
            startActivity(userIntent)
        }

        binding.BtnLogin.setOnClickListener {
            val email = binding.TIEEmail.text.toString().trim() { it < ' ' }
            val password = binding.TIEPassword.text.toString().trim(){ it < ' '}

            when {
                TextUtils.isEmpty(email) ->{
                    binding.TIEEmail.error = "Email is Required"
                }
                TextUtils.isEmpty(password) ->{
                    binding.TIEPassword.error = "Password is Required"
                }
                else ->{
                    if(checkForInternet(this)){
                        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                                task ->
                            run {
                                progress.show()
                                if (task.isSuccessful) {
                                    val currentUser = auth.currentUser!!.uid
                                    allUsersDefaultValue.child(currentUser).get().addOnSuccessListener {
                                        if(it.exists()){
                                            val defaultNumber = it.child("default_number").value
                                            if (defaultNumber != null) {
                                                if(defaultNumber == "1"){
                                                    progress.dismiss()
                                                    finish()
                                                    doctorIntent()
                                                }else{
                                                    progress.dismiss()
                                                    finish()
                                                    userIntent()
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    progress.dismiss()
                                    Toast.makeText(this@MainActivity, task.exception?.message, Toast.LENGTH_LONG).show()
                                }
                            }
                        }.addOnFailureListener {
                            progress.dismiss()
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                    }else{
                        Toast.makeText(this, "Connect to Active Internet connection and try again", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        binding.ForgotPasswordUnderlined.setOnClickListener{
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }

   private fun userIntent(){
       val loginIntent = Intent(this@MainActivity, MainUserPage::class.java)
       startActivity(loginIntent)
   }

    private fun doctorIntent(){
        val intent = Intent(this@MainActivity, DoctorMainPage::class.java)
        intent.putExtra("main", "FromSplashNotNull")
        startActivity(intent)
    }

     private fun checkForInternet(context: Context): Boolean {

         // register activity with the connectivity manager service
         val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

         // if the android version is equal to M
         // or greater we need to use the
         // NetworkCapabilities to check what type of
         // network has the internet connection
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

             // Returns a Network object corresponding to
             // the currently active default data network.
             val network = connectivityManager.activeNetwork ?: return false

             // Representation of the capabilities of an active network.
             val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

             return when {
                 // Indicates this network uses a Wi-Fi transport,
                 // or WiFi has network connectivity
                 activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                 // Indicates this network uses a Cellular transport. or
                 // Cellular has network connectivity
                 activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                 // else return false
                 else -> false
             }
         } else {
             // if the android version is below M
             @Suppress("DEPRECATION") val networkInfo =
                 connectivityManager.activeNetworkInfo ?: return false
             @Suppress("DEPRECATION")
             return networkInfo.isConnected
         }
     }
}