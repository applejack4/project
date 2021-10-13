package com.medico.medko.View.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medico.medko.databinding.FragmentQrcodeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso


class Qrcode : Fragment() {

    private lateinit var _binding : FragmentQrcodeBinding
    private lateinit var firebaseDatabase : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentQrcodeBinding.inflate(LayoutInflater.from(context), container, false)
        return _binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")

        auth = FirebaseAuth.getInstance()
        val receivedIntent : String = activity?.intent?.extras?.getString("id_firebase").toString()
        println("Received Intent is this $receivedIntent")

            val myRef = firebaseDatabase.child("Doctor").child(receivedIntent).child("Qrcode")
                myRef.get().addOnSuccessListener {
                    if(it.exists()) {
                        val image: String = it.child("qr").value.toString()
                        when (image) {
                            image -> {
                                if (image == "ybl@XXXXXXXX") {
                                    _binding.QRCODEScan.visibility = View.GONE
                                    _binding.noQrCode.visibility = View.VISIBLE
                                    _binding.noQrCode.text =
                                        "The Doctor has not uploaded a payment Qr Code."
                                }
                                if (image != "ybl@XXXXXXXX") {
                                    _binding.QRCODEScan.visibility = View.VISIBLE
                                    _binding.noQrCode.visibility = View.GONE
                                    Picasso.get().load(image).fit().centerInside().rotate(90F)
                                        .into(_binding.QRCODEScan)
                                }
                            }
                        }
                    }else {
                        _binding.QRCODEScan.visibility = View.GONE
                        _binding.noQrCode.visibility = View.VISIBLE
                        _binding.noQrCode.text =
                            "The Doctor has not uploaded a payment Qr Code."
                    }
            }
    }
}