package com.medico.medko.View.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.View.Adapters.FirebaseSearchAdapter
import com.medico.medko.View.Adapters.UserRecordsAdapter
import com.medico.medko.databinding.ActivitySpecificDoctorListBinding

import com.medico.medko.viewModel.MyRecordViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class SpecificDoctorList : AppCompatActivity() {
    private lateinit var _binding : ActivitySpecificDoctorListBinding
    private lateinit var firebaseDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySpecificDoctorListBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onResume() {
        super.onResume()
        val adapter = FirebaseSearchAdapter()
        val recordAdapter = UserRecordsAdapter()
        val model : MyRecordViewModel by viewModels()
        val currentUserId : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

        when(val receivedIntent : String = intent.getStringExtra("item").toString()){
            receivedIntent ->{
                _binding.DoctorCategory.text = receivedIntent
                if(receivedIntent == receivedIntent){
                    model.specificDoctor(receivedIntent).observe(this, {
                            doctors ->
                        run {
                            doctors.let {
                                when(it){
                                    it ->{
                                        if(it.isNotEmpty() || it.size > 0){
                                            _binding.Recycler.visibility = View.VISIBLE
                                            _binding.TvNoDoctors.visibility = View.GONE
                                            adapter.passingList(it)
                                            adapter.notifyDataSetChanged()
                                        }
                                        if(it.isEmpty() || it.size == 0){
                                            _binding.Recycler.visibility = View.GONE
                                            _binding.TvNoDoctors.visibility = View.VISIBLE
                                            _binding.TvNoDoctors.text = "No $receivedIntent's Are Available"
                                        }
                                    }
                                }
                            }
                            adapter.passingActivity(this)
                            _binding.Recycler.adapter = adapter
                            _binding.Recycler.setHasFixedSize(true)
                            _binding.Recycler.layoutManager = LinearLayoutManager(this)
                        }
                    })
                }
                if(receivedIntent == "My Doctors/Records"){
                    println("Works right for Hello.")
                    model.myDoctors(currentUserId).observe(this, {
                        doctors ->
                        run {
                                doctors.let {
                                    if (it.isNotEmpty()){
                                        _binding.Recycler.visibility = View.VISIBLE
                                        _binding.TvNoDoctors.visibility = View.GONE

                                        recordAdapter.passingList(it)
                                        recordAdapter.notifyDataSetChanged()
                                    }else if(it.isEmpty()){
                                        _binding.Recycler.visibility = View.GONE
                                        _binding.TvNoDoctors.visibility = View.VISIBLE
                                        _binding.TvNoDoctors.text = "No $receivedIntent Doctors Registered"
                                    }
                                }
                            recordAdapter.passingActivity(this)
                            _binding.Recycler.adapter = recordAdapter
                            _binding.Recycler.setHasFixedSize(true)
                            _binding.Recycler.layoutManager = LinearLayoutManager(this)
                        }
                    })
                }
            }
        }
    }
}

