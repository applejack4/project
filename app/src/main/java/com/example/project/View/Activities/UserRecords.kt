package com.example.project.View.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project.R
import com.example.project.View.Adapters.MyRecordAdapter
import com.example.project.databinding.ActivityUserRecordsBinding
import com.example.project.viewModel.MyRecordViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso

class UserRecords : AppCompatActivity() {

    private lateinit var _binding : ActivityUserRecordsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUserRecordsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()

        val profilePic : String = intent.getStringExtra("profilePic").toString()
        val name : String = intent.getStringExtra("Doctor_name").toString()
        val mobile : String = intent.getStringExtra("mobile").toString()

        _binding.CallDoctorDetail.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.setData(Uri.parse("tel:$mobile"))
                        startActivity(callIntent)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(this@UserRecords, "Permission has been denied", Toast.LENGTH_LONG).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                        showRationalForPermissionMobile()
                    }
                }).check()
        }

        _binding.DoctorNameDetail.text = name
        Picasso.get().load(profilePic)?.fit()?.centerInside()?.rotate(90F)?.placeholder(R.drawable.ic_baseline_account_circle_24)?.into(_binding.DoctorImageDetail)

        val id : String = intent.getStringExtra("id").toString()
        val model : MyRecordViewModel by viewModels()
        val adapter = MyRecordAdapter()

        model.myDoctorsHistory(id).observe(this, {
                history ->
            kotlin.run {
                history.let {
                    _binding.RecV.adapter = adapter
                    when(it){
                        it ->{
                            if(it.isNotEmpty() || it.size > 0){
                                _binding.RecV.visibility = View.VISIBLE
                                _binding.noRecord.visibility = View.GONE
                                adapter.imageList(it)
                            }
                            if(it.isEmpty() || it.size == 0){
                                _binding.RecV.visibility = View.GONE
                                _binding.noRecord.visibility = View.VISIBLE
                                _binding.noRecord.text = "Dr.$name has not uploaded any records Yet."
                            }
                        }
                    }
            }

                val layoutManager : LinearLayoutManager = LinearLayoutManager(this)
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true

                adapter.passingActivity(this)
                _binding.RecV.adapter = adapter
                _binding.RecV.setHasFixedSize(true)
                _binding.RecV.layoutManager = layoutManager


            }
        })
    }

    private fun showRationalForPermissionMobile(){
        AlertDialog.Builder(this).setMessage("It seems that you have declined the permissions to access the feature," +
                "                \"Please turn on the permission to use this feature").setPositiveButton("GO TO SETTINGS"){
                _,_ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package",packageName, null)
                intent.data = uri
                startActivity(intent)
            }catch (e : ActivityNotFoundException){
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){
                dialog, _ ->
            dialog.dismiss()
        }.show()
    }
}