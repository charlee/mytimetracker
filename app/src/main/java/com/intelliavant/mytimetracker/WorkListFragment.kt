package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.intelliavant.mytimetracker.databinding.FragmentWorkListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class WorkListFragment() : Fragment() {

    private lateinit var binding: FragmentWorkListBinding
    private val workListViewModel: WorkListViewModel by viewModels()

    var onWorkClickListener: OnWorkClickListener?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val adapter = WorkListAdapter()

        adapter.onWorkClickListener = onWorkClickListener

        binding = FragmentWorkListBinding.inflate(inflater, container, false)
        binding.isWorkListVisible = false
        binding.workListRecyclerView.adapter = adapter

        arguments?.getInt("daysBack")?.let {
            val date = LocalDate.now().minusDays(it.toLong())

            workListViewModel.findByDate(date).observe(viewLifecycleOwner, { workList ->
                binding.isWorkListVisible = workList.isNotEmpty()
                adapter.submitList(workList)
                adapter.dateText = date.toString()
            })
        }

        return binding.root
    }
}
