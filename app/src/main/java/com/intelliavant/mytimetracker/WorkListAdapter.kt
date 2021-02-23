package com.intelliavant.mytimetracker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelliavant.mytimetracker.data.Work
import com.intelliavant.mytimetracker.data.WorkWithWorkType
import com.intelliavant.mytimetracker.databinding.WorkListItemBinding
import com.intelliavant.mytimetracker.utils.formatTime


typealias OnWorkClickListener = (workId: Long) -> Unit

class WorkListAdapter() :
    ListAdapter<WorkWithWorkType, WorkListAdapter.ViewHolder>(
        WorkDiffCallback()
    ) {

    var dateText: String = ""

    var onWorkClickListener: OnWorkClickListener? = null

    inner class ViewHolder(private val binding: WorkListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                binding.work?.id?.let {
                    // TODO
                    onWorkClickListener?.invoke(it)
                }
            }

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