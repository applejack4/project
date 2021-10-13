package com.medico.medko.View.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.medico.medko.Model.SliderItem
import com.medico.medko.R
import com.makeramen.roundedimageview.RoundedImageView

class SliderAdapter internal constructor(sliderItem : MutableList<SliderItem>, viewpager : ViewPager2) :
    RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private val sliderItem : List<SliderItem>
    private val viewPager2 : ViewPager2

    init {
        this.sliderItem = sliderItem
        this.viewPager2 = viewpager
    }

    class SliderViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val imageview : RoundedImageView = view.findViewById(R.id.ImageSlide)


        fun image(sliderItem : SliderItem){
            imageview.setImageResource(sliderItem.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.slideritemcontainer, parent, false))
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.image(sliderItem[position])
        if(position == sliderItem.size - 2){
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return sliderItem.size
    }

    @SuppressLint("NotifyDataSetChanged")
    private val runnable = Runnable{
        sliderItem.addAll(sliderItem)
        notifyDataSetChanged()
    }
}