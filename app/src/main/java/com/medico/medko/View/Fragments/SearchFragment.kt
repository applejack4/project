package com.medico.medko.View.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.Model.Doctor
import com.medico.medko.View.Adapters.FirebaseSearchAdapter
import com.medico.medko.databinding.FragmentSearchBinding
import com.medico.medko.viewModel.SearchViewModel

class SearchFragment : Fragment() {

  private lateinit var searchViewModel : SearchViewModel
  private var firebaseSearchAdapter : FirebaseSearchAdapter ?= null
    private lateinit var docList : ArrayList<Doctor>
    private lateinit var itemList : ArrayList<Doctor>
private var _binding: FragmentSearchBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        itemList = arrayListOf<Doctor>()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        searchViewModel.doctorArrayList.observe(viewLifecycleOwner, { it ->
            docList = ArrayList<Doctor>()
            docList.clear()
            docList = it

            _binding?.SearchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    _binding?.SearchView?.clearFocus()
                    return false
                }

                override fun onQueryTextChange(query : String?): Boolean {
                    itemList.clear()

                    when(query){
                        query ->{
                            if(query!!.isEmpty()){
                                itemList.clear()
                            }

                            for(obj : Doctor in docList){
                                if(obj.DoctorName?.lowercase()?.contains(query.lowercase()) == true){
                                    itemList.add(obj)
                                }else if(query.isEmpty()){
                                    itemList.clear()
                                }
                            }
                            val searchAdapter = FirebaseSearchAdapter()
                            searchAdapter.passingFragment(this@SearchFragment)
                            searchAdapter.passingList(itemList)
                            _binding?.RecView?.adapter  = searchAdapter
                            _binding?.RecView?.adapter?.notifyDataSetChanged()
                            _binding?.RecView?.setHasFixedSize(true)
                            _binding?.RecView?.layoutManager = LinearLayoutManager(context)

                        }
                    }
                    return true
                }
            })
        })
    }


override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


