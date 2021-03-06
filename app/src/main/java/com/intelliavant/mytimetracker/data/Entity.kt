package com.intelliavant.mytimetracker.data

import android.graphics.Color
import androidx.room.*
import java.time.LocalDate


@Entity(
    tableName = "work",
    foreignKeys = [ForeignKey(entity = WorkType::class, parentColumns = ["id"], childColumns = ["worktype_id"])],
    indices = [Index("worktype_id")]
)
data class Work(
    @PrimaryKey(autoGenerate = true) val id: Long?,
    val name: String,

    val date: LocalDate,

    val duration: Long,
    @ColumnInfo(name = "worktype_id") val workTypeId: Long
)

@Entity(tableName = "work_type")
data class WorkType(
    @PrimaryKey(autoGenerate = true) val id: Long?,

    val name: String,

    val color: Color,
    val effective: Boolean
)


/**
 * Work entity with workType filled automatically.
 */
data class WorkWithWorkType(
    @Embedded val work: Work,
    @Relation(
        parentColumn = "worktype_id",
        entityColumn = "id"
    )
    val workType: WorkType

)