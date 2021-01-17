package com.intelliavant.mytimetracker.viewmodel

import com.intelliavant.mytimetracker.data.Work

class WorkViewModel(work: Work) {
    private val _work = work
    val work
        get() = _work
}
