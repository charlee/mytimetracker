package com.intelliavant.mytimetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelliavant.mytimetracker.data.Work
import com.intelliavant.mytimetracker.databinding.WorkListItemBinding
import com.intelliavant.mytimetracker.viewmodel.WorkViewModel

class WorkListAdapter() :
    ListAdapter<Work, WorkListAdapter.ViewHolder>(
        WorkDiffCallback()
    ) {

    class ViewHolder(private val binding: WorkListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
        }

        fun bind(work: Work) {
            binding.work = work
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
}


private class WorkDiffCallback : DiffUtil.ItemCallback<Work>() {
    override fun areContentsTheSame(oldItem: Work, newItem: Work): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Work, newItem: Work): Boolean {
        return oldItem == newItem
    }
}