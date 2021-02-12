package com.intelliavant.mytimetracker

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.intelliavant.mytimetracker.utils.StopwatchManager
import com.intelliavant.mytimetracker.viewmodel.WorkListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var sm: StopwatchManager
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("STOPWATCH", "MainActivity.onCreate()")

        super.onCreate(savedInstanceState)

        // Request foereground service permission
        requestPermissions(
            arrayOf(android.Manifest.permission.FOREGROUND_SERVICE),
            PackageManager.PERMISSION_GRANTED
        )

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.work_list_toolbar))

        val navController = findNavController(R.id.nav_host_fragment)

//        appBarConfiguration = AppBarConfiguration(navController.graph)
        // Set workListFragment and stopwatchFragment as root so that Up button won't show on the stopwatchFragment.
        // https://stackoverflow.com/questions/56670215/hide-toolbar-back-arrow-with-navigationcomponent-and-bottomnavigationview
        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.workListFragment,
            R.id.stopwatchFragment
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)

        sm = StopwatchManager.getInstance(this)
        sm.create()
    }

    /**
     * Override this function to handle the action when the toolbar navigation button is clicked.
     */
    override fun onSupportNavigateUp(): Boolean {
        // Since we called `setupActionBarWithNavController()` and connected the toolbar with the navcontroller,
        // we need to override this function to provide Up button action.
        // https://medium.com/@ermarajhussain/how-to-work-with-navigation-controller-in-android-with-kotlin-part-2-152aa6dc3839
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        Log.d("STOPWATCH", "MainActivity.onResume()")
        super.onResume()
        // Recover stopwatch fragment if service is running and current fragment is the main fragment
        if (sm.isStopwatchServiceRunning()) {
            val navController = findNavController(R.id.nav_host_fragment)
            if (navController.currentDestination?.id == R.id.workListFragment) {
                navController.navigate(R.id.action_workListFragment_to_stopwatchFragment)
            }
        }

    }

    override fun onDestroy() {
        Log.d("STOPWATCH", "MainActivity.onDesctroy()")
        StopwatchManager.getInstance(this).destroy()
        super.onDestroy()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val navController = findNavController(R.id.nav_host_fragment)
        return when (item.itemId) {
            R.id.action_share -> onShare()
            R.id.settingsFragment -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_workListFragment_to_settingsFragment)
                return true
            }
            else -> NavigationUI.onNavDestinationSelected(item, navController)
        }
    }

    private fun onShare(): Boolean {

        val workListRecyclerView = findViewById<RecyclerView>(R.id.work_list_recycler_view)
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