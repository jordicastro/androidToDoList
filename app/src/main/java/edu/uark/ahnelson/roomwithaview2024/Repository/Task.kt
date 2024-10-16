package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
// the task class is the entity class for the task_table
// the task class contains the following attributes
// taskId, taskName, taskDescription, taskDueDate, taskStatus
@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int?,
    @ColumnInfo(name="taskName") val taskName:String,
    @ColumnInfo(name="taskDescription") val taskDescription:String,
    @ColumnInfo(name="taskDateDue") val taskDateDue:String,
    @ColumnInfo(name="taskTimeDue") val taskTimeDue:String,
    @ColumnInfo(name="taskStatus") val taskStatus:Boolean
)