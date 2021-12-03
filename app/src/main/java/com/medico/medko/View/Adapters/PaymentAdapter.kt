package com.medico.medko.View.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.medico.medko.View.Activities.PaymentActivity
import com.medico.medko.View.Fragments.Qrcode
import com.medico.medko.databinding.DialogCustomItemBinding

class PaymentAdapter(private val activity : Activity,
                     private val list: List<String>,
                     private val selectedItem : String) : RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    class ViewHolder(view : DialogCustomItemBinding) : RecyclerView.ViewHolder(view.root){
        val txtView = view.tvTextItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding : DialogCustomItemBinding = DialogCustomItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.txtView.text = item

        holder.itemView.setOnClickListener {
            if(activity is PaymentActivity){
                activity.selectedListItem(item, selectedItem)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}