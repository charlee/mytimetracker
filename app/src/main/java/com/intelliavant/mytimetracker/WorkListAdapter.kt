package com.intelliavant.mytimetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelliavant.mytimetracker.data.WorkWithWorkType
import com.intelliavant.mytimetracker.databinding.WorkListItemBinding
import com.intelliavant.mytimetracker.utils.formatTime

class WorkListAdapter() :
    ListAdapter<WorkWithWorkType, WorkListAdapter.ViewHolder>(
        WorkDiffCallback()
    ) {

    var dateText: String = ""

    class ViewHolder(private val binding: WorkListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
        }

        fun bind(work: WorkWithWorkType) {
            binding.work = work.work
            binding.workListItemBorder.setBackgroundColor(work.workType.color.toArgb())
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.work_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getShareableText(): String {
        val text =
            currentList.joinToString("\n") { work -> "${work.work.name}: ${formatTime(work.work.duration)}" }
        return "$dateText\n$text"
    }
}


private class WorkDiffCallback : DiffUtil.ItemCallback<WorkWithWorkType>() {
    override fun areContentsTheSame(oldItem: WorkWithWorkType, newItem: WorkWithWorkType): Boolean {
        return oldItem.work.id == newItem.work.id
    }

    override fun areItemsTheSame(oldItem: WorkWithWorkType, newItem: WorkWithWorkType): Boolean {
        return oldItem.work == newItem.work
    }
}