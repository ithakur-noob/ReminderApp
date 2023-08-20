package com.example.myapplication.fragments

import TaskViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Task
import com.example.myapplication.activities.AddTaskActivity
import com.example.myapplication.adapters.TaskAdapter
import com.example.myapplication.databinding.FragmentTaskBinding
import com.example.myapplication.utils.CommonFunctions
import com.example.myapplication.utils.EditAndDeleteTask
import com.example.myapplication.utils.TimePickerFragment

class CompletedTasksFragment :
    BaseFragment<FragmentTaskBinding>(FragmentTaskBinding::inflate),
    EditAndDeleteTask {
    private lateinit var adapter: TaskAdapter
    private val completedTasksArrayList = ArrayList<Task>()
    private lateinit var taskViewModel: TaskViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingBtn.visibility = View.GONE
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        adapter = TaskAdapter(requireActivity(), completedTasksArrayList, this)
        binding.rvTasks.adapter = adapter
        binding.rvTasks.layoutManager = LinearLayoutManager(requireActivity())
    }

    override fun task(task: Task, isEdit: Boolean) {
        if (isEdit) {
            startActivity(
                Intent(requireActivity(), AddTaskActivity::class.java)
                    .putExtra("isEdit", true).putExtra("task", task)
            )
        } else {
            taskViewModel.deleteTask(task)
        }
    }

    private fun observeTasks() {
        taskViewModel.getAllTasks().observe(requireActivity()) {
            // Update your RecyclerView adapter with the new list of tasks
            completedTasksArrayList.clear()
            for (task in it) {
                if (task.isCompleted)
                    completedTasksArrayList.add(task)
            }
            if (completedTasksArrayList.isEmpty()) {
                binding.rvTasks.visibility = View.GONE
                binding.llAddTask.visibility = View.VISIBLE
            } else {
                binding.rvTasks.visibility = View.VISIBLE
                binding.llAddTask.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        observeTasks()
    }
}
