package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.Task
import com.example.myapplication.databinding.ItemTaskBinding
import com.example.myapplication.utils.CommonFunctions
import com.example.myapplication.utils.EditAndDeleteTask


class TaskAdapter(
    private val context: Context,
    private val tasks: ArrayList<Task>,
    private val onClick: EditAndDeleteTask
) :
    RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {

        return TaskViewHolder(
            ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.tvTitle.text = task.title
            binding.tvDescription.text = task.description
            binding.tvDateTime.text =
                "Remind at: " + CommonFunctions.convertMillisToDateTimeString(task.dueDate) // Adjust as needed

            // Edit button click listener
            binding.tvEdit.setOnClickListener {
                onClick.task(task, true)
            }

            // Delete button click listener
            binding.tvDelete.setOnClickListener {
                onClick.task(task, false)
            }
        }
    }
}
