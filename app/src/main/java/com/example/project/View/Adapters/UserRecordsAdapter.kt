package com.example.project.View.Adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.project.Model.Doctor
import com.example.project.R
import com.example.project.View.Activities.Booking
import com.example.project.View.Activities.UserRecords
import com.example.project.databinding.SearchviewItemBinding
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserRecordsAdapter : RecyclerView.Adapter<UserRecordsAdapter.MyViewHolder>() {

    var list : ArrayList<Doctor> = ArrayList<Doctor>()
    var activity : Activity = Activity()
    var fragment : Fragment = Fragment()

    class MyViewHolder(view  : SearchviewItemBinding) : RecyclerView.ViewHolder(view.root){
        val doctorName : TextView = itemView.findViewById(R.id.tv_doctorName_inspection)
        val hospitalName : TextView = itemView.findViewById(R.id.tv_Hospital_name)
        val image : CircleImageView = itemView.findViewById(R.id.circleImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : SearchviewItemBinding = SearchviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val doctor : Doctor = list[position]
        holder.hospitalName.text= doctor.ClinicName
        holder.doctorName.text = doctor.DoctorName
        Picasso.get().load(doctor.profilePicture).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.image)

        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, UserRecords::class.java)
            intent.putExtra("Doctor_name", doctor.DoctorName)
            intent.putExtra("Clinic_name", doctor.ClinicName)
            intent.putExtra("Speciality", doctor.Speciality)
            intent.putExtra("profilePic", doctor.profilePicture)
            intent.putExtra("id", doctor.id)
            intent.putExtra("mobile", doctor.mobile)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun passingList(docList : ArrayList<Doctor>){
        list = docList
    }

    fun passingFragment(frag : Fragment){
        fragment = frag
    }

    fun passingActivity(act : Activity){
        activity = act
    }

}