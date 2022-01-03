package com.medico.medko.View.Activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.medico.medko.R
import com.medico.medko.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var _binding : ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var allUsersDefaultValue : DatabaseReference
    private lateinit var currentUser : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        currentUser = FirebaseAuth.getInstance().currentUser?.uid.toString()


        allUsersDefaultValue = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AllUsers")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        val splash2Animation = AnimationUtils.loadAnimation(this, R.anim.anim_splash)
        _binding.tvAppName.animation = splash2Animation


        splash2Animation.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                if(checkForInternet(this@SplashScreen)){
                    Handler(Looper.getMainLooper()).postDelayed({
                     redirectingConfiguration()
                    },250)
                }else{
                    _binding.Refresh.visibility = View.VISIBLE
                    _binding.Refresh.setOnClickListener {
                        if(checkForInternet(this@SplashScreen)){
                            redirectingConfiguration()
                        }else{
                            Toast.makeText(this@SplashScreen, "Connect to Active Internet connection and try again", Toast.LENGTH_LONG).show()
                        }
                    }
                    Toast.makeText(this@SplashScreen, "Connect to Active Internet connection and try again", Toast.LENGTH_LONG).show()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })


    }


    private fun userIntent(){
        finish()
        startActivity(Intent(this, MainUserPage::class.java))
    }

    private fun doctorIntent(){
        finish()
        val docIntent = Intent(this, DoctorMainPage::class.java)
        docIntent.putExtra("main", "FromSplashNotNull")
        startActivity(docIntent)
    }

    private fun mainActivity(){
        finish()
        startActivity(Intent(this, MainActivity::class.java))
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

    private fun redirectingConfiguration(){
        when(auth){
            auth ->{
                if(auth.currentUser != null){
                    allUsersDefaultValue.child(currentUser).get().addOnSuccessListener {
                        if(it.exists()){
                            when(val defNumber : String = it.child("default_number").value.toString()){
                                defNumber ->{
                                    if(defNumber == "1"){
                                        doctorIntent()
                                    }
                                    if(defNumber == "0"){
                                        userIntent()
                                    }
                                }
                            }
                        }else {
                            mainActivity()
                        }
                    }
                }

                if(auth.currentUser == null){
                    mainActivity()
                }
            }
        }
    }
}