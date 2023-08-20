package com.example.myapplication.utils

import com.example.myapplication.Task

interface EditAndDeleteTask {
    fun task(task: Task, isEdit:Boolean)
}