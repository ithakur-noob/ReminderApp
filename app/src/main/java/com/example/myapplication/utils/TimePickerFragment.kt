package com.example.myapplication.utils

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment(private val listener: OnTimeSetListener) : DialogFragment() {

    interface OnTimeSetListener {
        fun onTimeSet(timestamp: Long)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            listener.onTimeSet(calendar.timeInMillis)
        }, hour, minute, false)
    }


}
