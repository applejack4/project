package com.example.project.View.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.project.Model.Doctor
import com.example.project.Model.User
import com.example.project.View.Activities.ChangePassword
import com.example.project.View.Activities.MainActivity
import com.example.project.databinding.FragmentSettingsBinding
import com.example.project.viewModel.NotificationsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SettingFragment : Fragment() {
    private lateinit var firebaseDatabase : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

      firebaseAuth = FirebaseAuth.getInstance()

    _binding = FragmentSettingsBinding.inflate(inflater, container, false)
    val root: View = binding.root
    return root
  }

    override fun onResume() {
        super.onResume()

        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
        firebaseAuth = FirebaseAuth.getInstance()

        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            firebaseDatabase.child("Users").child(userId.toString()).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user : User? = snapshot.getValue(User::class.java)
                        if (user != null) {
                            _binding!!.UserPermanentName.text = user.firstname
                            _binding!!.UserPermanentMobile.text = user.mobile
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("TAG", error.message)
                }

            })
        }


        _binding!!.bTnEditProfile.setOnClickListener {
            val name : String = _binding!!.UserPermanentName.text.toString()
            val mobile : String = _binding!!.UserPermanentMobile.text.toString()
            //name Settings
            _binding!!.UserPermanentName.visibility = View.GONE
            _binding!!.ChangeUserName.visibility = View.VISIBLE

            //mobile settings
            _binding!!.UserPermanentMobile.visibility = View.GONE
            _binding!!.ChangeUserMobile.visibility = View.VISIBLE

            //button settings

            _binding!!.bTnEditProfile.visibility = View.GONE
            _binding!!.bTnSaveChanges.visibility = View.VISIBLE

            _binding!!.ChangeUserName.setText(name)
            _binding!!.ChangeUserMobile.setText(mobile)

            onResume()
        }

        _binding!!.bTnSaveChanges.setOnClickListener {
                val name : String = _binding!!.ChangeUserName.text.toString()
                val mobile : String = _binding!!.ChangeUserMobile.toString()
            val userid : String  = FirebaseAuth.getInstance().currentUser?.uid.toString()

                when{
                    TextUtils.isEmpty(_binding!!.ChangeUserName.text) ->{
                        Toast.makeText(context, "Name Cannot be empty", Toast.LENGTH_LONG).show()
                    }
                    TextUtils.isEmpty(_binding!!.ChangeUserMobile.text) ->{
                        Toast.makeText(context, "Mobile Cannot be empty", Toast.LENGTH_LONG).show()
                    }
                    else ->{
                        if(_binding!!.ChangeUserName.text.isNotEmpty() && _binding!!.ChangeUserMobile.text.isNotEmpty()){
                            firebaseDatabase =  FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")

                            val nameUpdate : String = _binding!!.ChangeUserName.text.toString()
                            val mobileUpdate : String = _binding!!.ChangeUserMobile.text.toString()


                            firebaseDatabase.child("Users").child(userid).child("firstname").setValue(nameUpdate)
                            firebaseDatabase.child("Users").child(userid).child("mobile").setValue(mobileUpdate)
                            editTextMethod()
                            onResume()
                        }
                    }
                }
        }

        _binding!!.BtnChangePassword.setOnClickListener {
            startActivity(Intent(context, ChangePassword::class.java))
        }

        _binding!!.BtnLogout.setOnClickListener {
            firebaseAuth.signOut()
            activity?.finish()
            startActivity(Intent(context, MainActivity::class.java))
        }
    }

    fun editTextMethod(){
        //name Settings
        _binding!!.ChangeUserName.visibility = View.GONE
        _binding!!.UserPermanentName.visibility = View.VISIBLE

        //mobile settings
        _binding!!.ChangeUserMobile.visibility = View.GONE
        _binding!!.UserPermanentMobile.visibility = View.VISIBLE

        //button change
        _binding!!.bTnSaveChanges.visibility = View.GONE
        _binding!!.bTnEditProfile.visibility  = View.VISIBLE
    }


override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}