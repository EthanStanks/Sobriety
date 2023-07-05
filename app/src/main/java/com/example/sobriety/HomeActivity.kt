package com.example.sobriety

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.card.MaterialCardView
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var tvWelcomeMessage: TextView
    private lateinit var tvSobrietyCount: TextView
    private lateinit var tvStartDay: TextView
    private lateinit var tvStartMonth: TextView
    private lateinit var mcvCalendar: MaterialCardView

    private lateinit var sharedPreferences: SharedPreferences // needed for saving and reading

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        val calendar = Calendar.getInstance()

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        // Read the saved date from SharedPreferences
        val savedYear = sharedPreferences.getInt("year", -1)
        val savedMonth = sharedPreferences.getInt("month", -1)
        val savedDay = sharedPreferences.getInt("day", -1)

        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        tvSobrietyCount = findViewById(R.id.tvDayCount)
        tvStartDay = findViewById(R.id.tvStartDay)
        tvStartMonth = findViewById(R.id.tvStartMonth)
        mcvCalendar = findViewById(R.id.mcvCalendar)

        tvWelcomeMessage.text = getWelcomeMessage(calendar)
        tvSobrietyCount.text = getDaysSober(savedYear, savedMonth, savedDay)

        getStartDate(calendar, savedYear, savedMonth, savedDay)
        mcvCalendar.setOnClickListener {
            clickedCalendar()
        }

    }

    private fun getWelcomeMessage(calendar: Calendar) : String
    {
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            currentHour < 12 -> "Good Morning,"
            currentHour < 18 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
    }

    private fun getDaysSober(year: Int, month: Int, day: Int): String
    {
        val startDate = Calendar.getInstance()
        startDate.set(year, month, day)
        val currentDate = Calendar.getInstance()
        val startTimeInMillis = startDate.timeInMillis
        val currentTimeInMillis = currentDate.timeInMillis
        val millisecondsPerDay = 24 * 60 * 60 * 1000
        val count = ((currentTimeInMillis - startTimeInMillis) / millisecondsPerDay).toInt()

        var display: String
        if(day == -1)  display = "0 Days"
        else if(count != 1) display = "$count Days"
        else display = "$count Day"

        return display
    }

    private fun getMonth(month: Int) : String
    {
        return when (month) {
            0 -> "January"
            1 -> "February"
            2 -> "March"
            3 -> "April"
            4 -> "May"
            5 -> "June"
            6 -> "July"
            7 -> "August"
            8 -> "September"
            9 -> "October"
            10 -> "November"
            11 -> "December"
            else -> "Broken Uh Oh"
        }
    }
    private fun getStartDate(calendar: Calendar, year: Int, month: Int, day: Int)
    {
        if(day != -1)
        {
            // set it to the saved date
            tvStartMonth.text = getMonth(month)
            tvStartDay.text = "$day"
        }
        else
        {
            // set it to the current date if they dont have one selected
            val currentMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            tvStartMonth.text = "$currentMonth"
            tvStartDay.text = "$currentDay"
        }
    }
    private fun clickedCalendar()
    {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the selected date
                handleSelectedDate(calendar, selectedYear, selectedMonth, selectedDay)
                getStartDate(calendar, selectedYear, selectedMonth, selectedDay)
                tvSobrietyCount.text = getDaysSober(selectedYear, selectedMonth, selectedDay)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
        datePickerDialog.show()
    }
    private fun handleSelectedDate(calendar: Calendar, year: Int, month: Int, day: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("year", year)
        editor.putInt("month", month)
        editor.putInt("day", day)
        editor.apply()
    }
}