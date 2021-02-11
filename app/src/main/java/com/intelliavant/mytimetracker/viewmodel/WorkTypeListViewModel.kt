package com.intelliavant.mytimetracker.viewmodel

import android.graphics.Color
import android.widget.EdgeEffect
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intelliavant.mytimetracker.data.Work
import com.intelliavant.mytimetracker.data.WorkType
import com.intelliavant.mytimetracker.data.WorkTypeRepository

class WorkTypeListViewModel @ViewModelInject internal constructor(private val workTypeRepository: WorkTypeRepository) :
    ViewModel() {
    val workTypes: LiveData<List<WorkType>> = workTypeRepository.getWorkTypes().asLiveData()

    fun findById(id: Long): LiveData<WorkType> = workTypeRepository.findById(id).asLiveData()


    suspend fun createWorkType(name: String, color: Color, effective: Boolean): Long {
        return workTypeRepository.createWorkType(name, color, effective)
    }

    suspend fun updateWorkType(id: Long, name: String, color: Color, effective: Boolean) {
        workTypeRepository.updateWorkType(id, name, color, effective)
    }
}
