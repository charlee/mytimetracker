package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.intelliavant.mytimetracker.databinding.FragmentWorkListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class WorkListFragment(private val date: LocalDate) : Fragment() {

    private lateinit var binding: FragmentWorkListBinding
    private val workListViewModel: WorkListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter = WorkListAdapter()

        binding = FragmentWorkListBinding.inflate(inflater, container, false)
        binding.workList.adapter = adapter

        workListViewModel.findByDate(date).observe(viewLifecycleOwner, {
            Log.d(
                "STOPWATCH",
                "WorkListFragment workListViewModel.works.observe, it=${it.toString()}"
            )
            adapter.submitList(it)
        })

        return binding.root
    }

}
