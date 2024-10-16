package edu.uark.ahnelson.roomwithaview2024

import android.app.Application
import edu.uark.ahnelson.roomwithaview2024.Repository.TaskRepository
import edu.uark.ahnelson.roomwithaview2024.Repository.TaskRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// this file is the application class
// im not sure what this does

class TasksApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { TaskRoomDatabase.getDatabase(this,applicationScope) }
    val repository by lazy { TaskRepository(database.taskDao()) }
}
