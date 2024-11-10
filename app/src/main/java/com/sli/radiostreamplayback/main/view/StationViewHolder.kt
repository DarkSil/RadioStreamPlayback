package com.sli.radiostreamplayback.main.view

import android.view.View.OnClickListener
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.sli.radiostreamplayback.databinding.MainMenuItemBinding
import com.sli.radiostreamplayback.main.model.RadioStation

class StationViewHolder(private val binding: MainMenuItemBinding) : ViewHolder(binding.root) {

    fun setItem(station: RadioStation) {
        Glide
            .with(binding.root)
            .load(station.imgUrl)
            .into(binding.imageLogo)

        binding.textName.text = station.name
        binding.textDescription.text = station.description
    }

    fun setOnClickListener(listener: OnClickListener) {
        binding.root.setOnClickListener(listener)
    }

    fun setListenClickListener(listener: OnClickListener) {
        binding.buttonListen.setOnClickListener(listener)
    }

}