package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
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

        // If any work type is clicked, invoke the edit form
        adapter.onWorkTypeClickListener = { workType ->

            val bundle = bundleOf("workTypeId" to workType.id)
            findNavController().navigate(R.id.action_settingsWorkTypeListFragment_to_settingsWorkTypeFormFragment, bundle)
        }

        binding = FragmentSettingsWorkTypeListFragmentBinding.inflate(inflater, container, false)
        binding.workTypeList.adapter = adapter

        workTypeListViewModel.workTypes.observe(viewLifecycleOwner, {
            Log.d("STOPWATCH", it.toString())
            adapter.submitList(it)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // Setup FAB
        view.findViewById<FloatingActionButton>(R.id.settings_work_type_list_fab).setOnClickListener {
//            val fragment = SettingsWorkTypeFormFragment()
//            val transaction = parentFragmentManager.beginTransaction()
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//            transaction.add(android.R.id.content, fragment)
//                .addToBackStack(null)
//                .commit()

            findNavController().navigate(R.id.action_settingsWorkTypeListFragment_to_settingsWorkTypeFormFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }


}
