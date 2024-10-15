package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.MapInfo
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @MapInfo(keyColumn = "taskId")
    @Query("SELECT * FROM task_table ORDER BY taskName ASC")
    fun getAlphabetizedWords(): Flow<Map<Int,Task>>

    @Update
    suspend fun update(task: Task)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Query("DELETE FROM task_table")
    suspend fun deleteAll()
}