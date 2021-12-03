package com.medico.medko.View.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.medico.medko.Model.*
import com.medico.medko.View.Adapters.AppointmentAdapter
import com.medico.medko.databinding.FragmentDoctorHomeBinding
import com.medico.medko.viewModel.AppointmentViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
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
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class DoctorHome : Fragment() {
    private lateinit var firebaseDatabase : DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
   private var _binding : FragmentDoctorHomeBinding?= null
    private lateinit var appointmentAdapter : AppointmentAdapter
    private lateinit var appointmentViewModel: AppointmentViewModel
    private var storageReference: StorageReference? = null
    private var firebaseStore: FirebaseStorage? = null
    private var imageUri : Uri ?= null
    private lateinit var currentPhotoPath : String
    private lateinit var listUserId : String
    private lateinit var f : File
    var isRemembered = true
    private var rStr: String ?= null
    private lateinit var token1 : String
    private lateinit var bitmap : Bitmap
    private lateinit var dataArray : ByteArray
    private lateinit var auth : String
    private lateinit var progressDialog : ProgressDialog
    private lateinit var totalListSize : String

    companion object {
        private const val CAMERA = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val c = Calendar.getInstance()
        val monthName = arrayOf(
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"
        )
        val month = monthName[c[Calendar.MONTH]]
        val year = c[Calendar.YEAR]
        val date = c[Calendar.DATE]

        val num : String = "$date $month $year"

        auth = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val checkRef = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/")
            .getReference("AppUsers").child("Doctor")


        checkRef.child(auth)
            .child("History").child(num).child("data").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var sizeList : ArrayList<AppointConstructor> = ArrayList<AppointConstructor>()
                    if(snapshot.exists()){
                        sizeList.clear()
                        for (obj in snapshot.children){
                            val addDate = obj.getValue(AppointConstructor::class.java)
                            if (addDate != null) {
                                sizeList.add(addDate)
                            }
                        }
                        totalListSize = (sizeList.size + 1).toString()
                    }else{
                        totalListSize = (0).toString()
                        return
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                }
            })



        firebaseAuth = FirebaseAuth.getInstance()
        var firebaseToken : String ?= null
        var key : String ?= null
        val currentUser : String = firebaseAuth.currentUser?.uid.toString()
        val sdt = SimpleDateFormat("dd:MM:yyyy hh:mm:ss")
        val currentDate = sdt.format(Date())

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading image...")
        progressDialog.setCancelable(false)

        val myRef = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/")
            .getReference("AppUsers").child("Doctor").child(currentUser)

        val defaultDate = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/")
            .getReference("AppUsers").child("Doctor").child(currentUser)

        val defOnlyDate : OnlyDate = OnlyDate(num, "data")

        defaultDate.child("History").child(num).child("data").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    return
                }else{
                    defaultDate.child("History").child(num).setValue(defOnlyDate)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.i("error", error.message)
            }

        })

        val friends: MutableList<String?> = ArrayList()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                token1 = it.result.toString()
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        }

        myRef.child("Token").orderByChild("token").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                when (snapshot) {
                    snapshot -> {
                        if (snapshot.exists()) {
                            myRef.child("Token").orderByChild("token").equalTo(token1)
                                .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (obj in snapshot.children){
                                            val friend : String = obj.child("token").value.toString()
                                            friends.add(friend)
                                        }
                                    }
                                    if(friends.contains(token1)){
                                        return
                                    }else{
                                        val token : Token = Token(token1)
                                        myRef.child("Token").child(currentDate).setValue(token)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                                }

                            })
                        }
                        if (!snapshot.exists()) {
                                val token : Token = Token(token1)
                                myRef.child("Token").child(currentDate).setValue(token)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return _binding!!.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = firebaseAuth.currentUser?.uid.toString()
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
        firebaseAuth = FirebaseAuth.getInstance()
        val model : AppointmentViewModel by viewModels()
//        appointmentViewModel = ViewModelProvider(this)[AppointmentViewModel::class.java]

        firebaseDatabase.child("Doctor").child(userId).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val doctor : Doctor? = snapshot.getValue(Doctor::class.java)
                if(doctor != null){
                    _binding?.tvDoctorHomeName?.text = doctor.DoctorName
                    _binding?.tvDoctorClinicName?.text = doctor.ClinicName
                    if(_binding?.ivDoctorHomeImage != null){
                        context?.let { Glide.with(it).asBitmap().load(doctor.profilePicture).centerCrop().into(_binding!!.ivDoctorHomeImage) }
                    }else{
                        return
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error"+error.message, Toast.LENGTH_LONG).show()
            }
        })

        _binding?.Switch?.setOnClickListener {
            if(_binding!!.Switch.isChecked){
                firebaseDatabase.child("Doctor").child(userId).child("appointmentStatus").setValue("1")
                onResume()
            }  else{
                firebaseDatabase.child("Doctor").child(userId).child("appointmentStatus").setValue("0")
                onResume()
            }
        }

        firebaseDatabase.child("Doctor").child(userId).get().addOnSuccessListener {
            if(it.exists()){
                val appointmentStatus = it.child("appointmentStatus").value
                if (appointmentStatus != null) {
                    if(appointmentStatus == "1")
                        _binding!!.Switch.isChecked = true
                }else{
                    _binding!!.Switch.isChecked = false
                }
            }
        }

        appointmentAdapter = AppointmentAdapter(this)
        _binding!!.recyclerView.adapter = appointmentAdapter
        _binding!!.recyclerView.layoutManager = LinearLayoutManager(activity)

        val reference = firebaseDatabase.child("Doctor").child(userId)
        val adapter = AppointmentAdapter(this)
    }

    fun intent(string : String){
        Toast.makeText(context,string, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        val model : AppointmentViewModel by viewModels()
        val userId = firebaseAuth.currentUser?.uid.toString()
        val reference = firebaseDatabase.child("Doctor").child(userId)
        _binding!!.recyclerView.layoutManager = LinearLayoutManager(activity)
        val sdf = SimpleDateFormat("dd:MM:yyyy")
        val tf = SimpleDateFormat("hh:mm:ss")

        val job = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + job)




        reference.get().addOnSuccessListener { it ->
            if(it.exists()){
                when(it.child("appointmentStatus").value.toString()){
                    "1" ->{
                        val adapter = AppointmentAdapter(this)
                        reference.get().addOnSuccessListener { it ->
                            if(it.exists()){
                                val statusNumber = it.child("appointmentStatus").value.toString()
                                if(statusNumber == "1"){
                                    model.getUsers().observe(viewLifecycleOwner, {
                                            appointments ->
                                        kotlin.run {
                                        appointments.let {
                                            _binding!!.recyclerView.adapter = adapter
                                            when(it){
                                                it ->{
                                                    if(it.isNotEmpty() || it.size > 0){
                                                        _binding!!.recyclerView.visibility = View.VISIBLE
                                                        _binding!!.noAppsOffline.visibility = View.GONE
                                                        adapter.appointmentList(it)
                                                    }
                                                    if(it.isEmpty() || it.size == 0){
                                                        _binding!!.recyclerView.visibility = View.GONE
                                                        _binding!!.noAppsOffline.visibility = View.VISIBLE
                                                        _binding!!.noAppsOffline.text = "You have no Online Appointments."
                                                    }
                                                }
                                            }
                                        }
                                        }
                                    })
                                }
                            }
                        }
                    }
                    "0" ->{
                        val adapter = AppointmentAdapter(this)
                        model.getOfflineUsers().observe(viewLifecycleOwner, {
                                appointments ->
                            appointments.let {
                                _binding!!.recyclerView.adapter = adapter
                                when(it){
                                    it ->{
                                        if(it.isNotEmpty() || it.size > 0){
                                            _binding!!.recyclerView.visibility = View.VISIBLE
                                            _binding!!.noAppsOffline.visibility = View.GONE
                                            adapter.appointmentList(it)
                                        }
                                        if(it.isEmpty() || it.size == 0){
                                            _binding!!.recyclerView.visibility = View.GONE
                                            _binding!!.noAppsOffline.visibility = View.VISIBLE
                                            _binding!!.noAppsOffline.text = "You have no Offline Appointments."
                                        }
                                    }
                                }
                            }
                        })
                    }
                }
            }
        }
    }


     fun callPhone(string: String) {
         Dexter.withContext(context)
             .withPermission(Manifest.permission.CALL_PHONE)
             .withListener(object : PermissionListener {
                 override fun onPermissionGranted(response: PermissionGrantedResponse) {
                     val callIntent = Intent(Intent.ACTION_CALL)
                     callIntent.setData(Uri.parse("tel:$string"))
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
             }).onSameThread().check()
    }

    fun deleteItem(list : ArrayList<AppointConstructor>, position : Int, id : String){
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle("Remove Item")
        dialog.setMessage("Do you really want to remove this item?")
        dialog.setCancelable(true)
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
        val sdf = SimpleDateFormat("dd:MM:yyyy")
        val tf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date())
        val currentTime = tf.format(Date()).toString()


        val c = Calendar.getInstance()
        val monthName = arrayOf(
            "January", "February", "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"
        )
        val month = monthName[c[Calendar.MONTH]]
        val year = c[Calendar.YEAR]
        val date = c[Calendar.DATE]

        val num : String = "$date $month $year"

        val progress = ProgressDialog(context)
        progress.setMessage("Removing")
        progress.setCancelable(false)

        val userid : String = firebaseAuth.currentUser?.uid.toString()

        dialog.setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, id ->
            dialogInterface.cancel()
        })
        dialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.cancel()
            val model : AppointmentViewModel by viewModels()
            progress.show()

            val statusRef = firebaseDatabase.child("Doctor").child(userid)
            statusRef.get().addOnSuccessListener { it ->
                when(it.child("appointmentStatus").value.toString()){
                    "1" ->{
                        firebaseDatabase.child("Doctor").child(userid).child("Online_Appointment").child(id).get().addOnSuccessListener {
                            if(it.exists()){
                                val idUser : String = it.child("id").value.toString()
                                val name : String = it.child("name").value.toString()
                                val mobile : String = it.child("mobile").value.toString()
                                val profilePic : String = it.child("profilePic").value.toString()
                                val time : String = it.child("time").value.toString()
                                val savingDate : String = currentDate.toString()
                                val savingModel : AppointConstructor = AppointConstructor(idUser, name, mobile, profilePic, currentTime, currentDate)
                                val dataWrapModel = OnlyDate(num.toString(), "data")


                                firebaseDatabase.child("Doctor").child(userid).child("History").child(num).child("data").child(totalListSize.toString()).setValue(savingModel).addOnSuccessListener {
                                    model.deleteUser(position)
                                    firebaseDatabase.child("Doctor").child(userid).child("Online_Appointment").child(id).removeValue().
                                    addOnSuccessListener {
                                        appointmentAdapter = AppointmentAdapter(this@DoctorHome)
                                        val adapter = appointmentAdapter
                                        adapter.appointmentList(list)
                                        adapter.notifyItemRemoved(position)
                                        adapter.notifyItemRangeChanged(position, list.size)
                                        adapter.notifyDataSetChanged()
                                        onResume()
                                        progress.dismiss()
                                    }.addOnFailureListener {
                                        Toast.makeText(context, "Failed to remove", Toast.LENGTH_LONG).show()
                                        progress.dismiss()
                                    }
                                }.addOnFailureListener {
                                    Toast.makeText(context, "Failed to remove", Toast.LENGTH_LONG).show()
                                    progress.dismiss()
                                }

                            }
                        }
                    }
                    "0" ->{
                        firebaseDatabase.child("Doctor").child(userid).child("Offline_Appointment").child(id).get().addOnSuccessListener {
                            if(it.exists()){
                                val idUser : String = it.child("id").value.toString()
                                val name : String = it.child("name").value.toString()
                                val mobile : String = it.child("mobile").value.toString()
                                val profilePic : String = it.child("profilePic").value.toString()
                                val time : String = it.child("time").value.toString()
                                val savingDate : String = currentDate.toString()
                                val dateModel : DateModel = DateModel(num)
                                val appointModel : AppointConstructor = AppointConstructor(idUser, name, mobile, profilePic,currentTime, currentDate)
                                    firebaseDatabase.child("Doctor").child(userid).child("History")
                                        .child(idUser).setValue(appointModel)
                                        .addOnSuccessListener {
                                            model.deleteOfflineUser(position)
                                            firebaseDatabase.child("Doctor").child(userid)
                                                .child("Offline_Appointment").child(id)
                                                .removeValue().addOnSuccessListener {
                                                    appointmentAdapter = AppointmentAdapter(this)
                                                    val adapter = appointmentAdapter
                                                    adapter.appointmentList(list)
                                                    adapter.notifyItemRemoved(position)
                                                    adapter.notifyItemRangeChanged(
                                                        position,
                                                        list.size
                                                    )
                                                    adapter.notifyDataSetChanged()
                                                    onResume()
                                                    progress.dismiss()
                                                }.addOnFailureListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Failed to remove",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    progress.dismiss()
                                                }
                                        }.addOnFailureListener {
                                            Toast.makeText(
                                                context,
                                                "Failed to remove",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            progress.dismiss()
                                        }
                            }
                        }
                    }
                }
            }
        })
        dialog.show()
    }

    fun DetailIntent(){
        val intent = Intent(context, DoctorProfile::class.java)
        startActivity(intent)
    }

    fun camera(id : String){
        listUserId = id
        Dexter.withContext(context).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if(report!!.areAllPermissionsGranted()){
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if(context?.let { cameraIntent.resolveActivity(it.packageManager) } != null){
                        // Create the File where the photo should go
                        var photoFile: File? = null
                        try {
                            photoFile = createImageFile()
                        } catch (ex: IOException) {
                            // Error occurred while creating the File
                            Log.i("Error","Exception occurred is ${ex.message}")
                        }
                        if(photoFile != null){
                            val photoURI = FileProvider.getUriForFile(context!!, "com.medico.medko.android.fileProvide", photoFile)
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(cameraIntent, CAMERA)
                        }else{
                            progressDialog.dismiss()
                            return
                        }
                    }else{
                        progressDialog.dismiss()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode) {
                requestCode ->{
                    if(requestCode == CAMERA){
                        auth = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        f = File(currentPhotoPath)
                        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                        val contentUri = Uri.fromFile(f)
                        mediaScanIntent.data = contentUri
                        context!!.sendBroadcast(mediaScanIntent)
                        val sdf = SimpleDateFormat("dd-MM-YYYY")
                        val tf = SimpleDateFormat("hh:mm:ss")
                        val currentDate = sdf.format(Date()).toString()
                        val currentTime = tf.format(Date()).toString()

                        val sdfd = SimpleDateFormat("dd:MM:yyyy hh:mm:ss")
                        val CD= sdfd.format(Date())

                        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
                        storageReference = FirebaseStorage.getInstance("gs://trial-38785.appspot.com").getReference("Records")
                        val storageRef = storageReference!!.child(listUserId)
                        launchImageCrop(contentUri)
                    }

                    if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                        val resultUri = CropImage.getActivityResult(data)
                        if(resultUri == null){
                            progressDialog.dismiss()
                            return
                        }else{
                            if(Build.VERSION.SDK_INT < 29){
                                bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, resultUri.uri)
                                byteArray()
                            }else{
                                bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, resultUri.uri)
                                byteArray()
                            }
                        }
                    }
                    if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                    }

                }
            }
        }
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


    private fun createImageFile(): File {
        // Create an image file name

        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
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

    private fun launchImageCrop(uri : Uri){
        context?.let {
            CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(10, 20)
                .setMaxCropResultSize(2250, 4000)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .start(it, this)
        } ?: progressDialog.dismiss()
    }

    private fun byteArray(){
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
        dataArray = baos.toByteArray()
        setImage(dataArray)
    }

    private fun setImage(croppedImage : ByteArray){
        progressDialog.show()
        firebaseDatabase = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com").getReference("AppUsers")
        storageReference = FirebaseStorage.getInstance("gs://trial-38785.appspot.com").getReference("Records")
        val storageRef = storageReference!!.child(listUserId)

        println("Not able to set the image to firebase database")

        val sdf = SimpleDateFormat("dd-MM-YYYY")
        val tf = SimpleDateFormat("hh:mm:ss")
        val currentDate = sdf.format(Date()).toString()
        val currentTime = tf.format(Date()).toString()

        val sdfd = SimpleDateFormat("dd:MM:yyyy hh:mm:ss")
        val CD= sdfd.format(Date())

        val myRef = firebaseDatabase.child("Doctor").child(auth)
        myRef.get().addOnSuccessListener {
            val name: String = it.child("doctorName").value.toString()
            val clinic: String = it.child("clinicName").value.toString()
            val id : String = it.child("id").value.toString()
            storageRef.child(id).child(f.name).putBytes(croppedImage).addOnSuccessListener {
                storageRef.child(id).child(f.name).downloadUrl.addOnSuccessListener { img ->
                    val imageData: ImageModel = ImageModel(img.toString(), currentDate, currentTime)
                    val detailImageData : DetailImageModel = DetailImageModel(img.toString(), currentDate, currentTime, name, clinic)
                    firebaseDatabase.child("Users")
                        .child(listUserId)
                        .child("Medical_Records").child(id).child(CD)
                        .setValue(imageData).addOnSuccessListener {
                            firebaseDatabase.child("Users")
                                .child(listUserId).child("Medical_Records").child("Entire_Records").child(CD)
                                .setValue(detailImageData).addOnSuccessListener {
                                    progressDialog.dismiss()
                                    Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_LONG).show()
                                }.addOnFailureListener { exc ->
                                    progressDialog.dismiss()
                                    Toast.makeText(context, exc.message, Toast.LENGTH_LONG).show()
                                }
                        }.addOnFailureListener { exp ->
                            progressDialog.dismiss()
                            Toast.makeText(context, exp.message, Toast.LENGTH_LONG).show()
                        }
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
 }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
