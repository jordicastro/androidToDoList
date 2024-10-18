package edu.uark.ahnelson.roomwithaview2024.MainActivity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.uark.ahnelson.roomwithaview2024.NewWordActivity.NewTaskActivity
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.Task
import edu.uark.ahnelson.roomwithaview2024.TasksApplication

// main activity class contains

class MainActivity : AppCompatActivity() {

    //ViewModel object to communicate between Activity and repository
//    private val wordViewModel: WordViewModel by viewModels {
//        WordViewModelFactory((application as WordsApplication).repository)
//    }

    // ViewModel object to communicate between Activity and repository
    private val taskViewModel: TaskViewModel by viewModels {
        TaskViewModelFactory((application as TasksApplication).repository)
    }
    /**
    Callback function passed through to RecyclerViewItems to launch
    A new activity based on id
     @param id id of the item that is clicked
        called when an item is clicked -> intent to NewWordActivity view with the id
     */
    // **this method is passed into wordlistadapter as a callback. When an item is clicked, the adapter calls this method with the id of the item
//    fun launchNewWordActivity(id:Int){
//        val secondActivityIntent = Intent(this, NewWordActivity::class.java)
//        secondActivityIntent.putExtra("EXTRA_ID",id)
//        this.startActivity(secondActivityIntent)
//    }

    // TODO: refactor NewWordActivity folder to TaskActivity
    fun launchNewTaskActivity(id:Int){
        val secondActivityIntent = Intent(this, NewTaskActivity::class.java)
        secondActivityIntent.putExtra("EXTRA_ID",id)
        this.startActivity(secondActivityIntent)
    }

    /**
    onCreate callback, handle setting up the application
     */
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        //Get reference to recyclerView object
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
//        //Create adapter class, passing the launchNewWordActivity callback
//        val adapter = WordListAdapter(this::launchNewWordActivity)
//        //Set the adapter for the recyclerView to the adapter object
//        recyclerView.adapter = adapter
//        //Set the recyclerview layout to be a linearLayoutManager with activity context
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        //Start observing the words list (now map), and pass updates through
//        //to the adapter
//        wordViewModel.allWords.observe(this, Observer { words ->
//            // Update the cached copy of the words in the adapter.
//            words?.let { adapter.submitList(it.values.toList()) }
//        })
//        //Get reference to floating action button
//        val fab = findViewById<FloatingActionButton>(R.id.fab)
//        //Start the NewWordActivity when it is clicked
//        fab.setOnClickListener {
//            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
//            startActivity(intent)
//        }
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Get reference to recyclerView object
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        //Create adapter class, passing the launchNewTaskActivity callback
        val adapter = TaskListAdapter(this::launchNewTaskActivity, this::deleteTask, this::updateCheckBox)
        //Set the adapter for the recyclerView to the adapter object
        recyclerView.adapter = adapter
        //Set the recyclerview layout to be a linearLayoutManager with activity context
        recyclerView.layoutManager = LinearLayoutManager(this)
        //Start observing the words list (now map), and pass updates through
        //to the adapter
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            // Update the cached copy of the words in the adapter.
            tasks?.let { adapter.submitList(it.values.toList()) }
        })
        //Get reference to floating action button
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        //Start the NewWordActivity when it is clicked
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewTaskActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteTask(task: Task) {
        taskViewModel.delete(task)

    }

    private fun updateCheckBox(task: Task) {
        taskViewModel.update(task)
    }

}