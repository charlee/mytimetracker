package com.intelliavant.mytimetracker.data

import android.graphics.Color
import androidx.room.*
import java.util.*


@Entity(
    foreignKeys = [ForeignKey(entity = WorkType::class, parentColumns = ["id"], childColumns = ["worktype_id"])],
    indices = [Index("worktype_id")]
)
data class Work(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    val name: String,

    val date: Date,

    val duration: Int,
    @ColumnInfo(name = "worktype_id") val workTypeId: Int
)

@Entity(tableName = "work_type")
data class WorkType(
    @PrimaryKey(autoGenerate = true) val id: Int?,

    val name: String,

    val color: Color,
    val effective: Boolean
)


//data class WorkWithWorkType(
//    @Embedded val workType: WorkType,
//    @Relation(
//        parentColumn = "id",
//        entityColumn = "id"
//    )
//    val works: List<Work>
//)