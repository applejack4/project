package com.medico.medko.View.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.medico.medko.R
import com.medico.medko.databinding.ActivitySplashScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@SuppressLint("CustomSplashScreen")
public class SplashScreen : AppCompatActivity() {
    private lateinit var _binding : ActivitySplashScreenBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var allUsersDefaultValue : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        _binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        val currentUser : String = auth.currentUser?.uid.toString()

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
                Handler(Looper.getMainLooper()).postDelayed({
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
                                    }else{
                                        println("snapshot doesn't exist")
                                    }
                                }
                            }

                            if(auth.currentUser == null){
                                mainActivity()
                            }
                        }
                    }
                },1000)
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
        startActivity(Intent(this, DoctorMainPage::class.java))
    }

    private fun mainActivity(){
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }
}