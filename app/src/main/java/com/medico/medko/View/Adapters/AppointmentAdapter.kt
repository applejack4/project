package com.medico.medko.View.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medico.medko.Model.AppointConstructor
import com.medico.medko.View.Activities.PatientsDetails
import com.medico.medko.View.Fragments.DoctorHome
import com.medico.medko.databinding.UserAppointmentLayoutBinding
import com.squareup.picasso.Picasso

class AppointmentAdapter(private val fragment : Fragment) :
    RecyclerView.Adapter<AppointmentAdapter.MyViewHolder>() {

    private var list : ArrayList<AppointConstructor> = ArrayList<AppointConstructor>()

    class MyViewHolder(view : UserAppointmentLayoutBinding) : RecyclerView.ViewHolder(view.root){
        val image = view.appointmentImage
        val name = view.tvNamem
        val time = view.Time
        val call = view.Call
        val camera = view.Camera
        val remove = view.Cancel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val binding : UserAppointmentLayoutBinding = UserAppointmentLayoutBinding.inflate(
                LayoutInflater.from(fragment.context), parent, false)
            return MyViewHolder(binding)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val appointConstructor : AppointConstructor = list[position]
        holder.name.text = appointConstructor.name
        holder.time.text = appointConstructor.Time
        fragment.context?.let { Glide.with(it).load(appointConstructor.profilePic).centerCrop().into(holder.image) }

        holder.call.setOnClickListener {
            if(fragment is DoctorHome){
                val number : String = appointConstructor.mobile.toString()
                fragment.callPhone(number)
            }
        }
            holder.remove.setOnClickListener {
                if(fragment is DoctorHome){
                    fragment.deleteItem(list, holder.adapterPosition, appointConstructor.id.toString())
            }
        }

        holder.itemView.setOnClickListener{
            val intent = Intent(it.context, PatientsDetails::class.java)
            intent.putExtra("name", appointConstructor.name)
            intent.putExtra("profilePicture", appointConstructor.profilePic)
            intent.putExtra("mobile", appointConstructor.mobile)
            intent.putExtra("id", appointConstructor.id)
            it.context.startActivity(intent)
        }

        holder.camera.setOnClickListener {
            if(fragment is DoctorHome){
                fragment.camera(appointConstructor.id.toString())
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun appointmentList(appointList: ArrayList<AppointConstructor>){
        list = appointList
        notifyDataSetChanged()
    }
}