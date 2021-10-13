package com.medico.medko.View.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.View.Adapters.MyRecordAdapter
import com.medico.medko.databinding.FragmentMyHistoryBinding
import com.medico.medko.viewModel.MyRecordViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyHistory : Fragment() {
    private lateinit var _binding : FragmentMyHistoryBinding
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMyHistoryBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")

        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
        val authId : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val id : String? = activity?.intent?.extras?.getString("id")
        val model : MyRecordViewModel by viewModels()
        val adapter = MyRecordAdapter()
        adapter.passingFragment(this)
        _binding.RecyclerViewMyHistory.adapter = adapter

        model.getData(id.toString()).observe(viewLifecycleOwner, {
                history ->
            kotlin.run {
                history.let {
                    _binding.RecyclerViewMyHistory.adapter = adapter

                    when(it){
                        it ->{
                            if(it.isNotEmpty() || it.size > 0){
                                _binding.RecyclerViewMyHistory.visibility = View.VISIBLE
                                _binding.noApps.visibility = View.GONE
                                adapter.imageList(it)
                            }
                            if(it.isEmpty() || it.size == 0){
                                _binding.RecyclerViewMyHistory.visibility = View.GONE
                                _binding.noApps.visibility = View.VISIBLE
                                _binding.noApps.text = "No Medical Record"
                            }
                        }
                    }
                }
                val layoutManager : LinearLayoutManager = LinearLayoutManager(context)
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true

                _binding.RecyclerViewMyHistory.adapter = adapter
                _binding.RecyclerViewMyHistory.setHasFixedSize(true)
                _binding.RecyclerViewMyHistory.layoutManager = layoutManager
            }
        })
    }
}