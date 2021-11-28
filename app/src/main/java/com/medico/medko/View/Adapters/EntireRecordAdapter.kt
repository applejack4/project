package com.medico.medko.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.medico.medko.Model.DetailImageModel
import com.medico.medko.R
import com.medico.medko.databinding.EntireRecordBinding
import com.squareup.picasso.Picasso

class EntireRecordAdapter(private var fragment : Fragment) : RecyclerView.Adapter<EntireRecordAdapter.MyViewHolder>() {

    var list : ArrayList<DetailImageModel> = ArrayList<DetailImageModel>()

    class MyViewHolder(view : EntireRecordBinding) : RecyclerView.ViewHolder(view.root){
        val image = view.myRecordImage
        val date = view.PermanentDateVisited
        val name = view.PermanentDoctorName
        val clinicName = view.PermanentClinicName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : EntireRecordBinding = EntireRecordBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val detailImageModel : DetailImageModel = list[position]
        holder.date.text = detailImageModel.date
        holder.name.text = detailImageModel.name
        holder.clinicName.text = detailImageModel.clinic
        fragment.context?.let { Glide.with(it).asBitmap().load(detailImageModel.Image).centerCrop().into(holder.image) }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun imageList(imgList : ArrayList<DetailImageModel>){
        list = imgList
        notifyDataSetChanged()
    }
}