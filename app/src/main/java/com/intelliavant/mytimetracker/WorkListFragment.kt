package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelliavant.mytimetracker.data.AppDatabase
import com.intelliavant.mytimetracker.databinding.FragmentWorkListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class WorkListFragment : Fragment() {

    private lateinit var binding: FragmentWorkListBinding
    private val workListViewModel: WorkListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter = WorkListAdapter()

        binding = FragmentWorkListBinding.inflate(inflater, container, false)
        binding.workList.adapter = adapter

        workListViewModel.works.observe(viewLifecycleOwner, {
            Log.d(
                "STOPWATCH",
                "WorkListFragment workListViewModel.works.observe, it=${it.toString()}"
            )
            adapter.submitList(it)
        })

        return binding.root
    }

}