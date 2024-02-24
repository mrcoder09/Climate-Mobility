package com.cityof.glendale.composables.components

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import com.cityof.glendale.BaseApp
import com.cityof.glendale.R
import com.cityof.glendale.theme.Purple
import com.cityof.glendale.theme.RobotoFontFamily
import ir.kaaveh.sdpcompose.sdp
import ir.kaaveh.sdpcompose.ssp
import timber.log.Timber
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDatePicker(
    showDialog: MutableState<Boolean>, onDismiss: () -> Unit, onDateSelected: (Long) -> Unit
) {

//    MaterialTheme(
//        typography = RobotoTypography
//    ) {
    val year13Old = Calendar.getInstance().let {
        it.add(Calendar.YEAR, -13)
        it
    }

    val dialogState = rememberDatePickerState(initialSelectedDateMillis = year13Old.timeInMillis,
        initialDisplayMode = DisplayMode.Picker,
        yearRange = 1903..year13Old.get(Calendar.YEAR),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long) =
                utcTimeMillis <= year13Old.timeInMillis
        })
    val selectedDate = dialogState.selectedDateMillis

    if (showDialog.value) {
        DatePickerDialog(modifier = Modifier.padding(1.sdp),
            colors = DatePickerDefaults.colors(containerColor = Color.White),
            onDismissRequest = {
                showDialog.value = false
            },
            confirmButton = {

            }) {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                )
            ) {
                DatePicker(
                    state = dialogState,
                    modifier = Modifier.background(Color.White),
                    showModeToggle = false,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            end = 20.sdp, bottom = 10.sdp
                        ), horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier, contentAlignment = Alignment.CenterEnd
                    ) {
                        AppClickableText(stringRes = R.string.select, spanStyle = SpanStyle(
                            color = Purple,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.ssp,
                            fontFamily = RobotoFontFamily
                        ), onClick = {
                            showDialog.value = false
                            onDateSelected(selectedDate ?: System.currentTimeMillis())
                        })
                    }

                    Spacer(modifier = Modifier.width(8.sdp))

                    Box(
                        modifier = Modifier, contentAlignment = Alignment.CenterEnd
                    ) {
                        AppClickableText(stringRes = R.string.cancel, spanStyle = SpanStyle(
                            color = Purple,
                            fontWeight = FontWeight.W400,
                            fontSize = 14.ssp,
                            fontFamily = RobotoFontFamily
                        ), onClick = {
                            showDialog.value = false
                            onDismiss()
                        })
                    }
                }

            }
        }
    }
//    }
}


fun NativeDatePicker(
    context: Context,
    calendar: Calendar = Calendar.getInstance(Locale.getDefault()),
    minDate: Long? = null,
    onTimeChanged: (Long) -> Unit
) {

    Timber.d("1: ${context.resources.configuration.locales} ${BaseApp.myLang}")
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val datePickerDialog = android.app.DatePickerDialog(
        context, R.style.app_date_picker_theme, { d, yearOfYear, monthOfYear, dayOfMonth ->
            // Do something with the date
            val time = Calendar.getInstance().also {
                it.set(Calendar.YEAR, yearOfYear)
                it.set(Calendar.MONTH, monthOfYear)
                it.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.timeInMillis
            Timber.d("$time")
            onTimeChanged(time)
        }, year, month, day
    )

    datePickerDialog.setOnDismissListener {}
    if (minDate == null) datePickerDialog.datePicker.maxDate = calendar.timeInMillis
    if (minDate != null) datePickerDialog.datePicker.minDate = minDate
    datePickerDialog.show()
}


fun NativeTimePicker(
    context: Context,
    calendar: Calendar = Calendar.getInstance(Locale.getDefault()),
    onTimeChanged: (Long, Int, Int) -> Unit
) {

    val c = Calendar.getInstance()

    // on below line we are getting our hour, minute.
    val hour = c.get(Calendar.HOUR_OF_DAY)
    val minute = c.get(Calendar.MINUTE)

    // on below line we are initializing
    // our Time Picker Dialog
    val timePickerDialog = TimePickerDialog(
        context, { view, hourOfDay, minute ->
            // on below line we are setting selected
            // time in our text view.
            val time = Calendar.getInstance(Locale.getDefault()).also {
                it.set(Calendar.HOUR_OF_DAY, hourOfDay)
                it.set(Calendar.MINUTE, minute)
            }.timeInMillis
            onTimeChanged(time, hour, minute)
        }, hour, minute, false
    )
    // at last we are calling show to
    // display our time picker dialog.
    timePickerDialog.show()
}