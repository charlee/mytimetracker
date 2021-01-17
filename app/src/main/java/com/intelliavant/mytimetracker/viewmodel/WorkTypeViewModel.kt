package com.intelliavant.mytimetracker.viewmodel

import com.intelliavant.mytimetracker.data.WorkType

class WorkTypeViewModel(workType: WorkType) {
    private val _workType = workType
    val id
        get() = _workType.id
    val name
        get() = _workType.name
}