package com.intelliavant.mytimetracker

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelliavant.mytimetracker.data.WorkType
import com.intelliavant.mytimetracker.databinding.FragmentSettingsWorkTypeFormBinding
import com.intelliavant.mytimetracker.viewmodel.WorkTypeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsWorkTypeFormFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsWorkTypeFormFragment()
    }

    private val workTypeListViewModel: WorkTypeListViewModel by viewModels()
    private lateinit var binding: FragmentSettingsWorkTypeFormBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSettingsWorkTypeFormBinding.inflate(inflater, container, false)
        // set default value for work type
        binding.workType = WorkType(null, "", Color.valueOf(Color.WHITE), true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val workTypeId = arguments?.getLong("workTypeId")

        workTypeId?.let {
            workTypeListViewModel.findById(it).observe(viewLifecycleOwner, { workType ->
                binding.workType = workType
            })
        }

        view.findViewById<Button>(R.id.save_work_type).setOnClickListener {
            findNavController().popBackStack()
        }
    }
}