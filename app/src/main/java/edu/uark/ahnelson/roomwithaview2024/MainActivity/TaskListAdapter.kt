package edu.uark.ahnelson.roomwithaview2024.MainActivity

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.Task
import edu.uark.ahnelson.roomwithaview2024.TasksApplication

/**
WordListAdapter class
Implements a ListAdapter holding Words in WordViewHolders
Compares words with the WordsComparator
@param onItemClicked the callback function when an itemView is clicked
 */

// this file creates an adaptor for the recycler view. It is responsible for creating the view holder objects and binding the data to the views
// the onClick method is passed in from main activity.
// when onBindViewHolder detects a click, it calls onItemClicked, defined in main activity as 'launchNewWordActivity'

// CALLBACK FUNCTIONS
    // onItemClicked
    // onDeleteClicked
class TaskListAdapter(
    val onItemClicked:(taskId:Int)->Unit,
    private val onDeleteClicked:(task:Task)->Unit,
    private val onCheckBoxClicked:(task:Task)->Unit
    ) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {


    /**
     * onCreateViewHolder creates the viewHolder object
     * Implements WordViewHolder
     * @param parent the object that holds the ViewGroup
     * @param viewType the type of the view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent, onDeleteClicked, onCheckBoxClicked)
    }

    /**
     * onBindViewHolder is called when a view object is bound to a view holder
     * @param holder the ViewHolder being created
     * @param position integer value for the position in the recyclerView
     */

    // this method sets an onClick for each item in the recycler view. When an item is clicked, the onItemClicked callback is called with the id of the item
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        //Get the item in a position
        val current = getItem(position)
        //Set its onClickListener to the class callback parameter
        holder.itemView.setOnClickListener {
            current.taskId?.let { it1 -> onItemClicked(it1) }
        }
        //Bind the item to the holder
        holder.bind(current)
    }

    /**
     * WordViewHolder class implements a RecyclerView.ViewHolder object
     * Responsible for creating the layouts and binding objects to views
     * @param itemView the View object to be bound
     */
    class TaskViewHolder(
            itemView: View,
            private val onDeleteClicked: (task: Task) -> Unit,
            private val onCheckBoxClicked: (task: Task) -> Unit
        ) : RecyclerView.ViewHolder(itemView) {

        //Reference to the textTaskName, textTaskDue
        private val textTaskName: TextView = itemView.findViewById(R.id.textTaskName)
        private val textTaskDue: TextView = itemView.findViewById(R.id.textTaskDue)
        private val buttonDeleteTask: Button = itemView.findViewById(R.id.ButtonDeleteTask)
        private val checkboxTaskCompleted: CheckBox = itemView.findViewById(R.id.checkboxTaskCompleted)



        /**
         * bind binds a word object's data to views
         */
        fun bind(task: Task?) {
            // call the updateTaskAppearance everytime the task is binded
            updateTaskAppearance(task?.taskStatus ?: false)
            if (task != null) {
                textTaskName.text = task.taskName
                textTaskDue.text = getDateAndTime(task.taskDateDue, task.taskTimeDue)
                checkboxTaskCompleted.isChecked = task.taskStatus

                // delete button onClick -> callback onDeleteClicked
                buttonDeleteTask.setOnClickListener {
                    onDeleteClicked(task)
                }

                // checkbox onClick -> callback onCheckBoxClicked
                checkboxTaskCompleted.setOnClickListener {
                    // change the task with the new checkBox
                    task.taskStatus = checkboxTaskCompleted.isChecked
                    // change the appearance of the task
                    updateTaskAppearance(task.taskStatus)
                    // call the callback
                    onCheckBoxClicked(task)
                }
            }
        }

        private fun updateTaskAppearance(isCompleted: Boolean) {
            if (isCompleted) {
                textTaskName.paintFlags = textTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                textTaskName.setTextColor(itemView.context.getColor(R.color.gray))
            } else {
                textTaskName.paintFlags = textTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                textTaskName.setTextColor(itemView.context.getColor(R.color.black))
            }
        }

        private fun getDateAndTime(taskDateDue: String, taskTimeDue: String): CharSequence? {
            return "$taskDateDue $taskTimeDue"
        }

        /**
         * create the view object
         */
        companion object {
            fun create(parent: ViewGroup, onDeleteClicked: (task: Task) -> Unit, onCheckBoxClicked: (task: Task) -> Unit): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view, onDeleteClicked, onCheckBoxClicked)
            }
        }
    }

    /**
     * Comparators to determine whether to actually inflate new views
     */
    class TasksComparator : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskName == newItem.taskName
        }
    }
}
