package com.medico.medko.View.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.medico.medko.R
import com.medico.medko.View.Fragments.SearchFragment
import com.medico.medko.View.Fragments.HomeFragment
import com.medico.medko.View.Fragments.SettingFragment
import com.medico.medko.databinding.ActivityMainUserPageBinding
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


        binding.BottomNavigation.setOnItemSelectedListener{
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