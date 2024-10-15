package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
// this file is the entity class for the word_table
// an entity class is a class that represents a table in the database
// TODO: change word -> task object: taskId, taskName, taskDateDue, taskStatus
@Entity(tableName = "word_table")
data class Word(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name="word") val word:String
)