package edu.uark.ahnelson.roomwithaview2024.NewWordActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.Word
import edu.uark.ahnelson.roomwithaview2024.WordsApplication
import java.util.Calendar

// this file handles creation, updating, and deleting of words

class NewWordActivity : AppCompatActivity() {

    // initialize variables
    private lateinit var showDate: TextView
    private lateinit var buttonDate: Button

    private lateinit var editTextTask: EditText
    private lateinit var word: Word
    val newWordViewModel: NewWordViewModel by viewModels {
        NewWordViewModelFactory((application as WordsApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_word)

        showDate = findViewById(R.id.showDate);
        buttonDate = findViewById(R.id.buttonDate);

        buttonDate.setOnClickListener { v ->
            openDialog()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        editTextTask = findViewById(R.id.editTextTask)

        //Logic block to determine whether we are updating an exiting word
        //Or creating a new word
        //Get intent that created the activity id value, if exists
        val id = intent.getIntExtra("EXTRA_ID",-1)
        //If it doesn't exist, create a new Word object
        if(id == -1){
            word = Word(null,"")
        }else{
            //Otherwise, start the viewModel with the id
            //And begin observing the word to set the text in the
            //text view
            newWordViewModel.start(id)
            newWordViewModel.word.observe(this){
                if(it != null){
                    editTextTask.setText(it.word)
                }
            }
        }

        //Get reference to the button
        val buttonSave = findViewById<Button>(R.id.button_save)
        //Set the click listener functionality
        //If text is empty, return with nothing
        buttonSave.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editTextTask.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                //If text isn't empty, determine whether to update
                //or insert
                val word = editTextTask.text.toString()
                if(newWordViewModel.word.value?.id == null){
                    newWordViewModel.insert(Word(null,word))
                    // newTaskViewModel.insert(Task(null, task))
                }else{
                    newWordViewModel.word.value?.let { it1 -> newWordViewModel.update(it1) }
                    // newTaskViewModel.task
                }
                //replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK)
            }
            //End the activity
            finish()
        }

    }

    @SuppressLint("SetTextI18n")
    private fun openDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            showDate.text = "$selectedDay/${selectedMonth + 1}/$selectedYear"
        }, year, month, day)

        datePickerDialog.show()
    }
}