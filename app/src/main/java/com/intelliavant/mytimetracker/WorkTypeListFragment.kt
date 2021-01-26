package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intelliavant.mytimetracker.databinding.FragmentWorkTypeListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkTypeListViewModel
import dagger.hilt.android.AndroidEntryPoint


typealias OnCreateWorkListener = OnWorkTypeClickListener

@AndroidEntryPoint
class WorkTypeListFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentWorkTypeListBinding
    private val workTypeListViewModel: WorkTypeListViewModel by viewModels()

    var onCreateWorkListener: OnCreateWorkListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter = WorkTypeListAdapter()
        adapter.onWorkTypeClickListener = { workType ->
            dismiss()
            this.onCreateWorkListener?.invoke(workType)
        }

        binding = FragmentWorkTypeListBinding.inflate(inflater, container, false)
        binding.workTypeList.adapter = adapter

        workTypeListViewModel.workTypes.observe(viewLifecycleOwner, Observer {
            Log.d("STOPWATCH", it.toString())
            adapter.submitList(it)
        })

        return binding.root
    }
}