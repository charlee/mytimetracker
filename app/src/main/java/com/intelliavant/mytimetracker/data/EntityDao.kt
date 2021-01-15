package com.intelliavant.mytimetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface WorkDao {

    @Insert
    fun insert(work: Work)

    @Query("SELECT * FROM work")
    fun getAll(): List<Work>

    @Query("SELECT * FROM work WHERE date=:date")
    fun findByDate(date: Date): List<Work>
}


@Dao
interface WorkTypeDao {

    @Insert
    fun insert(workType: WorkType)


    @Query("SELECT * FROM work_type")
    fun getAll(): List<WorkType>
}