package com.intelliavant.mytimetracker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.intelliavant.mytimetracker.data.WorkType
import com.intelliavant.mytimetracker.databinding.WorkTypeListItemBinding
import com.intelliavant.mytimetracker.viewmodel.WorkTypeListViewModel
import com.intelliavant.mytimetracker.viewmodel.WorkTypeViewModel

class WorkTypeListAdapter(private val workTypes: List<WorkType>) :
    ListAdapter<WorkType, WorkTypeListAdapter.ViewHolder>(
        WorkTypeDiffCallback()
    ) {

    class ViewHolder(private val binding: WorkTypeListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener { view ->
                binding.viewModel?.id.let { workTypeId ->
                    Log.d("STOPWATCH", "work type $workTypeId clicked")
                }
            }
        }

        fun bind(workType: WorkType) {
            with(binding) {
                viewModel = WorkTypeViewModel(workType)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.work_type_list_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount() = workTypes.size
}

private class WorkTypeDiffCallback : DiffUtil.ItemCallback<WorkType>() {

    override fun areContentsTheSame(oldItem: WorkType, newItem: WorkType): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: WorkType, newItem: WorkType): Boolean {
        return oldItem.id == newItem.id
    }
}