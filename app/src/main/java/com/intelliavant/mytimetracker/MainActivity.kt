package com.intelliavant.mytimetracker

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.intelliavant.mytimetracker.data.AppDatabase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var db: AppDatabase ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        // show the work type bottom sheet when FAB is clicked
        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val fragment = WorkTypeListFragment()
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_work_types -> openWorkTypes()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openWorkTypes(): Boolean {
        val intent = Intent(this, WorkTypeActivity::class.java)
        startActivity(intent)
        return true
    }
}