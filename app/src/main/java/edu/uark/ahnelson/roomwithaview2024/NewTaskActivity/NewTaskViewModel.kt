package edu.uark.ahnelson.roomwithaview2024.NewWordActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.roomwithaview2024.Repository.Task
import edu.uark.ahnelson.roomwithaview2024.Repository.TaskRepository
import kotlinx.coroutines.launch

class NewTaskViewModel(private val repository: TaskRepository) : ViewModel() {
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    val _task = MutableLiveData<Task>().apply{value=null}
    val task: LiveData<Task>
        get() = _task

    fun start(taskId:Int){
        viewModelScope.launch {
            repository.allTasks.collect{
                _task.value = it[taskId]
            }
        }
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }
}

class NewTaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewTaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
