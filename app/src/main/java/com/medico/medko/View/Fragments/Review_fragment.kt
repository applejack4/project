package com.medico.medko.View.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.View.Adapters.AppointmentAdapter
import com.medico.medko.View.Adapters.ReviewAdapter
import com.medico.medko.databinding.FragmentDoctorReviewBinding
import com.medico.medko.viewModel.AppointmentViewModel
import com.medico.medko.viewModel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference


class Review_fragment : Fragment() {
    private var _binding : FragmentDoctorReviewBinding ?= null
    private lateinit var appointmentAdapter : AppointmentAdapter
    private lateinit var appointViewModel : AppointmentViewModel
    private lateinit var firebaseDatabase : DatabaseReference
    private lateinit var auth : FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

                val layoutManager : LinearLayoutManager = LinearLayoutManager(context)
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true

                _binding?.RecV?.adapter = adapter
                _binding?.RecV?.setHasFixedSize(true)
                _binding?.RecV?.layoutManager = layoutManager
            }
        })
    }

}