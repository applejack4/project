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
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.project.Model.Doctor
import com.example.project.Model.QrCode
import com.example.project.Model.User
import com.example.project.Model.profileImage
import com.example.project.View.Activities.ChangePassword
import com.example.project.View.Activities.MainActivity
import com.example.project.databinding.FragmentSettingsBinding
import com.example.project.viewModel.NotificationsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class SettingFragment : Fragment() {
    private var currentPhotoPath : String ?= null
    private var storageReference : StorageReference?= null
    private lateinit var firebaseDatabase : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    companion object{
        const val GALLERY = 2
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

      firebaseAuth = FirebaseAuth.getInstance()

    _binding = FragmentSettingsBinding.inflate(inflater, container, false)
    val root: View = binding.root
    return root
  }

    override fun onResume() {
        super.onResume()

        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
        firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid

        _binding?.changeProfilePicture?.setOnClickListener {
            Dexter.withContext(context).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(object :
                MultiplePermissionsListener {
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
                                startActivityForResult(galIntent, GALLERY)
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

        if (userId != null) {
            firebaseDatabase.child("Users").child(userId.toString()).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user : User? = snapshot.getValue(User::class.java)
                        if (user != null) {
                            _binding!!.UserPermanentName.text = user.firstname
                            _binding!!.UserPermanentMobile.text = user.mobile
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("TAG", error.message)
                }

            })
        }


        _binding!!.bTnEditProfile.setOnClickListener {
            val name : String = _binding!!.UserPermanentName.text.toString()
            val mobile : String = _binding!!.UserPermanentMobile.text.toString()
            //name Settings
            _binding!!.UserPermanentName.visibility = View.GONE
            _binding!!.ChangeUserName.visibility = View.VISIBLE

            //mobile settings
            _binding!!.UserPermanentMobile.visibility = View.GONE
            _binding!!.ChangeUserMobile.visibility = View.VISIBLE

            //button settings

            _binding!!.bTnEditProfile.visibility = View.GONE
            _binding!!.bTnSaveChanges.visibility = View.VISIBLE

            _binding!!.ChangeUserName.setText(name)
            _binding!!.ChangeUserMobile.setText(mobile)

            onResume()
        }

        _binding!!.bTnSaveChanges.setOnClickListener {
                val name : String = _binding!!.ChangeUserName.text.toString()
                val mobile : String = _binding!!.ChangeUserMobile.toString()
            val userid : String  = FirebaseAuth.getInstance().currentUser?.uid.toString()

                when{
                    TextUtils.isEmpty(_binding!!.ChangeUserName.text) ->{
                        Toast.makeText(context, "Name Cannot be empty", Toast.LENGTH_LONG).show()
                    }
                    TextUtils.isEmpty(_binding!!.ChangeUserMobile.text) ->{
                        Toast.makeText(context, "Mobile Cannot be empty", Toast.LENGTH_LONG).show()
                    }
                    else ->{
                        if(_binding!!.ChangeUserName.text.isNotEmpty() && _binding!!.ChangeUserMobile.text.isNotEmpty()){
                            firebaseDatabase =  FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")

                            val nameUpdate : String = _binding!!.ChangeUserName.text.toString()
                            val mobileUpdate : String = _binding!!.ChangeUserMobile.text.toString()


                            firebaseDatabase.child("Users").child(userid).child("firstname").setValue(nameUpdate)
                            firebaseDatabase.child("Users").child(userid).child("mobile").setValue(mobileUpdate)
                            editTextMethod()
                            onResume()
                        }
                    }
                }
        }

        _binding!!.BtnChangePassword.setOnClickListener {
            startActivity(Intent(context, ChangePassword::class.java))
        }

        _binding!!.BtnLogout.setOnClickListener {
            firebaseAuth.signOut()
            activity?.finish()
            startActivity(Intent(context, MainActivity::class.java))
        }
    }

    fun editTextMethod(){
        //name Settings
        _binding!!.ChangeUserName.visibility = View.GONE
        _binding!!.UserPermanentName.visibility = View.VISIBLE

        //mobile settings
        _binding!!.ChangeUserMobile.visibility = View.GONE
        _binding!!.UserPermanentMobile.visibility = View.VISIBLE

        //button change
        _binding!!.bTnSaveChanges.visibility = View.GONE
        _binding!!.bTnEditProfile.visibility  = View.VISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading Profile Picture...")
        progressDialog.setCancelable(false)
        firebaseAuth = FirebaseAuth.getInstance()
        val currentId : String = firebaseAuth.currentUser?.uid.toString()

        if(requestCode == DoctorProfile.GALLERY){
            if(resultCode == Activity.RESULT_OK){
                if(data != null){
                    data.let {
                        progressDialog.show()
                        val selectedPhotoUri = data.data
                        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
                        storageReference = FirebaseStorage.getInstance("gs://trial-38785.appspot.com").getReference("ProfilePics")
                        val storageRef = storageReference!!.child(currentId.toString())
                        if (selectedPhotoUri != null) {
                            storageRef.putFile(selectedPhotoUri).addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener {
                                    val image : profileImage = profileImage(it.toString())
                                    firebaseDatabase.child("Users")
                                        .child(currentId.toString())
                                        .child("profilePicture").setValue(image).addOnSuccessListener {
                                            Toast.makeText(context, "Profile Picture Uploaded Successfully", Toast.LENGTH_LONG).show()
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
    }


override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}