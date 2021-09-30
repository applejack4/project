package com.example.project.View.Adapters


import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.project.Model.AppointConstructor
import com.example.project.Model.ImageModel
import com.example.project.R
import com.example.project.databinding.DoctorHistoryItemBinding
import com.example.project.databinding.MyRecordsBinding
import com.google.firebase.database.core.view.View
import com.squareup.picasso.Picasso

class MyRecordAdapter : RecyclerView.Adapter<MyRecordAdapter.MyViewHolder>() {

    var list : ArrayList<ImageModel> = ArrayList<ImageModel>()
    var fragment : Fragment = Fragment()
    var activity : Activity = Activity()

    class MyViewHolder(view : MyRecordsBinding) : RecyclerView.ViewHolder(view.root){
        val image = view.myRecordImage
        val date = view.DateVisited
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
    val binding : MyRecordsBinding = MyRecordsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imageModel : ImageModel = list[position]
        holder.date.text = imageModel.date
        Picasso.get().load(imageModel.Image).placeholder(R.drawable.ic_baseline_account_circle_24).into(holder.image)
        Picasso.get().load(imageModel.Image).fit().centerInside().rotate(90F).into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun imageList(imgList : ArrayList<ImageModel>){
        list = imgList
        notifyDataSetChanged()
    }

    fun passingFragment(frag: Fragment){
        fragment = frag
    }

    fun passingActivity(act : Activity){
        activity = act
    }
}