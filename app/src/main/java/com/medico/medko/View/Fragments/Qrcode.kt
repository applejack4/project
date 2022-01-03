package com.medico.medko.View.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.medico.medko.Model.Doctor
import com.medico.medko.R
import com.medico.medko.databinding.FragmentQrcodeBinding
import com.medico.medko.viewModel.MyRecordViewModel


class Qrcode : Fragment() {

    private lateinit var _binding : FragmentQrcodeBinding
    private lateinit var id : String
    private lateinit var firebaseDatabase : DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrcodeBinding.inflate(LayoutInflater.from(context), container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        id = activity?.intent?.extras?.getString("id_firebase").toString()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        val userId : String = activity?.intent?.extras?.getString("id_firebase").toString()
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
        firebaseDatabase.child("Doctor").child(userId).get().addOnSuccessListener {
            if (it.exists()){
                when(val qr : String = it.child("qrCode").value.toString()){
                    qr ->{
                        if(qr == "null"){
                            _binding.NoQRCode.visibility = View.VISIBLE
                            _binding.QRcode.visibility = View.GONE
                            _binding.NoQRCode.text = "The doctor has not uploaded Payment QRCode"
                        }else{
                            _binding.NoQRCode.visibility = View.GONE
                            _binding.QRcode.visibility = View.VISIBLE
                            context?.let { it1 -> Glide.with(it1).asBitmap().load(qr).into(_binding.QRcode) }
                        }
                    }
                }
            }
        }

    }

}




