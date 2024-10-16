package edu.uark.ahnelson.roomwithaview2024.NewWordActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.Task
import edu.uark.ahnelson.roomwithaview2024.TasksApplication
import java.util.Calendar

// this file handles creation, updating, and deleting of words

class NewTaskActivity : AppCompatActivity() {

    // initialize variables
    private lateinit var showDate: TextView
    private lateinit var buttonDate: Button
    private lateinit var showTime: TextView
    private lateinit var buttonTime: Button


    private lateinit var editTextTask: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var checkboxTaskCompleted: CheckBox
    private lateinit var task: Task
    val newTaskViewModel: NewTaskViewModel by viewModels {
        NewTaskViewModelFactory((application as TasksApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_new_word)

        // date picker variables
        showDate = findViewById(R.id.showDate)
        buttonDate = findViewById(R.id.buttonDate)

        // time picker variables
        showTime = findViewById(R.id.showTime)
        buttonTime = findViewById(R.id.buttonTime)

        // task, description, completed
        editTextTask = findViewById(R.id.editTextTask)
        editTextDescription = findViewById(R.id.editTextDescription)
        checkboxTaskCompleted = findViewById(R.id.checkboxTaskCompleted)


        buttonDate.setOnClickListener { v ->
            openDialog("date")
        }

        buttonTime.setOnClickListener { v ->
            openDialog("time")
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Logic block to determine whether we are updating an exiting word
        //Or creating a new word
        //Get intent that created the activity id value, if exists
        val id = intent.getIntExtra("EXTRA_ID",-1)
        //If it doesn't exist, create a new Word object
        if(id == -1){
            task = Task(null,"", "", "", false)
        }else{
            //Otherwise, start the viewModel with the id
            //And begin observing the word to set the text in the
            //text view
            newTaskViewModel.start(id)
            newTaskViewModel.task.observe(this){
                if(it != null){
                    editTextTask.setText(it.taskName)
                    editTextDescription.setText(it.taskDescription)
                    showDate.text = it.taskDateDue

                    // check box on or off
                    checkboxTaskCompleted.isChecked = !it.taskStatus
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
                val taskName = editTextTask.text.toString()
                val taskDescription = editTextDescription.text.toString()
                val taskDueDate = showDate.text.toString()
                val taskDueTime = showTime.text.toString()
                val taskStatus = checkboxTaskCompleted.isChecked


                if(newTaskViewModel.task.value?.taskId == null){
                    newTaskViewModel.insert(Task(null, taskName, taskDescription, taskDueDate, taskStatus ))
                    // newTaskViewModel.insert(Task(null, task))
                }else{
                    newTaskViewModel.task.value?.let { it1 -> newTaskViewModel.update(it1) }
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
    private fun openDialog(dialogType: String) {
        if (dialogType == "date") {
            // Date picker dialog
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                showDate.text = "$selectedMonth/${selectedDay + 1}/$selectedYear"
            }, year, month, day)

            datePickerDialog.show()
        } else if (dialogType == "time") {
            // Time picker dialog
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                showTime.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            }, hour, minute, false)

            timePickerDialog.show()
        }
    }
}