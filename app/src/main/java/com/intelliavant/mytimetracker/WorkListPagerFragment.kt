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
import androidx.viewpager2.widget.ViewPager2
import com.intelliavant.mytimetracker.data.AppDatabase
import com.intelliavant.mytimetracker.databinding.FragmentWorkListBinding
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class WorkListPagerFragment : Fragment() {

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_work_list_pager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activity?.let { activity ->
            val viewPagerAdapter = WorkListPagerAdapter(activity)
            viewPager = view.findViewById(R.id.view_pager)
            viewPager.adapter = viewPagerAdapter
        }

        super.onViewCreated(view, savedInstanceState)
    }
}