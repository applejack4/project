package com.example.project.View.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.project.View.Activities.DoctorRegistration
import com.example.project.databinding.DialogCustomListBinding
import com.example.project.databinding.DialogCustomItemBinding

class CustomItemListAdapter(private val activity : Activity,
                            private val fragment : Fragment?,
                            private val listItems : List<String>,
                            private val selectedItem : String): RecyclerView.Adapter<CustomItemListAdapter.MyViewHolder>() {

    class MyViewHolder(view: DialogCustomItemBinding): RecyclerView.ViewHolder(view.root){
        val tvText = view.tvTextItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : DialogCustomItemBinding = DialogCustomItemBinding.inflate(LayoutInflater.from(activity), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val items = listItems[position]
        holder.tvText.text = items

        holder.itemView.setOnClickListener{
            if(activity is DoctorRegistration){
                activity.selectedListItem(items, selectedItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }
}