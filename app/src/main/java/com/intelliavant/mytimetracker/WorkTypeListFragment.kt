package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intelliavant.mytimetracker.databinding.FragmentWorkTypeListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import com.intelliavant.mytimetracker.viewmodel.WorkTypeListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkTypeListFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentWorkTypeListBinding
    private val workTypeListViewModel: WorkTypeListViewModel by viewModels()
    private val workListViewModel: WorkListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter = WorkTypeListAdapter()
        adapter.setOnWorkTypeClickListener { workType ->
            Log.d("STOPWATCH", "workType ${workType.id} clicked")
            lifecycleScope.launch {
                val workId = workListViewModel.createWork(workType)

                // Start StopwatchActivity
                val bundle = bundleOf("workId" to workId)
                findNavController().navigate(R.id.action_workListFragment_to_stopwatchFragment, bundle)

                // Close bottomsheet
                dismiss()
            }
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