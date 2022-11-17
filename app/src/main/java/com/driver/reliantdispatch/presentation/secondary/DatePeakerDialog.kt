package com.driver.reliantdispatch.presentation.secondary

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

const val ARG_DAY = "argday"
const val ARG_MONTH = "argmon"
const val ARG_YEAR = "argyear"

class DatePeakerDialog: DialogFragment(){
    var listener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var day = 0
        var month = 0
        var year = 0
        arguments?.let {
            day = it.getInt(ARG_DAY,0)
            month = it.getInt(ARG_MONTH,0)
            year = it.getInt(ARG_YEAR,0)
        }
        return DatePickerDialog(activity, listener, year, month, day)
    }

    companion object {

        fun date2Args(date: Date): Bundle {
            val c = Calendar.getInstance()
            c.time = date
            val args = Bundle()
            args.putInt(ARG_YEAR,c.get(Calendar.YEAR))
            args.putInt(ARG_MONTH,c.get(Calendar.MONTH))
            args.putInt(ARG_DAY,c.get(Calendar.DAY_OF_MONTH))
            return args
        }

        fun args2Date(year: Int, month: Int, day: Int): Date {
            val c = Calendar.getInstance()
            c.time = Date()
            c.set(Calendar.YEAR, year)
            c.set(Calendar.MONTH, month)
            c.set(Calendar.DAY_OF_MONTH, day)
            return c.time
        }
    }

}