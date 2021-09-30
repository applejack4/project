package com.example.project.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.project.R
import com.example.project.View.Fragments.SearchFragment
import com.example.project.View.Fragments.HomeFragment
import com.example.project.View.Fragments.SettingFragment
import com.example.project.databinding.ActivityMainUserPageBinding
import com.google.firebase.messaging.FirebaseMessaging


class MainUserPage : AppCompatActivity() {

    private lateinit var binding : ActivityMainUserPageBinding

    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val profileFragment  = SettingFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainUserPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        changeFragment(homeFragment)


        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                val firebaseToken = it.result.toString()
                println("Token printed is --->${firebaseToken.toString()}")
            }
        }


        binding.BottomNavigation.setOnNavigationItemSelectedListener{
            when(it.itemId){
                R.id.Home -> changeFragment(homeFragment)
                R.id.Search -> changeFragment(searchFragment)
                R.id.Profile -> changeFragment(profileFragment)
            }
            true
        }


    }

    private fun changeFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.Fragment_container, fragment).commit()
    }
}