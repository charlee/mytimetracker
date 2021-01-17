package com.intelliavant.mytimetracker.data

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkTypeRepository @Inject constructor(private val workTypeDao: WorkTypeDao) {

    fun getWorkTypes() = workTypeDao.getWorkTypes()
}


@Singleton
class WorkRepository @Inject constructor(private val workDao: WorkDao) {

    suspend fun createWork(workType: WorkType) {
        if (workType.id == null) return

        val current = Date()
        val work = Work(null, workType.name, current, 0, workType.id)

        workDao.insert(work)
    }

    fun getWorks() = workDao.getWorks()
}