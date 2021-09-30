package com.example.project.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.project.R
import com.example.project.View.Fragments.EntireFragment
import com.example.project.View.Fragments.MyHistory
import com.example.project.databinding.ActivityPatientsDetailsBinding

class PatientsDetails : AppCompatActivity() {

    private val myHistory = MyHistory()
    private val entireHistory = EntireFragment()
    private lateinit var _binding : ActivityPatientsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPatientsDetailsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        changeFragment(myHistory)

        _binding.NavigationDetail.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.My_History -> changeFragment(myHistory)
                R.id.Entire_History -> changeFragment(entireHistory)
            }
            true
        }

        val patientName : String = intent.getStringExtra("name").toString()
        val profilePicture : String = intent.getStringExtra("profilePicture").toString()
        val mobile : String = intent.getStringExtra("mobile").toString()
        val id : String = intent.getStringExtra("id").toString()

        _binding.PatientsNameDetail.text = patientName
    }

    private fun changeFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FrameLayout_Container_Detail, fragment).commit()
    }

    override fun onResume() {
        super.onResume()
        _binding.CallPatientDetail.setOnClickListener {
            Toast.makeText(applicationContext, "Call", Toast.LENGTH_LONG).show()
        }
    }
}