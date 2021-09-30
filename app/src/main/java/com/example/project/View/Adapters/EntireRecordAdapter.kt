package com.example.project.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.project.Model.DetailImageModel
import com.example.project.Model.ImageModel
import com.example.project.R
import com.example.project.databinding.EntireRecordBinding
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
//        Picasso.get().load(detailImageModel.Image).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.image)
        Picasso.get().load(detailImageModel.Image).fit().centerInside().rotate(90F).into(holder.image)
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