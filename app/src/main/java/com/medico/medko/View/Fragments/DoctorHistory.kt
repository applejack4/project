package com.medico.medko.View.Fragments

import HistoryViewModel
import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.medico.medko.View.Adapters.HistoryAdapter
import com.google.firebase.auth.FirebaseAuth
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.medico.medko.databinding.FragmentDoctorHistoryBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

class DoctorHistory : Fragment() {

    private var _binding : FragmentDoctorHistoryBinding?= null
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorHistoryBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        val model : HistoryViewModel by viewModels()
        val adapter = HistoryAdapter()
        adapter.passingFragment(this)

        model.getTitleHistory().observe(viewLifecycleOwner, {
                history ->
            run {
                history.let {
                    _binding!!.RecyclerView.adapter = adapter
                    when(it){
                        it ->{
                            if (it.isNotEmpty() || it.size > 0){
                                _binding!!.RecyclerView.visibility = View.VISIBLE
                                _binding!!.noHistory.visibility = View.GONE
                                adapter.appointmentList(it)
                                println("SIze of it is ${it.size}")
                            }
                            if(it.isEmpty() || it.size == 0){
                                _binding!!.RecyclerView.visibility = View.GONE
                                _binding!!.noHistory.visibility = View.VISIBLE
                                _binding!!.noHistory.text = "You have no Patient's History/Empty"
                            }
                        }
                    }
                }

                val layoutManager : LinearLayoutManager = LinearLayoutManager(context)
                layoutManager.reverseLayout = true
                layoutManager.stackFromEnd = true

                _binding?.RecyclerView?.adapter = adapter
                _binding?.RecyclerView?.setHasFixedSize(true)
                _binding?.RecyclerView?.layoutManager = layoutManager
            }
        })
    }



    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun callUser(number : String){
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CALL_PHONE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    val callIntent = Intent(Intent.ACTION_CALL)
                    callIntent.setData(Uri.parse("tel:$number"))
                    startActivity(callIntent)
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(context, "Permission has been denied", Toast.LENGTH_LONG).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    showRationalForPermissionMobile()
                }
            }).check()
    }

    private fun showRationalForPermissionMobile(){
        AlertDialog.Builder(context).setMessage("It seems that you have declined the permissions to access the feature," +
                "                \"Please turn on the permission to use this feature").setPositiveButton("GO TO SETTINGS"){
                _,_ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity?.packageName, null)
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