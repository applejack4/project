package com.medico.medko.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.medico.medko.Model.ReviewMode
import com.medico.medko.R
import com.medico.medko.View.Fragments.DoctorReview
import com.medico.medko.databinding.ReviewlayoutBinding
import com.squareup.picasso.Picasso

class ReviewAdapter(private val fragment: Fragment) : RecyclerView.Adapter<ReviewAdapter.MyViewHolder>() {

    var list : ArrayList<ReviewMode> = ArrayList<ReviewMode>()

    class MyViewHolder(view: ReviewlayoutBinding) : RecyclerView.ViewHolder(view.root){
        val image = view.reviewImage
        val name = view.reviewName
        val message = view.reviewMessage

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding : ReviewlayoutBinding = ReviewlayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val review : ReviewMode = list[position]
        holder.name.text = review.name
        holder.message.text = review.review
        Picasso.get().load(review.profilePic)?.fit()?.centerInside()?.rotate(90F)?.placeholder(R.drawable.ic_baseline_account_circle_24)?.into(holder.image)

        holder.itemView.setOnClickListener {
            if(fragment is DoctorReview){
//                review.id?.let { it1 -> fragment.deleteReview(list, holder.adapterPosition, it1, review.name.toString()) }
                fragment.deleteReview(list, holder.adapterPosition, review.id.toString(), review.name.toString())
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun reviewList(reviewList : ArrayList<ReviewMode>){
        list = reviewList
        notifyDataSetChanged()
    }
}