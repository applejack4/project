package com.example.project.View.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.Model.ReviewMode
import com.example.project.R
import com.example.project.View.Adapters.AppointmentAdapter
import com.example.project.View.Adapters.HistoryAdapter
import com.example.project.View.Adapters.ReviewAdapter
import com.example.project.databinding.FragmentDoctorReviewBinding
import com.example.project.databinding.FragmentReviewFragmentBinding
import com.example.project.viewModel.AppointmentViewModel
import com.example.project.viewModel.HistoryViewModel
import com.example.project.viewModel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class Review_fragment : Fragment() {
    private var _binding : FragmentDoctorReviewBinding ?= null
    private lateinit var appointmentAdapter : AppointmentAdapter
    private lateinit var appointViewModel : AppointmentViewModel
    private lateinit var firebaseDatabase : DatabaseReference
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorReviewBinding.inflate(layoutInflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val model : ReviewViewModel by viewModels()
        val id : String = activity?.intent?.extras?.getString("id_firebase").toString()

        val adapter = ReviewAdapter(this)
        _binding?.RecV?.layoutManager = LinearLayoutManager(activity)

        model.bookingReview(id).observe(viewLifecycleOwner, {
            review ->
            run {
                    review.let {
                        _binding!!.RecV.adapter = adapter
                        when(it){
                            it ->{
                                if(it.isNotEmpty() || it.size > 0){
                                    _binding!!.RecV.visibility = View.VISIBLE
                                    _binding!!.noReviews.visibility = View.GONE
                                    adapter.reviewList(it)
                                }
                                if(it.isEmpty() || it.size == 0){
                                    _binding!!.RecV.visibility = View.GONE
                                    _binding!!.noReviews.visibility = View.VISIBLE
                                    _binding!!.noReviews.text = "Doctor Has no Reviews"
                                }
                            }
                        }
                    }
            }
        })
    }

}