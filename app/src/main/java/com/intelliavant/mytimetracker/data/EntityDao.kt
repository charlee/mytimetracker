package com.intelliavant.mytimetracker.data

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.*

@Dao
interface WorkDao {

    @Insert
    suspend fun insert(work: Work): Long

    @Transaction
    @Query("SELECT * FROM work")
    fun getWorks(): Flow<List<WorkWithWorkType>>

    @Transaction
    @Query("SELECT * FROM work WHERE date=:date")
    fun findByDate(date: LocalDate): Flow<List<WorkWithWorkType>>

    @Transaction
    @Query("SELECT * FROM work WHERE id=:id")
    fun findById(id: Long): WorkWithWorkType

    // Guard the update with duration<:duration to prevent time reset
    @Query("UPDATE work SET duration=:duration WHERE id=:id AND duration<:duration")
    fun updateDuration(id: Long, duration: Long)
}


@Dao
interface WorkTypeDao {

    @Insert
    suspend fun insert(workType: WorkType): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workTypes: List<WorkType>)

    @Query("SELECT * FROM work_type")
    fun getWorkTypes(): Flow<List<WorkType>>

    @Query("SELECT * FROM work_type WHERE id=:id")
    fun findById(id: Long): Flow<WorkType>

    @Query("UPDATE work_type SET name=:name, color=:color, effective=:effective WHERE id=:id")
    fun updateWorkType(id: Long, name: String, color: Color, effective: Boolean)
}