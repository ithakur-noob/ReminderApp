package com.example.myapplication.fragments

import TaskViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.Task
import com.example.myapplication.activities.AddTaskActivity
import com.example.myapplication.adapters.TaskAdapter
import com.example.myapplication.databinding.FragmentTaskBinding
import com.example.myapplication.utils.EditAndDeleteTask
import com.example.myapplication.utils.TaskScheduler

class PendingTasksFragment : BaseFragment<FragmentTaskBinding>(FragmentTaskBinding::inflate),
    EditAndDeleteTask {
    private val pendingTasksArrayList = ArrayList<Task>()
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var adapter: TaskAdapter
    private lateinit var taskScheduler: TaskScheduler

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskScheduler = TaskScheduler(requireActivity())
        adapter = TaskAdapter(requireActivity(), pendingTasksArrayList, this)
        binding.rvTasks.adapter = adapter
        binding.rvTasks.layoutManager = LinearLayoutManager(requireActivity())
        binding.floatingBtn.setOnClickListener {
            startActivity(Intent(requireActivity(), AddTaskActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        observeTasks()
    }

    private fun observeTasks() {
        taskViewModel.getAllTasks().observe(requireActivity()) {
            // Update your RecyclerView adapter with the new list of tasks
            Log.i("TASKLIST", "${it.toString()}")
            pendingTasksArrayList.clear()
            for (task in it) {
                if (!task.isCompleted)
                    pendingTasksArrayList.add(task)
            }
            if (pendingTasksArrayList.isEmpty()) {
                binding.rvTasks.visibility = View.GONE
                binding.llAddTask.visibility = View.VISIBLE
            } else {
                binding.rvTasks.visibility = View.VISIBLE
                binding.llAddTask.visibility = View.GONE
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun task(task: Task, isEdit: Boolean) {
        if (isEdit) {
            startActivity(
                Intent(requireActivity(), AddTaskActivity::class.java)
                    .putExtra("isEdit", true).putExtra("task", task)
            )
        } else {
            taskScheduler.deleteTaskByTag(task.id)
            taskViewModel.deleteTask(task)
        }
    }
}