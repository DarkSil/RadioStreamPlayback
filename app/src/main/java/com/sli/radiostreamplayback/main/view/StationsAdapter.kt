package com.sli.radiostreamplayback.main.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.sli.radiostreamplayback.databinding.MainMenuItemBinding
import com.sli.radiostreamplayback.main.model.RadioStation

class StationsAdapter : Adapter<StationViewHolder>() {

    fun interface ItemClickListener {
        fun onItemClick(station: RadioStation)
    }

    private val list = arrayListOf<RadioStation>()
    private var clickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationViewHolder {
        return StationViewHolder(
            MainMenuItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: StationViewHolder, position: Int) {
        holder.setItem(list[position])
        holder.setOnClickListener {
            clickListener?.onItemClick(list[position])
        }
    }

    fun setList(list: List<RadioStation>) {
        val size = this.list.size
        val savedItems = this.list.toList()

        this.list.clear()
        this.list.addAll(list)
        this.list.forEachIndexed { index, item ->
            val oldItem = savedItems.find { it.id == item.id }
            if (oldItem != null) {
                notifyItemMoved(savedItems.indexOf(oldItem), index)
            } else {
                notifyItemChanged(index)
            }
        }

        notifyItemRangeRemoved(list.size, size - list.size)
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        clickListener = listener
    }
}