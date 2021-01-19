package com.intelliavant.mytimetracker.data

import kotlinx.coroutines.Dispatchers
import java.lang.IllegalArgumentException
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkTypeRepository @Inject constructor(private val workTypeDao: WorkTypeDao) {

    fun getWorkTypes() = workTypeDao.getWorkTypes()
}


@Singleton
class WorkRepository @Inject constructor(private val workDao: WorkDao) {

    suspend fun createWork(workType: WorkType): Long {
        if (workType.id == null) throw IllegalArgumentException("workType does not exist")

        val current = Date()
        val work = Work(null, workType.name, current, 0, workType.id)

        return withContext(Dispatchers.IO) {
            workDao.insert(work)
        }
    }

    suspend fun updateDuration(workId: Long, duration: Long) {
        return withContext(Dispatchers.IO) {
            workDao.updateDuration(workId, duration)
        }
    }

    fun findById(id: Long): Flow<Work> {
        return workDao.findById(id)
    }

    fun getWorks() = workDao.getWorks()
}