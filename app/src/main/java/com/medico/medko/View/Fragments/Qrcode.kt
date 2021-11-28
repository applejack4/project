package com.medico.medko.View.Fragments

import android.annotation.SuppressLint

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.databinding.FragmentQrcodeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.medico.medko.View.Adapters.PaymentAdapter
import com.medico.medko.databinding.DialogCustomListBinding
import com.medico.medko.utils.Payment
import com.razorpay.Checkout
import org.json.JSONObject
import android.app.Activity

import android.R
import com.razorpay.PaymentResultListener
import java.lang.Exception


class Qrcode : Fragment(), PaymentResultListener {

    private lateinit var _binding : FragmentQrcodeBinding
    private lateinit var firebaseDatabase : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var customListDialog: Dialog
    private lateinit var gpay : String
    private lateinit var pPay : String
    private lateinit var paytm : String
    private lateinit var name : String
    private lateinit var advAmount : String
    private lateinit var dName : String

    private fun initViews() {
        val transactionId = "TID" + System.currentTimeMillis()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Checkout.preload(requireContext())
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
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val id : String = activity?.intent?.extras?.getString("id_firebase").toString()
        val detailRef = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
        val cUser = FirebaseAuth.getInstance().currentUser?.uid.toString()

        detailRef.child("Users").child(cUser).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    name = snapshot.child("firstname").value.toString()
                    _binding.PaymentPatientNameTIE.setText(name)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }

        })

        detailRef.child("Doctor").child(id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    advAmount = snapshot.child("bookingAmount").value.toString()
                    dName = snapshot.child("doctorName").value.toString()
                    gpay = snapshot.child("gPay").value.toString()
                    pPay = snapshot.child("pPay").value.toString()
                    paytm = snapshot.child("paytm").value.toString()
                    _binding.PaymentAmountTIE.setText(advAmount)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }

        })

        _binding.PaymentUPIIDTIE.setOnClickListener {
            customItemListDialog("Select Payment Method", Payment.pay(), Payment.pay)
        }

        _binding.PaymentSubmission.setOnClickListener {
            val amount : String = _binding.PaymentAmountTIE.text.toString()
            val upiCode : String = _binding.PaymentUPIIDTIE.text.toString()
            val patientName : String = _binding.PaymentPatientNameTIE.text.toString()
            val note : String = _binding.PaymentNoteTIE.text.toString()

            when{
                TextUtils.isEmpty(amount) ->{
                    Toast.makeText(context, "Amount cannot be empty", Toast.LENGTH_LONG).show()
                    _binding.PaymentAmountTIE.error = "Amount cannot be null."
                }
                TextUtils.isEmpty(upiCode) ->{
                    Toast.makeText(context, "UpiCode cannot be empty", Toast.LENGTH_LONG).show()
                    _binding.PaymentUPIIDTIE.error = "UPI ID cannot be empty."
                }
                TextUtils.isEmpty(patientName) ->{
                    Toast.makeText(context, "name cannot be empty", Toast.LENGTH_LONG).show()
                    _binding.PaymentPatientNameTIE.error = "Name cannot be empty."
                }
                TextUtils.isEmpty(note) ->{
                    _binding.PaymentNoteTIE.setText("Payment of $advAmount being made to $dName by $name for an Appointment Slot.")
                    Toast.makeText(context, "Please click the pay button again to proceed.", Toast.LENGTH_LONG).show()
                }else ->{
                makePayment(amount.toInt())
            }
            }
        }
    }

    private fun customItemListDialog(title : String, itemList : List<String>, selection : String ){
        customListDialog = Dialog(requireContext())
        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        customListDialog.setContentView(binding.root)
        binding.tvDialogList.text = title

        binding.rvListDialog.layoutManager = LinearLayoutManager(context)
        val adapter = PaymentAdapter(this, itemList, selection)
        binding.rvListDialog.adapter = adapter
        customListDialog.show()
    }


    fun selectedListItem(item : String, selection : String){
        when(selection){
            Payment.pay ->{
                if (item == "phone-pay"){
                    _binding.PaymentUPIIDTIE.setText(pPay)
                    customListDialog.dismiss()
                }
                if (item == "Google-pay"){
                    _binding.PaymentUPIIDTIE.setText(gpay)
                    customListDialog.dismiss()
                }
                if (item == "Paytm"){
                    _binding.PaymentUPIIDTIE.setText(paytm)
                    customListDialog.dismiss()
                }
            }

            else ->{
                customListDialog.dismiss()
            }
        }
    }


    private fun makePayment(amount : Int){

        val checkout = Checkout()
        checkout.setKeyID("rzp_test_z9pZiXuSpqBbaE")

        try {
            val options = JSONObject()
            options.put("name", "Razorpay Integration")
            options.put("Description", "Trial version.")
            options.put("currency", "INR")

            options.put("amount", "${(amount * 100)}")
            options.put("prefill.email", "zacksuhail@gmail.com")
            options.put("prefill.contact", "8008535097")

            checkout.open(requireActivity(), options)
        }catch (e : Exception){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(success : String?) {
        Toast.makeText(context, "Payment is SuccessFull + ${success.toString()}", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(p0: Int, fail : String?) {
        Toast.makeText(context, fail.toString(), Toast.LENGTH_LONG).show()
    }
}




