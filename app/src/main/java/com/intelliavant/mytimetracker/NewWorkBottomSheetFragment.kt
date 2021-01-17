package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intelliavant.mytimetracker.viewmodel.WorkTypeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewWorkBottomSheetFragment : BottomSheetDialogFragment() {

    private val workTypeViewModel: WorkTypeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_work_bottom_sheet, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.work_types_recycler_view)

        workTypeViewModel.workTypes.observe(this, Observer {
            Log.d("STOPWATCH", it.toString())

            recyclerView.adapter = WorkTypeListAdapter(it)
        })

        return view
    }
}