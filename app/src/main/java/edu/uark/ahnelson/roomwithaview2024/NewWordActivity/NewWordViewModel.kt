package edu.uark.ahnelson.roomwithaview2024.NewWordActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import edu.uark.ahnelson.roomwithaview2024.Repository.Word
import edu.uark.ahnelson.roomwithaview2024.Repository.WordRepository
import kotlinx.coroutines.launch

class NewWordViewModel(private val repository: WordRepository) : ViewModel() {
    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    val _word = MutableLiveData<Word>().apply{value=null}
    val word: LiveData<Word>
        get() = _word

    fun start(wordId:Int){
        viewModelScope.launch {
            repository.allWords.collect{
                _word.value = it[wordId]
            }
        }
    }

    fun insert(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }

    fun update(word: Word) = viewModelScope.launch {
        repository.update(word)
    }
}

class NewWordViewModelFactory(private val repository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewWordViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewWordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
