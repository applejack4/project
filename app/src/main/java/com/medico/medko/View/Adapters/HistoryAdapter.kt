package com.medico.medko.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medico.medko.Model.AppointConstructor
import com.medico.medko.R
import com.medico.medko.View.Fragments.DoctorHistory
import com.medico.medko.databinding.DoctorHistoryItemBinding


class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.MyViewHolder>() {

    private var list : ArrayList<AppointConstructor> = ArrayList<AppointConstructor>()
    var fragment : Fragment = Fragment()

    class MyViewHolder(view : DoctorHistoryItemBinding) : RecyclerView.ViewHolder(view.root){
        val image = view.historyImage
        val name = view.HistoryName
        val call = view.HistoryCall
        val date = view.HistoryDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : DoctorHistoryItemBinding = DoctorHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val appointModel : AppointConstructor = list[position]
        holder.name.text = appointModel.name
        holder.date.text = appointModel.date
        fragment.context.let {
            if (it != null) {
                Glide.with(it).load(appointModel.profilePic).centerCrop().placeholder(
                    R.drawable.ic_baseline_account_circle_24).into(holder.image)
            }
        }
        holder.call.setOnClickListener {
            if(fragment is DoctorHistory){
                val number : String = appointModel.mobile.toString()
                (fragment as DoctorHistory).callUser(number)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun appointmentList(historyList: ArrayList<AppointConstructor>){
        list = historyList
        notifyDataSetChanged()
    }

    fun passingFragment(frag : Fragment){
        fragment = frag
    }
}