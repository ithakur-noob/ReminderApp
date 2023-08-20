package com.example.myapplication.utils

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerDialogFragment(private val listener: OnDateSetListener) : DialogFragment() {
    interface OnDateSetListener {
        fun onDateSet(timestamp: Long)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the selected date
                // Do something with the selectedDate
                calendar.set(Calendar.YEAR,selectedYear)
                calendar.set(Calendar.MONTH,selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH,selectedDay)
                listener.onDateSet(calendar.timeInMillis)
            },
            year, month, day
        )
        datePicker.datePicker.minDate = calendar.timeInMillis
        return datePicker
    }


}
