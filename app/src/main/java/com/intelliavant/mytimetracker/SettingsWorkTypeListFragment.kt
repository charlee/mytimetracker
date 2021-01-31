package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelliavant.mytimetracker.databinding.FragmentSettingsWorkTypeListFragmentBinding
import com.intelliavant.mytimetracker.viewmodel.WorkTypeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsWorkTypeListFragment: Fragment() {

    private lateinit var binding: FragmentSettingsWorkTypeListFragmentBinding
    private val workTypeListViewModel: WorkTypeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val adapter = WorkTypeListAdapter()

        binding = FragmentSettingsWorkTypeListFragmentBinding.inflate(inflater, container, false)
        binding.workTypeList.adapter = adapter

        workTypeListViewModel.workTypes.observe(viewLifecycleOwner, {
            Log.d("STOPWATCH", it.toString())
            adapter.submitList(it)
        })

        return binding.root
    }
}
