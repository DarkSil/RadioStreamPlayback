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
    private var itemClickListener: ItemClickListener? = null
    private var listenClickListener: ItemClickListener? = null

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
            itemClickListener?.onItemClick(list[position])
        }
        listenClickListener?.let { listener ->
            holder.setListenClickListener {
                listener.onItemClick(list[position])
            }
        }
    }

    fun setList(list: List<RadioStation>) {
        val size = this.list.size

        this.list.clear()
        this.list.addAll(list)

        // TODO Make it smooth and beautiful

        notifyItemRangeChanged(0, list.size)
        notifyItemRangeRemoved(list.size, size - list.size)
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        itemClickListener = listener
    }

    fun setListenClickListener(listener: ItemClickListener) {
        this.listenClickListener = listener
    }
}