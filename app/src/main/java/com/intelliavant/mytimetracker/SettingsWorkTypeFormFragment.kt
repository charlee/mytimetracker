package com.intelliavant.mytimetracker

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.intelliavant.mytimetracker.viewmodel.WorkTypeListViewModel
import com.thebluealliance.spectrum.SpectrumPalette
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsWorkTypeFormFragment : Fragment() {

    companion object {
        fun newInstance() = SettingsWorkTypeFormFragment()
    }

    var name: String = "";
    var color: Color = Color.valueOf(Color.WHITE)

    private val workTypeListViewModel: WorkTypeListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_work_type_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val workTypeId = arguments?.getLong("workTypeId") ?: 0L

        val nameTextInput = view.findViewById<EditText>(R.id.work_type_name_textfield_input)
        val colorPicker = view.findViewById<SpectrumPalette>(R.id.color_picker)
        val saveButton = view.findViewById<Button>(R.id.save_work_type)

        if (workTypeId > 0) {
            workTypeListViewModel.findById(workTypeId).observe(viewLifecycleOwner, { workType ->
                nameTextInput.setText(workType.name)
                colorPicker.setSelectedColor(workType.color.toArgb())
            })
        }

        nameTextInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun afterTextChanged(s: Editable?) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                name = s?.toString() ?: ""
            }
        })

        colorPicker.setOnColorSelectedListener { color = Color.valueOf(it) }

        saveButton.setOnClickListener {
            Log.v("STOPWATCH", "work type saved, name=$name, color=$color")
            findNavController().popBackStack()
        }
    }
}