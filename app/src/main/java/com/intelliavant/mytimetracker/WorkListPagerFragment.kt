package com.intelliavant.mytimetracker

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.intelliavant.mytimetracker.utils.StopwatchServiceUtils
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
@AndroidEntryPoint
class WorkListPagerFragment : Fragment() {

    private val workListViewModel: WorkListViewModel by viewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var dateTabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_work_list_pager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPagerAdapter = WorkListPagerAdapter(requireActivity())
        viewPagerAdapter.onResumeWorkListener = { work ->
            Log.d("STOPWATCH", "work ${work.id} clicked")

            // Only allow resuming today's activity
            if (work.date.isEqual(LocalDate.now())) {
                work.id?.let { workId ->
                    lifecycleScope.launch {
                        // Start StopwatchService
                        StopwatchServiceUtils.startStopwatchService(requireContext(), workId)

                        // Move to stopwatch fragment
                        findNavController().navigate(R.id.action_workListFragment_to_stopwatchFragment)
                    }
                }
            }

        }

        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = viewPagerAdapter
        viewPager.setCurrentItem(viewPagerAdapter.itemCount - 1, false)

        dateTabLayout = view.findViewById(R.id.date_tab_layout)
        TabLayoutMediator(dateTabLayout, viewPager) { tab, position ->
            val lastIndex = viewPagerAdapter.itemCount - 1

            val date = LocalDate.now().minusDays((lastIndex - position).toLong())

            val dateText = when (position) {
                lastIndex -> getString(R.string.today)
                lastIndex - 1 -> getString(R.string.yesterday)
                else -> date.toString()
            }

            val weekdayText = date.format(DateTimeFormatter.ofPattern("E"))

            tab.text = "$dateText ($weekdayText)"
        }.attach()

        // Setup FAB
        // show the work type bottom sheet when FAB is clicked
        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val fragment = WorkTypeListFragment()
            fragment.onCreateWorkListener = { workType ->
                Log.d("STOPWATCH", "workType ${workType.id} clicked")
                lifecycleScope.launch {
                    val workId = workListViewModel.createWork(workType.name, workType)

                    // Start StopwatchService
                    StopwatchServiceUtils.startStopwatchService(requireContext(), workId)

                    // Move to stopwatch fragment
                    findNavController().navigate(R.id.action_workListFragment_to_stopwatchFragment)
                }
            }
            fragment.show(parentFragmentManager, fragment.tag)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_work_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}