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
import edu.uark.ahnelson.roomwithaview2024.NewWordActivity.NewWordActivity
import edu.uark.ahnelson.roomwithaview2024.R
import edu.uark.ahnelson.roomwithaview2024.Repository.Word

/**
WordListAdapter class
Implements a ListAdapter holding Words in WordViewHolders
Compares words with the WordsComparator
@param onItemClicked the callback function when an itemView is clicked
 */
class WordListAdapter(
    val onItemClicked:(id:Int)->Unit)
    : ListAdapter<Word, WordListAdapter.WordViewHolder>(WordsComparator()) {

    /**
     * onCreateViewHolder creates the viewHolder object
     * Implements WordViewHolder
     * @param parent the object that holds the ViewGroup
     * @param viewType the type of the view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        return WordViewHolder.create(parent)
    }

    /**
     * onBindViewHolder is called when a view object is bound to a view holder
     * @param holder the ViewHolder being created
     * @param position integer value for the position in the recyclerView
     */
    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        //Get the item in a position
        val current = getItem(position)
        //Set its onClickListener to the class callback parameter
        holder.itemView.setOnClickListener {
            current.id?.let { it1 -> onItemClicked(it1) }
            }
        //Bind the item to the holder
        holder.bind(current)
    }

    /**
     * WordViewHolder class implements a RecyclerView.ViewHolder object
     * Responsible for creating the layouts and binding objects to views
     * @param itemView the View object to be bound
     */
    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Reference to the textView object
        private val wordItemView: TextView = itemView.findViewById(R.id.textView)

        /**
         * bind binds a word object's data to views
         */
        fun bind(word: Word?) {
            if (word != null) {
                wordItemView.text = word.word
            }
        }

        /**
         * create the view object
         */
        companion object {
            fun create(parent: ViewGroup): WordViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return WordViewHolder(view)
            }
        }
    }

    /**
     * Comparators to determine whether to actually inflate new views
     */
    class WordsComparator : DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Word, newItem: Word): Boolean {
            return oldItem.word == newItem.word
        }
    }
}
