package com.intelliavant.mytimetracker.data

import android.graphics.Color
import kotlinx.coroutines.Dispatchers
import java.lang.IllegalArgumentException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkTypeRepository @Inject constructor(private val workTypeDao: WorkTypeDao) {

    fun getWorkTypes() = workTypeDao.getWorkTypes()

    fun findById(id: Long): Flow<WorkType> {
        return workTypeDao.findById(id)
    }

    suspend fun createWorkType(name: String, color: Color, effective: Boolean): Long {
        val workType = WorkType(null, name, color, effective)

        return withContext(Dispatchers.IO) {
            workTypeDao.insert(workType)
        }
    }
}


@Singleton
class WorkRepository @Inject constructor(private val workDao: WorkDao) {

    suspend fun createWork(name: String, workType: WorkType): Long {
        if (workType.id == null) throw IllegalArgumentException("workType does not exist")

        val current = LocalDate.now()
        val work = Work(null, name, current, 0, workType.id)

        return withContext(Dispatchers.IO) {
            workDao.insert(work)
        }
    }

    suspend fun updateDuration(workId: Long, duration: Long) {
        return withContext(Dispatchers.IO) {
            workDao.updateDuration(workId, duration)
        }
    }

    fun findById(id: Long): Flow<WorkWithWorkType> {
        return workDao.findById(id)
    }

    fun findByDate(date: LocalDate): Flow<List<WorkWithWorkType>> {
        return workDao.findByDate(date)
    }

    fun getWorks() = workDao.getWorks()
}