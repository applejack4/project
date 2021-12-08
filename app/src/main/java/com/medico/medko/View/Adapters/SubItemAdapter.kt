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
import com.squareup.picasso.Picasso

class SubItemAdapter : RecyclerView.Adapter<SubItemAdapter.MyViewHolder>() {

    var list : ArrayList<AppointConstructor> = ArrayList<AppointConstructor>()
    var fragment : Fragment = Fragment()

    class MyViewHolder(view : DoctorHistoryItemBinding) : RecyclerView.ViewHolder(view.root){
        val image = view.historyImage
        val name = view.HistoryName
        val call = view.HistoryCall
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
       val binding : DoctorHistoryItemBinding = DoctorHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val subItem : AppointConstructor = list[position]
        holder.name.text = subItem.name

        holder.call.setOnClickListener {
            val number : String = subItem.mobile.toString()
            if(fragment is DoctorHistory){
                (fragment as DoctorHistory).callUser(number)
            }
        }

        Picasso.get().load(subItem.profilePic).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.image)
        fragment.context?.let {
            Glide.with(it).asBitmap().load(subItem.profilePic).fitCenter()
                .placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun subItemList(subItemList : ArrayList<AppointConstructor>){
        list = subItemList
        notifyDataSetChanged()
    }

    fun passingFragment(frag : Fragment){
        fragment = frag
    }
}