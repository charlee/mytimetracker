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
import com.intelliavant.mytimetracker.databinding.FragmentWorkTypeListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkTypeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkTypeListFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentWorkTypeListBinding
    private val workTypeListViewModel: WorkTypeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentWorkTypeListBinding.inflate(inflater, container, false)
        val adapter = WorkTypeListAdapter()
        binding.workTypeList.adapter = adapter

        workTypeListViewModel.workTypes.observe(this, Observer {
            Log.d("STOPWATCH", it.toString())
            adapter.submitList(it)
        })

        return binding.root
    }
}