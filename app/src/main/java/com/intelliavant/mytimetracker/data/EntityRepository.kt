package com.intelliavant.mytimetracker.data

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkTypeRepository @Inject constructor(private val workTypeDao: WorkTypeDao) {
    fun getWorkTypes() = workTypeDao.getWorkTypes()
}