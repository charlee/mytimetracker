package com.intelliavant.mytimetracker.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface WorkDao {

    @Insert
    suspend fun insert(work: Work)

    @Query("SELECT * FROM work")
    fun getWorks(): Flow<List<Work>>

    @Query("SELECT * FROM work WHERE date=:date")
    fun findByDate(date: Date): List<Work>
}


@Dao
interface WorkTypeDao {

    @Insert
    suspend fun insert(workType: WorkType)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(workTypes: List<WorkType>)

    @Query("SELECT * FROM work_type")
    fun getWorkTypes(): Flow<List<WorkType>>

}