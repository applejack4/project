package com.medico.medko.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.medico.medko.R
import com.medico.medko.View.Fragments.DoctorHistory
import com.medico.medko.View.Fragments.DoctorHome
import com.medico.medko.View.Fragments.DoctorProfile
import com.medico.medko.View.Fragments.DoctorReview
import com.medico.medko.databinding.ActivityDoctorMainPageBinding

class DoctorMainPage : AppCompatActivity() {
    private val doctorHome = DoctorHome()
    private val doctorHistory = DoctorHistory()
    private val doctorReview = DoctorReview()
    private val doctorProfile = DoctorProfile()
    private lateinit var _binding : ActivityDoctorMainPageBinding
    private lateinit var receivedIntent : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDoctorMainPageBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    override fun onResume() {
        super.onResume()
        receivedIntent  = intent.getStringExtra("main").toString()
        when(receivedIntent){
            "FromSplashNotNull" ->{
                changeFragment(doctorHome)
            }

            "FromNotificationReview" ->{
                changeFragment(doctorReview)
            }
        }


        _binding.BottomNavigationDoctor.setOnItemSelectedListener{
            when(it.itemId){
                R.id.Appointment_home -> changeFragment(doctorHome)
                R.id.Review_doc -> changeFragment(doctorReview)
                R.id.profile_doc -> changeFragment(doctorProfile)
                R.id.History_doc -> changeFragment(doctorHistory)
            }
            true
        }
    }

    private fun changeFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.Fragment_container_Doctor, fragment).commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}