package com.medico.medko.View.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.View.Adapters.EntireRecordAdapter
import com.medico.medko.View.Adapters.MyRecordAdapter
import com.medico.medko.databinding.FragmentEntireBinding
import com.medico.medko.viewModel.MyRecordViewModel


class EntireFragment : Fragment() {

    private lateinit var myRecordAdapter : MyRecordAdapter
    private lateinit var myRecordViewModel: MyRecordViewModel
    private lateinit var _binding : FragmentEntireBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntireBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onResume() {
        super.onResume()
        val id : String? = activity?.intent?.extras?.getString("id")
        val model : MyRecordViewModel by viewModels()
        val adapter = EntireRecordAdapter(this@EntireFragment)
        _binding.RecyclerViewEntireHistory.adapter = adapter

        model.entireData(id.toString()).observe(viewLifecycleOwner, {
                history ->
            kotlin.run {
                history.let {
                    _binding.RecyclerViewEntireHistory.adapter = adapter
                    when(it){
                        it ->{
                            if(it.isNotEmpty() && it.size > 0){
                                _binding.RecyclerViewEntireHistory.visibility = View.VISIBLE
                                _binding.noApps.visibility = View.GONE
                                adapter.imageList(it)
                            }
                            if(it.isEmpty() && it.size == 0){
                                _binding.RecyclerViewEntireHistory.visibility = View.GONE
                                _binding.noApps.visibility = View.VISIBLE
                                _binding.noApps.text = "you have no records."
                            }
                        }
                    }
                }
                val layoutManager : LinearLayoutManager = LinearLayoutManager(context)
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true

                _binding.RecyclerViewEntireHistory.adapter = adapter
                _binding.RecyclerViewEntireHistory.setHasFixedSize(true)
                _binding.RecyclerViewEntireHistory.layoutManager = layoutManager
            }
        })
    }
}
