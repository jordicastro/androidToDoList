package edu.uark.ahnelson.roomwithaview2024.MainActivity

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.Task

/**
WordListAdapter class
Implements a ListAdapter holding Words in WordViewHolders
Compares words with the WordsComparator
@param onItemClicked the callback function when an itemView is clicked
 */

// this file creates an adaptor for the recycler view. It is responsible for creating the view holder objects and binding the data to the views
// the onClick method is passed in from main activity.
// when onBindViewHolder detects a click, it calls onItemClicked, defined in main activity as 'launchNewWordActivity'

class TaskListAdapter(
    val onItemClicked:(taskId:Int)->Unit)
    : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TasksComparator()) {

    /**
     * onCreateViewHolder creates the viewHolder object
     * Implements WordViewHolder
     * @param parent the object that holds the ViewGroup
     * @param viewType the type of the view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder.create(parent)
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
    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Reference to the textView object
        private val taskItemView: TextView = itemView.findViewById(R.id.textView)

        /**
         * bind binds a word object's data to views
         */
        fun bind(task: Task?) {
            if (task != null) {
                taskItemView.text = task.taskName
            }
        }

        /**
         * create the view object
         */
        companion object {
            fun create(parent: ViewGroup): TaskViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return TaskViewHolder(view)
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
