package com.intelliavant.mytimetracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.*

@Dao
interface WorkDao {

    @Insert
    suspend fun insert(work: Work): Long

    @Query("SELECT * FROM work")
    fun getWorks(): Flow<List<Work>>

    @Query("SELECT * FROM work WHERE date=:date")
    fun findByDate(date: LocalDate): Flow<List<Work>>

    @Query("SELECT * FROM work WHERE id=:id")
    fun findById(id: Long): Flow<Work>

    @Query("UPDATE work SET duration=:duration WHERE id=:id")
    fun updateDuration(id: Long, duration: Long)
}


@Dao
interface WorkTypeDao {

    @Insert
    suspend fun insert(workType: WorkType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workTypes: List<WorkType>)

    @Query("SELECT * FROM work_type")
    fun getWorkTypes(): Flow<List<WorkType>>

    @Query("SELECT * FROM work_type WHERE id=:id")
    fun findById(id: Long): Flow<WorkType>
}