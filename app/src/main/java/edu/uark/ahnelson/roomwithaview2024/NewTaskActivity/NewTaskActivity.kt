package edu.uark.ahnelson.roomwithaview2024.NewWordActivity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
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
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import edu.uark.ahnelson.roomwithaview2024.Notifications.Notification
import edu.uark.ahnelson.roomwithaview2024.Notifications.channelID
import edu.uark.ahnelson.roomwithaview2024.Notifications.notificationId
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.Task
import edu.uark.ahnelson.roomwithaview2024.TasksApplication
import java.util.Calendar
import java.util.Date

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
    private val newTaskViewModel: NewTaskViewModel by viewModels {
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
            task = Task(null,"", "", "", "", false)
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
                    showTime.text = convertToAMPM(it.taskTimeDue)

                    // check box on or off
                    checkboxTaskCompleted.isChecked = it.taskStatus
                }
            }
        }
        // share button -> share task to other apps
        val buttonShare = findViewById<Button>(R.id.button_share)
        buttonShare.setOnClickListener {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Task: ${editTextTask.text}\nDescription: ${editTextDescription.text}\nDue Date: ${showDate.text}\nDue Time: ${showTime.text}\nCompleted: ${checkboxTaskCompleted.isChecked}")
                type = "text/plain"
            }
            startActivity(Intent.createChooser(shareIntent, "Share Task"))
        }

        // back arrow button -> return to main activity
        val buttonBack = findViewById<Button>(R.id.button_back)
        buttonBack.setOnClickListener {
            finish()
        }

        // save button -> save task, insert or update task into database
        val buttonSave = findViewById<Button>(R.id.button_save)

        buttonSave.setOnClickListener {
            val replyIntent = Intent()
            if (TextUtils.isEmpty(editTextTask.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {

                val taskName = editTextTask.text.toString()
                val taskDescription = editTextDescription.text.toString()
                val taskDueDate = showDate.text.toString()
                val taskDueTime = convertToAMPM(showTime.text.toString()).toString()
                val taskStatus = checkboxTaskCompleted.isChecked

                if(newTaskViewModel.task.value?.taskId == null){
                    newTaskViewModel.insert(Task(null, taskName, taskDescription, taskDueDate, taskDueTime, taskStatus ))
                }else{
                    newTaskViewModel.task.value?.let { it1: Task ->
                        it1.taskName = taskName
                        it1.taskDescription = taskDescription
                        it1.taskDateDue = taskDueDate
                        it1.taskTimeDue = taskDueTime
                        it1.taskStatus = taskStatus
                        newTaskViewModel.update(it1)
                    }
                }
                //replyIntent.putExtra(EXTRA_REPLY, word)
                setResult(Activity.RESULT_OK)

                // create notification
                createNotification(taskName, taskDescription, taskDueDate, taskDueTime)


            }
            //End the activity
            finish()
        }

    }

    private fun createNotification(taskName: String, taskDescription: String, taskDueDate: String, taskDueTime: String) {
        // register app's notification channel:
        createNotificationChannel()
        scheduleNotification(taskName, taskDescription, taskDueDate, taskDueTime)

//        val CHANNEL_ID = "Task Notification Channel"
//        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
//            .setContentTitle("Task Reminder")
//            .setContentText("Task: $taskName\nDescription: $taskDescription\nDue Date: $taskDueDate\nDue Time: $taskDueTime")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }

    private fun scheduleNotification(taskName: String, taskDescription: String, taskDueDate: String, taskDueTime: String) {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Task Reminder"
        val message = "Task: $taskName\nDescription: $taskDescription\nDue Date: $taskDueDate\nDue Time: $taskDueTime"
        intent.putExtra("titleExtra", title)
        intent.putExtra("messageExtra", message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val time = getTime(taskDueDate, taskDueTime)
        alarmManager.set(AlarmManager.RTC_WAKEUP, time as Long, pendingIntent)
        // showAlert(time, title, message)
    }

    private fun showAlert(time: Long, title: String, message: String) {
        val date = Date(time)

        AlertDialog.Builder(this)
            .setTitle("Notification Scheduled")
            .setMessage("Notification scheduled for ${date.toString()}\n$title\n$message")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun getTime(taskDueDate: String, taskDueTime: String): Any {
        // taskDueTime is of the form: HH:MM AM/PM
        // taskDueDate is of the form: MM/DD/YYYY

        // convert taskDueTime to 24 hour format
        val twelveHourDueTime = timeTo24Hour(taskDueTime)
        val time = twelveHourDueTime.split(":")
        val hour = time[0].toInt()
        val minute = time[1].toInt()
        val date = taskDueDate.split("/")
        val month = date[0].toInt()
        val day = date[1].toInt()
        val year = date[2].toInt()

        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, hour, minute)
        return calendar.timeInMillis
    }

    private fun timeTo24Hour(taskDueTime: String): String {
        // taskDueTime is of the form: HH:MM AM/PM
        val time = taskDueTime.split(":")
        val hour = time[0].toInt()
        val minute = time[1].split(" ")[0].toInt()
        val amORpm = time[1].split(" ")[1]

        if (amORpm == "AM") {
            if (hour == 12) {
                // 12 AM -> 00:00
                return "00:00"
            } else {
                // 1 AM -> 01:00
                return "$hour:$minute"
            }
        } else {
            if (hour == 12) {
                // 12 PM -> 12:00
                return "$hour:$minute"
            } else {
                // 1 PM -> 13:00
                return "${hour + 12}:$minute"
            }

        }

    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun convertToAMPM(taskTimeDue: String): CharSequence? {
        // check to see if time was already converted (contains AM | PM)
        if (taskTimeDue.contains("AM") || taskTimeDue.contains("PM")) {
            return taskTimeDue
        }
        // taskTimeDue is in 24 hour format
        // convert to 12 hour format
        val time = taskTimeDue.split(":")
        val hour = time[0].toInt()
        val minute = time[1]
        val amORpm = if (hour < 12) "AM" else "PM"
        val newHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
        return "$newHour:$minute $amORpm"
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
                val correctMonth = selectedMonth + 1
                showDate.text = "$selectedMonth/${selectedDay}/$selectedYear"
            }, year, month, day)

            datePickerDialog.show()
        } else if (dialogType == "time") {
            // Time picker dialog
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                showTime.text = convertToAMPM("$selectedHour:$selectedMinute")
            }, hour, minute, false)

            timePickerDialog.show()
        }
    }
}