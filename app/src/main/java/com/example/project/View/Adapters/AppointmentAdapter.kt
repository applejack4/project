package com.example.project.View.Adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.project.Model.AppointConstructor
import com.example.project.R
import com.example.project.View.Activities.PatientsDetails
import com.example.project.View.Fragments.DoctorHome
import com.example.project.databinding.UserAppointmentLayoutBinding
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
//        Picasso.get().load(appointConstructor.profilePic).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.image)
        Picasso.get().load(appointConstructor.profilePic).fit().centerInside().rotate(90F).into(holder.image)


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
                fragment.camera(appointConstructor.id.toString(), holder.adapterPosition.toString())
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