package com.medico.medko.Services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.medico.medko.Constants.AppConstants
import com.medico.medko.R
import com.medico.medko.View.Activities.DoctorMainPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.medico.medko.Model.Token
import com.medico.medko.View.Activities.UserRecords
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private var auth: FirebaseAuth  = FirebaseAuth.getInstance()
    private val currentId : String = auth.currentUser?.uid.toString()

    override fun onNewToken(token : String) {
        super.onNewToken(token)
        updateToken(token)
    }

    override fun onMessageReceived(remoteMessage : RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.data.isNotEmpty()) {

            val map: Map<String, String> = remoteMessage.data

            val title = map["title"]
            val message = map["message"]
            val hisId = map["hisId"]

            when(message.toString()){
                AppConstants.BOOKED_APPOINTMENT ->{
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                        createOreoNotification(title.toString(), message!!, hisId!!)
                    } else {
                        createNormalNotification(title!!, message!!, hisId!!)
                    }
                }
                AppConstants.REVIEW ->{
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                        createReviewOreoNotification(title.toString(), message!!, hisId!!)
                    } else {
                        createReviewNormalNotification(title.toString(), message!!, hisId!!)
                    }
                }
                AppConstants.PRESCRIPTION ->{
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
                        println("Current version is greater than Android Oreo${Build.VERSION.SDK_INT.toString()}")
                        createRecordOreoNotification(title.toString(), message!!, hisId!!)
                    } else {
                        println("Current version is Lower than Android Oreo${Build.VERSION.SDK_INT.toString()}")
                        createRecordNormalNotification(title.toString(), message!!, hisId!!)
                    }
                }
            }
        }
    }

    private fun createRecordNormalNotification(title: String, message: String, hisId: String) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.check)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setSound(uri)

        val intent = Intent(this, UserRecords::class.java)

        intent.putExtra("hisId", hisId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())
    }

    private fun createRecordOreoNotification(title : String, message: String, hisId: String) {
        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, UserRecords::class.java)

        intent.putExtra("hisId", hisId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.check)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }

    private fun createReviewNormalNotification(title : String, message: String, hisId: String) {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.check)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setSound(uri)

        val intent = Intent(this, DoctorMainPage::class.java)

        intent.putExtra("hisId", hisId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())
    }

    private fun createReviewOreoNotification(title : String, message: String, hisId: String) {
        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, DoctorMainPage::class.java)

        intent.putExtra("hisId", hisId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.check)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }

    @SuppressLint("SimpleDateFormat")
    fun updateToken(token : String){
        val databaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/")
            .getReference("AppUsers").child("Doctor")
            .child(currentId).child("Token").orderByChild("token")
        val currentUser : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val sdt = SimpleDateFormat("dd:MM:yyyy hh:mm:ss")
        val currentDate = sdt.format(Date())

        val myRef = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/")
            .getReference("AppUsers").child("Doctor").child(currentUser)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                when(snapshot){
                    snapshot ->{
                        if(snapshot.exists()){
                            val tok : Token = Token(token)
                            myRef.child("Token").child(currentDate).setValue(tok)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
            }

        })


    }


    @SuppressLint("UnspecifiedImmutableFlag")
    private fun createNormalNotification(
        title: String,
        message: String,
        hisId: String
    ) {

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(this, AppConstants.CHANNEL_ID)
        builder.setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(R.drawable.check)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setSound(uri)


        val intent = Intent(this, DoctorMainPage::class.java)

        intent.putExtra("hisId", hisId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pendingIntent)
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(Random().nextInt(85 - 65), builder.build())
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createOreoNotification(
        title: String,
        message: String,
        hisId: String
    ) {

        val channel = NotificationChannel(
            AppConstants.CHANNEL_ID,
            "Message",
            NotificationManager.IMPORTANCE_HIGH
        )

        channel.setShowBadge(true)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)

        val intent = Intent(this, DoctorMainPage::class.java)

        intent.putExtra("hisId", hisId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val notification = Notification.Builder(this, AppConstants.CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.check)
            .setAutoCancel(true)
            .setColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            .setContentIntent(pendingIntent)
            .build()

        manager.notify(100, notification)
    }
}