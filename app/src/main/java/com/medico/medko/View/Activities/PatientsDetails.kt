package com.medico.medko.View.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.medico.medko.R
import com.medico.medko.View.Fragments.EntireFragment
import com.medico.medko.View.Fragments.MyHistory
import com.medico.medko.databinding.ActivityPatientsDetailsBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso

class PatientsDetails : AppCompatActivity() {

    private val myHistory = MyHistory()
    private val entireHistory = EntireFragment()
    private lateinit var _binding : ActivityPatientsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPatientsDetailsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        changeFragment(myHistory)

        _binding.NavigationDetail.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.My_History -> changeFragment(myHistory)
                R.id.Entire_History -> changeFragment(entireHistory)
            }
            true
        }

        val patientName : String = intent.getStringExtra("name").toString()
        val profilePicture : String = intent.getStringExtra("profilePicture").toString()
        val mobile : String = intent.getStringExtra("mobile").toString()
        val id : String = intent.getStringExtra("id").toString()

        _binding.PatientsNameDetail.text = patientName
        Picasso.get().load(profilePicture)?.fit()?.centerInside()?.placeholder(R.drawable.ic_baseline_account_circle_24)?.into(_binding.PatientsImageDetail)
    }

    private fun changeFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.FrameLayout_Container_Detail, fragment).commit()
    }

    override fun onResume() {
        super.onResume()
        val mobile : String = intent.getStringExtra("mobile").toString()
        _binding.CallPatientDetail.setOnClickListener {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.CALL_PHONE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        val callIntent = Intent(Intent.ACTION_CALL)
                        callIntent.setData(Uri.parse("tel:$mobile"))
                        startActivity(callIntent)
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(this@PatientsDetails, "Permission has been denied", Toast.LENGTH_LONG).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permission: PermissionRequest,
                        token: PermissionToken
                    ) {
                        showRationalForPermissionMobile()
                    }
                }).onSameThread().check()
        }
    }

    private fun showRationalForPermissionMobile(){
        AlertDialog.Builder(this).setMessage("It seems that you have declined the permissions to access the feature," +
                "                \"Please turn on the permission to use this feature").setPositiveButton("GO TO SETTINGS"){
                _,_ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", this.packageName, null)
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