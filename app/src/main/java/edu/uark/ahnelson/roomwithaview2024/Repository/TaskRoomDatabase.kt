package edu.uark.ahnelson.roomwithaview2024.Repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// this file is the database class
// it contains the general methods to initialize and get the database

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(Task::class), version = 1, exportSchema = false)
public abstract class TaskRoomDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: TaskRoomDatabase? = null

        fun getDatabase(context: Context, scope:CoroutineScope): TaskRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskRoomDatabase::class.java,
                    "task_database"
                ).addCallback(TaskDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
    private class TaskDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.taskDao())
                }
            }
        }

        suspend fun populateDatabase(taskDao: TaskDao) {
            // Delete all content here.
            taskDao.deleteAll()

            // add sample tasks
            // tasks are of type
            // id, taskName, taskDescription, taskDueDate, taskStatus
            var task = Task(null, "task1", "description1", "10/01/24", true)
            taskDao.insert(task)
            task = Task(null, "task2", "description2", "10/02/24", false)
            taskDao.insert(task)

            // TODO: Add your own words!
        }
    }
}



