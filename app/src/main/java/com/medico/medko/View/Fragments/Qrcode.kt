package com.medico.medko.View.Fragments

import HistoryViewModel
import android.annotation.SuppressLint
import android.content.Context

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.medico.medko.databinding.FragmentQrcodeBinding
import android.content.Intent
import androidx.fragment.app.viewModels
import com.medico.medko.View.Activities.PaymentActivity
import com.medico.medko.viewModel.AppointmentViewModel
import java.lang.RuntimeException


class Qrcode : Fragment() {

    private lateinit var _binding : FragmentQrcodeBinding
    private lateinit var id : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQrcodeBinding.inflate(LayoutInflater.from(context), container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        id = activity?.intent?.extras?.getString("id_firebase").toString()

        _binding.PaymentSubmission.setOnClickListener {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra("firebase_fb", id)
            startActivity(intent)
        }

        super.onViewCreated(view, savedInstanceState)
    }

}




