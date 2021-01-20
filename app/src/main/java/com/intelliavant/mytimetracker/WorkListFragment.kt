package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.intelliavant.mytimetracker.databinding.FragmentWorkListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint

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
