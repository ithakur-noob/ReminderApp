package com.example.myapplication.activities

import TaskViewModel
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.Task
import com.example.myapplication.databinding.ActivityAddTaskBinding
import com.example.myapplication.utils.CommonFunctions
import com.example.myapplication.utils.DatePickerDialogFragment
import com.example.myapplication.utils.SharedPreferencesManager
import com.example.myapplication.utils.TaskScheduler
import com.example.myapplication.utils.TimePickerFragment
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/*This activity is used to add, edit and set task in work manager*/
class AddTaskActivity : AppCompatActivity(), TimePickerFragment.OnTimeSetListener,
    DatePickerDialogFragment.OnDateSetListener {
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private var isEdit = false
    private var task: Task? = null
    private lateinit var taskScheduler: TaskScheduler
    private var timestamp = 0L
    private var datestamp = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        taskScheduler = TaskScheduler(this)
        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
                intent.getSerializableExtra("task", Task::class.java)?.let { task = it }
            else
                task = intent.getSerializableExtra("task") as Task
            task?.let {
                binding.etTitle.setText(it.title)
                binding.etDescription.setText(it.description)
                val (datestamp, timestamp) = splitDateAndTime(it.dueDate)
                this.timestamp = timestamp
                this.datestamp = datestamp
                binding.etTime.setText(CommonFunctions.convertMillisToTimeString(timestamp))
                binding.etDate.setText(CommonFunctions.convertMillisToDateString(datestamp))

            }
        }

        binding.btnAdd.setOnClickListener {
            /*ask permission of android version 13 or higher */
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        android.Manifest.permission.POST_NOTIFICATIONS,
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    /*To inform the user about autoStart permission for background task in workManager */
                    if (!SharedPreferencesManager(this).getBoolean("isShown", false)) {
                        showPermissionAlertDialog()
                        SharedPreferencesManager(this).saveBoolean("isShown", true)
                    } else
                        saveTask()
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            } else
                if (!SharedPreferencesManager(this).getBoolean("isShown", false)) {
                    showPermissionAlertDialog()
                    SharedPreferencesManager(this).saveBoolean("isShown", true)
                } else
                    saveTask()
        }
        binding.etTime.setOnClickListener {
            /*To select time of reminder*/
            if (binding.etDate.text.toString().trim().isEmpty())
                Toast.makeText(this, "Please select date first", Toast.LENGTH_LONG).show()
            else
                showTimePickerDialog()
        }
        binding.etDate.setOnClickListener {
            /*To select time of reminder*/
            showDatePickerDialog()
        }
        binding.ivBackArrow.setOnClickListener { finish() }
    }

    private fun saveTask() {
        if (binding.etTitle.text.toString().trim().isEmpty())
            Toast.makeText(this, "Enter Title", Toast.LENGTH_LONG).show()
        else if (binding.etDescription.text.toString().trim().isEmpty())
            Toast.makeText(this, "Enter Description", Toast.LENGTH_LONG).show()
        else if (binding.etDate.text.toString().trim().isEmpty())
            Toast.makeText(this, "Select Date", Toast.LENGTH_LONG).show()
        else if (binding.etTime.text.toString().trim().isEmpty())
            Toast.makeText(this, "Select Time", Toast.LENGTH_LONG).show()
        else {
            val task = Task(
                if (isEdit) task?.id ?: 0 else System.currentTimeMillis(),
                binding.etTitle.text.toString(),
                binding.etDescription.text.toString(),
                createDateWithTime(
                    datestamp,
                    timestamp
                ),
                false
            )

            if (isEdit) {
                if (isDateNotLessThanCurrent(datestamp)) {
                    if (isDateToday(datestamp)) {
                        if (isTimeAheadBy6Minutes(timestamp)) {
                            taskScheduler.scheduleTaskReminder(task.id, task.dueDate)
                            taskViewModel.updateTask(task)
                            Toast.makeText(this, "Reminder edited", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Selected time should be 6 min ahead of current time make sure am/pm is selected right",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        taskScheduler.scheduleTaskReminder(task.id, task.dueDate)
                        taskViewModel.updateTask(task)
                        Toast.makeText(this, "Reminder edited", Toast.LENGTH_LONG).show()
                        finish()
                    }
                } else
                    Toast.makeText(
                        this,
                        "Selected date should not be less then current date",
                        Toast.LENGTH_LONG
                    ).show()
            } else {
                taskScheduler.scheduleTaskReminder(task.id, task.dueDate)
                taskViewModel.insertTask(task)
                Toast.makeText(this, "Reminder added", Toast.LENGTH_LONG).show()
                finish()
            }

        }
    }


   private fun isDateNotLessThanCurrent(dateInMillis: Long): Boolean {
        val currentCalendar = Calendar.getInstance()
        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Compare only the year, month, and day
        return selectedCalendar.get(Calendar.YEAR) >= currentCalendar.get(Calendar.YEAR) &&
                selectedCalendar.get(Calendar.MONTH) >= currentCalendar.get(Calendar.MONTH) &&
                selectedCalendar.get(Calendar.DAY_OF_MONTH) >= currentCalendar.get(Calendar.DAY_OF_MONTH)
    }



    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                if (!SharedPreferencesManager(this).getBoolean("isShown", false)) {
                    showPermissionAlertDialog()
                    SharedPreferencesManager(this).saveBoolean("isShown", true)
                } else
                    saveTask()
            } else {
                Snackbar.make(
                    binding.root,
                    "Please grant Notification permission from App Settings",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

    private fun showPermissionAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("To ensure proper background reminder notification execution, please enable autoStart and remove battery saver restrictions on app")
            .setPositiveButton("Grant Permissions") { _, _ ->
                openBackgroundExecutionSettings()
            }
            .setNegativeButton("Cancel", null)
            .create()
        alertDialog.show()
    }

    private fun openBackgroundExecutionSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }


    private fun showTimePickerDialog() {
        val timePickerFragment = TimePickerFragment(this)
        timePickerFragment.show(supportFragmentManager, "timePicker")
    }

    private fun showDatePickerDialog() {
        val datePickerFragment = DatePickerDialogFragment(this)
        datePickerFragment.show(supportFragmentManager, "datePicker")

    }

    override fun onTimeSet(timestamp: Long) {
        if (isDateToday(datestamp)) {
            if (isTimeAheadBy6Minutes(timestamp)) {
                this.timestamp = timestamp
                binding.etTime.setText("${CommonFunctions.convertMillisToTimeString(timestamp)}")
            } else {
                Toast.makeText(
                    this,
                    "Selected time should be 6 mins ahead of current time make sure am/pm is selected right",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            this.timestamp = timestamp
            binding.etTime.setText("${CommonFunctions.convertMillisToTimeString(timestamp)}")
        }
    }


    private fun isDateToday(selectedDateInMillis: Long): Boolean {
        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateInMillis
        }

        val todayCalendar = Calendar.getInstance()

        return (selectedCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
                && selectedCalendar.get(Calendar.MONTH) == todayCalendar.get(Calendar.MONTH)
                && selectedCalendar.get(Calendar.DAY_OF_MONTH) == todayCalendar.get(Calendar.DAY_OF_MONTH))
    }


    fun isDateLessThanCurrent(dateInMillis: Long): Boolean {
        val currentCalendar = Calendar.getInstance()
        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return selectedCalendar < currentCalendar
    }


    private fun isTimeAheadBy6Minutes(timeInMillis: Long): Boolean {
        val currentTimeMillis = System.currentTimeMillis()
        val timeDifference = timeInMillis - currentTimeMillis
        val sixMinutesInMillis = 6 * 60 * 1000 // 6 minutes in milliseconds

        return timeDifference >= sixMinutesInMillis
    }

    /*combine date and time millis*/
    private fun createDateWithTime(dateInMillis: Long, timeInMillis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

        val timeCalendar = Calendar.getInstance()
        timeCalendar.timeInMillis = timeInMillis

        calendar[Calendar.HOUR_OF_DAY] = timeCalendar[Calendar.HOUR_OF_DAY]
        calendar[Calendar.MINUTE] = timeCalendar[Calendar.MINUTE]
        calendar[Calendar.SECOND] = timeCalendar[Calendar.SECOND]

        return calendar.timeInMillis
    }

    override fun onDateSet(timestamp: Long) {
        datestamp = timestamp
        binding.etDate.setText("${CommonFunctions.convertMillisToDateString(timestamp)}")
        binding.etTime.setText("")
    }

    /*Get date and time millis*/
    private fun splitDateAndTime(dateInMillis: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateInMillis

        val dateCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendar.get(Calendar.YEAR))
            set(Calendar.MONTH, calendar.get(Calendar.MONTH))
            set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
        }
        val timeCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, calendar.get(Calendar.MINUTE))
        }
        return Pair(dateCalendar.timeInMillis, timeCalendar.timeInMillis)
    }

}