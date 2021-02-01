package com.intelliavant.mytimetracker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.intelliavant.mytimetracker.utils.StopwatchManager
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
    private lateinit var sm: StopwatchManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sm = StopwatchManager.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_work_list_pager, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPagerAdapter = WorkListPagerAdapter(requireActivity())
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
                    val workName = workType.name

                    // Start StopwatchService
                    sm.start(workId, workName)

                    // Move to stopwatch fragment
                    findNavController().navigate(R.id.action_workListFragment_to_stopwatchFragment)
                }
            }
            fragment.show(parentFragmentManager, fragment.tag)
        }



        // Setup appbar
        // https://developer.android.com/guide/navigation/navigation-ui#support_app_bar_variations
        // https://stackoverflow.com/a/54361849/905321
        val navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        val toolbar = view.findViewById<Toolbar>(R.id.work_list_toolbar)

        // since the toolbar does not belong to the activity, we have to handle the menu creation
        // and menu click handler by ourselves
        toolbar.inflateMenu(R.menu.menu_work_list)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_share -> onShare()
                else -> NavigationUI.onNavDestinationSelected(it, navController)
            }
        }
        toolbar.setupWithNavController(navController, appBarConfiguration)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun onShare(): Boolean {

        val workListRecyclerView = requireView().findViewById<RecyclerView>(R.id.work_list_recycler_view)
        val adapter = workListRecyclerView.adapter as WorkListAdapter
        val text = adapter.getShareableText()

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

        return true
    }
}