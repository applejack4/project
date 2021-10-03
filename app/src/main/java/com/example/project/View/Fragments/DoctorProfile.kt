package com.example.project.View.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.project.Model.Doctor
import com.example.project.Model.QrCode
import com.example.project.Model.profileImage
import com.example.project.View.Activities.ChangePassword
import com.example.project.View.Activities.MainActivity
import com.example.project.databinding.FragmentDoctorProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.squareup.picasso.Picasso
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class DoctorProfile : Fragment() {

    companion object{
        const val GALLERY = 2
        const val PROFILE = 3
    }

    private var currentPhotoPath : String ?= null
    private lateinit var imageUri : Uri
    private lateinit var auth : FirebaseAuth
    private lateinit var _binding : FragmentDoctorProfileBinding
    private lateinit var firebaseDatabase: DatabaseReference
    private var storageReference : StorageReference ?= null
    private var currentId : String ?= null
    private lateinit var f : File
    val fragment : DoctorProfile = this



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDoctorProfileBinding.inflate(LayoutInflater.from(context), container, false)
        return _binding.root
    }


    override fun onResume() {
        super.onResume()
        _binding.GalleryQrCode.setOnClickListener {
            Dexter.withContext(context).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if(report!!.areAllPermissionsGranted()){
                        val galIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        if(context?.let { galIntent.resolveActivity(it.packageManager) } != null){
                            // Create the File where the photo should go
                            var photoFile: File? = null
                            try {
                                photoFile = createImageFile()
                            } catch (ex: IOException) {
                                // Error occurred while creating the File
                                Log.i("Error","Exception occurred is ${ex.message}")
                            }
                            if(photoFile != null){
                                val photoURI = FileProvider.getUriForFile(context!!, "com.example.android.fileProvide", photoFile)
                                galIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                activityResult(galIntent, GALLERY)
                            }else{
                                return
                            }
                        }else{
                            return
                        }
                    }else{
                        showRationalDialogForPermission()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission : MutableList<PermissionRequest>?,
                    token : PermissionToken?
                ) {
                    showRationalDialogForPermission()
                }
            }).onSameThread().check()
        }



        _binding.changeProfilePicture.setOnClickListener {
            Dexter.withContext(context)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(report : MultiplePermissionsReport?) {
                        if(report!!.areAllPermissionsGranted()){
                            val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            if(context?.let { imageIntent.resolveActivity(it.packageManager) } != null){
                                var photofile: File? = null
                                try {
                                    photofile = createImageFile()
                                } catch (ex: IOException) {
                                    // Error occurred while creating the File
                                    Log.i("Error","Exception occurred is ${ex.message}")
                                }
                                if(photofile != null){
                                    val photoURI = FileProvider.getUriForFile(context!!, "com.example.android.fileProvide", photofile)
                                    imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                    activityResult(imageIntent, PROFILE)
                                }else{
                                    return
                                }
                            }
                        }else{
                            showRationalDialogForPermission()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogForPermission()
                    }
                }).onSameThread().check()
        }


        val userid : String = FirebaseAuth.getInstance().currentUser?.uid.toString()


        auth = FirebaseAuth.getInstance()
        currentId = auth.currentUser?.uid.toString()
        firebaseDatabase =  FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
        firebaseDatabase.child("Doctor").child(userid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (obj in snapshot.children){
                    val details = snapshot.getValue(Doctor::class.java)
                    if(details != null){
                        _binding.DoctorPermanentName.text = details.DoctorName
                        _binding.DoctorPermanentClinicName.text = details.ClinicName
                        _binding.DoctorPermanentStatus.text = details.hospitalStatus
                        _binding.DoctorPermanentMobile.text = details.mobile
                        _binding.UpiPaymentPermanent.text = details.upiPay
                        Picasso.get().load(details.profilePicture)?.fit()?.centerInside()?.rotate(90F)?.into(_binding.ImageProfile)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("Error", error.message)
            }
        })

        _binding.ButtonEditProfile.setOnClickListener {
            val name : String = _binding.DoctorPermanentName.text.toString()
            val mobile : String = _binding.DoctorPermanentMobile.text.toString()
            val clinic : String = _binding.DoctorPermanentClinicName.text.toString()
            val upiPay : String = _binding.UpiPaymentPermanent.text.toString()

            //button settings
            _binding.ButtonSaveChanges.visibility = View.VISIBLE
            _binding.ButtonEditProfile.visibility = View.GONE

            //Name Settings
            _binding.DoctorPermanentName.visibility =View.GONE
            _binding.ChangeDoctorName.visibility = View.VISIBLE


            //Mobile Settings
            _binding.DoctorPermanentMobile.visibility = View.GONE
            _binding.ChangeDoctorMobile.visibility = View.VISIBLE

            //clinic Settings
            _binding.DoctorPermanentClinicName.visibility = View.GONE
            _binding.ChangeDoctorClinic.visibility = View.VISIBLE

            //Upi payment
            _binding.UpiPaymentPermanent.visibility = View.GONE
            _binding.ChangeDoctorUpiPayment.visibility = View.VISIBLE

            _binding.ChangeDoctorClinic.setText(clinic)
            _binding.ChangeDoctorMobile.setText(mobile)
            _binding.ChangeDoctorName.setText(name)
            _binding.ChangeDoctorUpiPayment.setText(upiPay)
        }

        _binding.ButtonSaveChanges.setOnClickListener {

            val name : String = _binding.ChangeDoctorName.text.toString()
            val mobile : String = _binding.ChangeDoctorMobile.toString()
            val clinic : String = _binding.ChangeDoctorClinic.toString()
            val upi : String = _binding.UpiPaymentPermanent.toString()
            when{
                TextUtils.isEmpty(_binding.ChangeDoctorName.text) ->{
                    Toast.makeText(context, "Name Cannot be empty", Toast.LENGTH_LONG).show()
                }
                TextUtils.isEmpty(_binding.ChangeDoctorMobile.text) ->{
                    Toast.makeText(context, "Mobile Cannot be empty", Toast.LENGTH_LONG).show()
                }
                TextUtils.isEmpty(_binding.ChangeDoctorClinic.text) ->{
                    Toast.makeText(context, "Clinic Cannot be empty", Toast.LENGTH_LONG).show()
                }
                TextUtils.isEmpty(_binding.ChangeDoctorUpiPayment.text) ->{
                    Toast.makeText(context, "Upi payment cannot be Empty", Toast.LENGTH_LONG).show()
                }
                else ->{
                if(_binding.ChangeDoctorName.text.isNotEmpty() && _binding.ChangeDoctorMobile.text.isNotEmpty() && _binding.ChangeDoctorClinic.text.isNotEmpty()){
                    firebaseDatabase =  FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")

                    val nameUpdate : String = _binding.ChangeDoctorName.text.toString()
                    val mobileUpdate : String = _binding.ChangeDoctorMobile.text.toString()
                    val clinicUpdate : String = _binding.ChangeDoctorClinic.text.toString()
                    val upiUpdate : String = _binding.ChangeDoctorUpiPayment.text.toString()

                    firebaseDatabase.child("Doctor").child(userid).child("doctorName").setValue(nameUpdate)
                    firebaseDatabase.child("Doctor").child(userid).child("mobile").setValue(mobileUpdate)
                    firebaseDatabase.child("Doctor").child(userid).child("clinicName").setValue(clinicUpdate)
                    firebaseDatabase.child("Doctor").child(userid).child("upiPay").setValue(upiUpdate)
                    editTextMethod()
                    onResume()
                }
            }
            }
        }


        _binding.ChangeStatusButton.setOnClickListener {
            val currentStatus : String = _binding.DoctorPermanentStatus.text.toString()
            _binding.ChangeStatusButton.visibility = View.GONE
            _binding.ButtonSaveStatus.visibility = View.VISIBLE

            _binding.DoctorPermanentStatus.visibility = View.GONE
            _binding.DoctorChangeStatus.visibility = View.VISIBLE

            _binding.DoctorChangeStatus.setText(currentStatus)

        }

        _binding.ButtonSaveStatus.setOnClickListener {
            _binding.ChangeStatusButton.visibility = View.VISIBLE
            _binding.ButtonSaveStatus.visibility = View.GONE

            val status : String = _binding.DoctorChangeStatus.text.toString()

            when{
                TextUtils.isEmpty(_binding.DoctorChangeStatus.text) -> {
                    Toast.makeText(context, "Status Cannot be empty", Toast.LENGTH_LONG).show()
                }
                else ->{
                    if(_binding.DoctorChangeStatus.text.isNotEmpty()){
                        firebaseDatabase =  FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")
                        firebaseDatabase.child("Doctor").child(userid).child("hospitalStatus").setValue(status).addOnSuccessListener {

                            _binding.DoctorPermanentStatus.visibility = View.VISIBLE
                            _binding.DoctorChangeStatus.visibility = View.GONE

                            _binding.DoctorChangeStatus.setText(status)
                            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }

        _binding.BtnChangePasswordDoctor.setOnClickListener {
            startActivity(Intent(context, ChangePassword::class.java))
        }

        _binding.Logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(context, MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun editTextMethod(){
        //Name Settings
        _binding.DoctorPermanentName.visibility =View.VISIBLE
        _binding.ChangeDoctorName.visibility = View.GONE

        //Mobile Settings
        _binding.DoctorPermanentMobile.visibility = View.VISIBLE
        _binding.ChangeDoctorMobile.visibility = View.GONE

        //clinic Settings
        _binding.DoctorPermanentClinicName.visibility = View.VISIBLE
        _binding.ChangeDoctorClinic.visibility = View.GONE

        //Upi payment
        _binding.ChangeDoctorUpiPayment.visibility = View.GONE
        _binding.UpiPaymentPermanent.visibility = View.VISIBLE

        //button

        _binding.ButtonSaveChanges.visibility  = View.GONE
        _binding.ButtonEditProfile.visibility = View.VISIBLE

        onResume()
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val progressDialog = ProgressDialog(context)
        val imageDialog = ProgressDialog(context)

        progressDialog.setMessage("Uploading QrCode...")
        imageDialog.setMessage("Uploading Profile Picture...")

        imageDialog.setCancelable(false)
        progressDialog.setCancelable(false)

        when(requestCode){
            requestCode ->{
                if(requestCode == GALLERY){
                    if(resultCode == Activity.RESULT_OK){
                        if(data != null){
                            data.let {
                                progressDialog.show()
                                val selectedPhotoUri = data.data
                                firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
                                storageReference = FirebaseStorage.getInstance("gs://trial-38785.appspot.com").getReference("QrCodes")
                                val storageRef = storageReference!!.child(currentId.toString())
                                if (selectedPhotoUri != null) {
                                    storageRef.putFile(selectedPhotoUri).addOnSuccessListener {
                                        storageRef.downloadUrl.addOnSuccessListener {
                                            val qrcode : QrCode = QrCode(it.toString())
                                            firebaseDatabase.child("Doctor")
                                                .child(currentId.toString())
                                                .child("Qrcode").setValue(qrcode).addOnSuccessListener {
                                                    Toast.makeText(context, "QR-Code Uploaded Successfully", Toast.LENGTH_LONG).show()
                                                    progressDialog.dismiss()
                                                }.addOnFailureListener {
                                                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                                                    progressDialog.dismiss()
                                                }
                                        }
                                    }
                                }else{
                                    return@let
                                }
                            }
                        }else{
                            return
                        }
                    }else if(resultCode == Activity.RESULT_CANCELED){
                        println("result cancelled.")
                    }
                }
                if(requestCode == PROFILE){
                    if(resultCode == Activity.RESULT_OK){
                        data?.let {
                            imageDialog.show()
                            val selectedPhotoUri = data.data
                            firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
                            storageReference = FirebaseStorage.getInstance("gs://trial-38785.appspot.com").getReference("ProfilePics")
                            val storageRef = storageReference!!.child(currentId.toString())
                            if (selectedPhotoUri != null) {
                                storageRef.putFile(selectedPhotoUri).addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener {
                                        val profileImage : String = it.toString()
                                        firebaseDatabase.child("Doctor")
                                            .child(currentId.toString()).child("profilePicture").setValue(profileImage).addOnSuccessListener {
                                                _binding.ImageProfile.setImageURI(selectedPhotoUri)
                                                Toast.makeText(context, "Profile picture Uploaded Successfully", Toast.LENGTH_LONG).show()
                                                imageDialog.dismiss()
                                            }.addOnFailureListener {
                                                Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                                                imageDialog.dismiss()
                                            }
                                    }
                                }
                            }else{
                                return println("The data isn't getting stored...")
                            }
                        }
                    }else if(resultCode == Activity.RESULT_CANCELED){
                        println("result cancelled.")
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        // Create an image file name

        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image

    }

    private fun showRationalDialogForPermission(){
        AlertDialog.Builder(context).setMessage("It seems that you have declined the permissions to access the feature, " +
                "Please turn on the permission to use this feature").setPositiveButton("GO TO SETTINGS"){
                _,_ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity?.packageName , null)
                intent.data = uri
                startActivity(intent)
            }catch (e : ActivityNotFoundException){
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){dialog, _ ->
            dialog.dismiss()
        }.show()
    }


    private fun activityResult(intent : Intent, requestcode : Int){
        startActivityForResult(intent, requestcode)
    }
}