package com.example.project.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.project.R
import com.example.project.View.Fragments.DoctorHistory
import com.example.project.View.Fragments.DoctorHome
import com.example.project.View.Fragments.DoctorProfile
import com.example.project.View.Fragments.DoctorReview
import com.example.project.databinding.ActivityDoctorMainPageBinding

class DoctorMainPage : AppCompatActivity() {
    private val doctorHome = DoctorHome()
    private val doctorHistory = DoctorHistory()
    private val doctorReview = DoctorReview()
    private val doctorProfile = DoctorProfile()
    private lateinit var _binding : ActivityDoctorMainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDoctorMainPageBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        changeFragment(doctorHome)

        _binding.BottomNavigationDoctor.setOnNavigationItemSelectedListener{
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