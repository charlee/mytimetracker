package com.intelliavant.mytimetracker.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.intelliavant.mytimetracker.data.Work
import com.intelliavant.mytimetracker.data.WorkRepository
import com.intelliavant.mytimetracker.data.WorkType
import kotlinx.coroutines.launch

class WorkListViewModel @ViewModelInject internal constructor(private val workRepository: WorkRepository) :
    ViewModel() {

    val works: LiveData<List<Work>> = workRepository.getWorks().asLiveData()

    fun createWork(workType: WorkType) {
        viewModelScope.launch {
            workRepository.createWork(workType)
        }
    }
}