package com.example.project.View.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.Model.AppointConstructor
import com.example.project.Model.ReviewMode
import com.example.project.R
import com.example.project.View.Adapters.ReviewAdapter
import com.example.project.databinding.FragmentReviewFragmentBinding
import com.example.project.viewModel.ReviewViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList

class DoctorReview : Fragment() {
    private lateinit var _binding : FragmentReviewFragmentBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var firebaseDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewFragmentBinding.inflate(layoutInflater, container, false)
        return _binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val model : ReviewViewModel by viewModels()
        auth  = FirebaseAuth.getInstance()
        val adapter = ReviewAdapter(this)
        _binding.RecView.layoutManager = LinearLayoutManager(activity)

        model.getReviews().observe(viewLifecycleOwner, {
            reviews ->
            run {
                reviews.let {
                    _binding.RecView.adapter = adapter
                    when(it){
                        it ->{
                            if(it.isNotEmpty() || it.size > 0){
                                _binding.RecView.visibility = View.VISIBLE
                                _binding.noReviews.visibility = View.GONE
                                adapter.reviewList(it)
                            }
                            if (it.isEmpty() || it.size == 0){
                                _binding.RecView.visibility = View.GONE
                                _binding.noReviews.visibility = View.VISIBLE
                                _binding.noReviews.text = "You Have no Reviews"
                            }
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteReview(list : ArrayList<ReviewMode>, position : Int, id : String, name : String){

        val currentUser : String = auth.currentUser?.uid.toString()
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Delete Review.")
        dialog.setMessage("Are you sure you want to remove $name Review?")
        dialog.setCancelable(true)

        val adapter = ReviewAdapter(this)

        val model : ReviewViewModel by viewModels()

        dialog.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        }).setPositiveButton("Yes", DialogInterface.OnClickListener{ dialogInterface, i ->
            model.deleteReview(position)
            val myRef = firebaseDatabase.child("Doctor").child(currentUser).child("Review").child(id).removeValue()
            myRef.addOnSuccessListener {
                adapter.reviewList(list)
                adapter.notifyItemRemoved(position)
                adapter.notifyItemRangeChanged(position, list.size)
                adapter.notifyDataSetChanged()
                dialogInterface.dismiss()
            }.addOnFailureListener {
                Toast.makeText(context, "Error${it.message}", Toast.LENGTH_LONG).show()
                dialogInterface.dismiss()
            }
        }).show()

    }
}

