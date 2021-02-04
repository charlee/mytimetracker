package com.intelliavant.mytimetracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        val workTypesPref = findPreference<Preference>("work_types")
        workTypesPref?.setOnPreferenceClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_settingsWorkTypeListFragment)
            true
        }
    }
}