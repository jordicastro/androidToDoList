package edu.uark.ahnelson.roomwithaview2024.Repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
// this file takes in the DAO and uses it to access the database
// insert, update, TODO: delete

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class WordRepository(private val wordDao: WordDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allWords: Flow<Map<Int,Word>> = wordDao.getAlphabetizedWords()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @WorkerThread
    suspend fun insert(word: Word) {
        wordDao.insert(word)
    }

    @WorkerThread
    suspend fun update(word: Word){
        wordDao.update(word)
    }
}
