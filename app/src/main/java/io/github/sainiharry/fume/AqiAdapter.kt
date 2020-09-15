package io.github.sainiharry.fume

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import io.github.sainiharry.fume.repository.AqiData

private val listDiffer = object : DiffUtil.ItemCallback<AqiData>() {
    override fun areItemsTheSame(oldItem: AqiData, newItem: AqiData): Boolean =
        oldItem.timestamp == newItem.timestamp

    override fun areContentsTheSame(oldItem: AqiData, newItem: AqiData): Boolean =
        oldItem == newItem
}

class AqiAdapter : ListAdapter<AqiData, AqiViewHolder>(listDiffer) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AqiViewHolder =
        AqiViewHolder(parent)

    override fun onBindViewHolder(holder: AqiViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}