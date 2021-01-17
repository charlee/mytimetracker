package com.intelliavant.mytimetracker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.intelliavant.mytimetracker.data.WorkType

class WorkTypeListAdapter(private val workTypes: List<WorkType>): RecyclerView.Adapter<WorkTypeListAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.work_type_list_item, parent, false)
        view.setOnClickListener {
            Log.d("STOPWATCH", it.toString() + " clicked")
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = workTypes[position].name
    }

    override fun getItemCount() = workTypes.size
}