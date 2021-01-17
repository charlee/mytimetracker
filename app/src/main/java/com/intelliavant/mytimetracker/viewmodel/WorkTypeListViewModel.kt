package com.intelliavant.mytimetracker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.intelliavant.mytimetracker.data.WorkType
import com.intelliavant.mytimetracker.data.WorkTypeRepository

class WorkTypeListViewModel @ViewModelInject internal constructor(workTypeRepository: WorkTypeRepository) :
    ViewModel() {
    val workTypes: LiveData<List<WorkType>> = workTypeRepository.getWorkTypes().asLiveData()
}
