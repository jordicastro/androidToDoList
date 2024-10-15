package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
// this file takes in the DAO and uses it to access the database
// insert, update, TODO: delete

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class TaskRepository(private val taskDao: TaskDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allTasks: Flow<Map<Int,Task>> = taskDao.getAlphabetizedWords()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    @WorkerThread
    suspend fun update(task: Task){
        taskDao.update(task)
    }

    @WorkerThread
    suspend fun deleteAll(){
        taskDao.deleteAll()
    }
}
