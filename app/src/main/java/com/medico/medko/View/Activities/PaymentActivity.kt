//package com.medico.medko.View.Activities
//
//import android.annotation.SuppressLint
//import android.app.Dialog
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.text.TextUtils
//import android.widget.Toast
//import androidx.activity.viewModels
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Observer
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.*
//import com.medico.medko.R
//import com.medico.medko.View.Adapters.PaymentAdapter
//import com.medico.medko.View.Fragments.Qrcode
//import com.medico.medko.databinding.ActivityPaymentBinding
//import com.medico.medko.databinding.DialogCustomListBinding
//import com.medico.medko.utils.Payment
//import com.razorpay.Checkout
//import com.razorpay.PaymentResultListener
//import org.json.JSONObject
//import java.lang.Exception
//
//class PaymentActivity : AppCompatActivity(), PaymentResultListener{
//
//    private lateinit var _binding : ActivityPaymentBinding
//    private lateinit var firebaseDatabase : DatabaseReference
//    private lateinit var auth : FirebaseAuth
//    private lateinit var customListDialog: Dialog
//    private lateinit var gpay : String
//    private lateinit var pPay : String
//    private lateinit var paytm : String
//    private lateinit var name : String
//    private lateinit var advAmount : String
//    private lateinit var dName : String
//    private lateinit var fbId : String
//    private lateinit var mail : String
//    private lateinit var phone : String
//
//    private fun initViews() {
//        val transactionId = "TID" + System.currentTimeMillis()
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        fbId = intent.getStringExtra("firebase_fb").toString()
//        println("This is the passing fb id $fbId")
//
//        setContentView(R.layout.activity_payment)
//        _binding = ActivityPaymentBinding.inflate(layoutInflater)
//        setContentView(_binding.root)
//        Checkout.preload(this)
//        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
//    }
//
//    @SuppressLint("SetTextI18n")
//    override fun onResume() {
//        super.onResume()
//        val detailRef = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
//        val cUser = FirebaseAuth.getInstance().currentUser?.uid.toString()
//
//        detailRef.child("Users").child(cUser).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    name = snapshot.child("firstname").value.toString()
//                    _binding.PaymentPatientNameTIE.setText(name)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@PaymentActivity, error.message, Toast.LENGTH_LONG).show()
//            }
//
//        })
//
//        detailRef.child("Doctor").child(fbId).addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                if(snapshot.exists()){
//                    advAmount = snapshot.child("bookingAmount").value.toString()
//                    dName = snapshot.child("doctorName").value.toString()
//                    gpay = snapshot.child("gPay").value.toString()
//                    pPay = snapshot.child("pPay").value.toString()
//                    paytm = snapshot.child("paytm").value.toString()
//                    phone = snapshot.child("mobile").value.toString()
//                    mail = snapshot.child("email").value.toString()
//
//                    _binding.PaymentAmountTIE.setText(advAmount)
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@PaymentActivity, error.message, Toast.LENGTH_LONG).show()
//            }
//
//        })
//
//        _binding.PaymentUPIIDTIE.setOnClickListener {
//            customItemListDialog("Select Payment Method", Payment.pay(), Payment.pay)
//        }
//
//        _binding.PaymentSubmission.setOnClickListener {
//            val amount : String = _binding.PaymentAmountTIE.text.toString()
//            val upiCode : String = _binding.PaymentUPIIDTIE.text.toString()
//            val patientName : String = _binding.PaymentPatientNameTIE.text.toString()
//            val note : String = _binding.PaymentNoteTIE.text.toString()
//
//            when{
//                TextUtils.isEmpty(amount) ->{
//                    Toast.makeText(this, "Amount cannot be empty", Toast.LENGTH_LONG).show()
//                    _binding.PaymentAmountTIE.error = "Amount cannot be null."
//                }
//                TextUtils.isEmpty(upiCode) ->{
//                    Toast.makeText(this, "UpiCode cannot be empty", Toast.LENGTH_LONG).show()
//                    _binding.PaymentUPIIDTIE.error = "UPI ID cannot be empty."
//                }
//                TextUtils.isEmpty(patientName) ->{
//                    Toast.makeText(this, "name cannot be empty", Toast.LENGTH_LONG).show()
//                    _binding.PaymentPatientNameTIE.error = "Name cannot be empty."
//                }
//                TextUtils.isEmpty(note) ->{
//                    _binding.PaymentNoteTIE.setText("Payment of $advAmount being made to $dName by $name for an Appointment Slot.")
//                    Toast.makeText(this, "Please click the pay button again to proceed.", Toast.LENGTH_LONG).show()
//                }else ->{
//                makePayment(amount.toInt())
//            }
//            }
//        }
//    }
//
//    private fun customItemListDialog(title : String, itemList : List<String>, selection : String ){
//        customListDialog = Dialog(this)
//        val binding : DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
//        customListDialog.setContentView(binding.root)
//        binding.tvDialogList.text = title
//
//        binding.rvListDialog.layoutManager = LinearLayoutManager(this)
//        val adapter = PaymentAdapter(this, itemList, selection)
//        binding.rvListDialog.adapter = adapter
//        customListDialog.show()
//    }
//
//
//    fun selectedListItem(item : String, selection : String){
//        when(selection){
//            Payment.pay ->{
//                if (item == "phone-pay"){
//                    _binding.PaymentUPIIDTIE.setText(pPay)
//                    customListDialog.dismiss()
//                }
//                if (item == "Google-pay"){
//                    _binding.PaymentUPIIDTIE.setText(gpay)
//                    customListDialog.dismiss()
//                }
//                if (item == "Paytm"){
//                    _binding.PaymentUPIIDTIE.setText(paytm)
//                    customListDialog.dismiss()
//                }
//            }
//
//            else ->{
//                customListDialog.dismiss()
//            }
//        }
//    }
//
//
//    private fun makePayment(amount : Int){
//
//        val checkout = Checkout()
//        checkout.setKeyID("rzp_test_z9pZiXuSpqBbaE")
//
//        try {
//            val options = JSONObject()
//            options.put("name", "Razorpay Integration")
//            options.put("Description", "Trial version.")
//            options.put("currency", "INR")
//
//            options.put("amount", "${(amount * 100)}")
//            options.put("prefill.email", mail)
//            options.put("prefill.contact", phone)
//
//            checkout.open(this, options)
//        }catch (e : Exception){
//            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
//            e.printStackTrace()
//        }
//    }
//
//    override fun onPaymentSuccess(success : String?) {
//        Toast.makeText(this, "Payment is SuccessFull + ${success.toString()}", Toast.LENGTH_LONG).show()
//    }
//
//    override fun onPaymentError(p0: Int, fail : String?) {
//        Toast.makeText(this, "payment cancelled.", Toast.LENGTH_LONG).show()
//    }
//}