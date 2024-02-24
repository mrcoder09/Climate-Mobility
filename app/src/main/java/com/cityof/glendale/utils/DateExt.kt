package com.cityof.glendale.utils

import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateFormats {
    const val APP_DATE_FORMAT = "MM/dd/yyyy"

    const val DATE_FORMAT = "MMMM dd, yyyy"
    const val DATE_FORMAT_WITH_DAY = "EEEE MMM dd, yyyy"
    const val DATE_FORMAT_1 = "dd-MM-yyyy"
    const val DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss"
    const val DATE_FORMAT_3 = "MM-dd-yyyy"
    const val DATE_FORMAT_4 = "MMMM dd yyyy"
    const val DATE_FORMAT_WITH_TIMEZONE = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"


    const val TIME_FORMAT = "HH:mm:ss"
    const val TIME_FORMAT_1 = "HH:mm"
    const val TIME_FORMAT_2 = "hh:mm a"
}


/**
 * Function for formatting date to readable string.
 * @param format format of Date to be out.
 * @param locale locale of Date to be out.
 * @return formatted date
 * @author SATNAM SINGH
 */
fun Date.xtFormat(
    format: String = DateFormats.APP_DATE_FORMAT, locale: Locale = Locale.getDefault()
) = SimpleDateFormat(format, locale).format(this)

/**
 * Function for formatting date to readable string.
 * @param format format of Date to be out.
 * @param locale locale of Date to be out.
 * @return formatted date
 * @author SATNAM SINGH
 */
fun Long.xtFormat(
    format: String = DateFormats.APP_DATE_FORMAT, locale: Locale = Locale.getDefault()
) = SimpleDateFormat(format, locale).format(this)


/**
 * Function for parsing string date to DATE object.
 * @param format format of incoming date.
 * @param locale locale of incoming date.
 * @return Date()?
 * @author SATNAM SINGH
 */
fun String?.xtParseDate(
    format: String = DateFormats.APP_DATE_FORMAT, locale: Locale = Locale.getDefault()
): Date? {
    return this?.let {
        try {
            SimpleDateFormat(format, locale).parse(it)
        } catch (e: ParseException) {
            null
        }
    } ?: kotlin.run {
        null
    }
}


/**
 * Function for providing difference in days
 * @param date1 From which date to compare with, By default taking today as date.
 * @param date2 To which date to get difference of days.
 * @author Satnam Singh
 */
fun xtDiffInDays(date1: Date = Date(), date2: Date): Int {
    val diff = TimeUnit.DAYS.convert(date1.time - date2.time, TimeUnit.MILLISECONDS).toInt()

    if (diff == 0) {
        val calendar1 = Calendar.getInstance().apply { time = date1 }
        val calendar2 = Calendar.getInstance().apply { time = date2 }

        val year1 = calendar1.get(Calendar.YEAR)
        val year2 = calendar2.get(Calendar.YEAR)
        if (year1 != year2) return year1 - year2

        val day1 = calendar1.get(Calendar.DAY_OF_YEAR)
        val day2 = calendar2.get(Calendar.DAY_OF_YEAR)
        return day1 - day2
    }

    return diff
}


/**
 * Function for striping time from given date,
 * It will set date with 00:00:00(HH:MIN:SEC) time.
 * @author Satnam Singh
 */
fun Date.xtStripTime(): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    this.time = cal.time.time
    return this
}


/**
 * Function faclitating timestamp to duration break up like
 * since 9 hrs 10 mins.
 * @author Satnam Singh
 */
fun Long.xtFormatMillis(): String {
    return xtDurationBreakUp(this)
}

/**
 * Function for converting timestamp to duraction break up.
 * @param millis timestamp for conversion.
 * @return formatted result like 09 Hr 33 Min 21 Sec.
 * @author Satnam Singh
 */
fun xtDurationBreakUp(millis: Long): String {
    var millis = millis
    if (millis <= 0) return ""

    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    millis -= TimeUnit.HOURS.toMillis(hours)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    millis -= TimeUnit.MINUTES.toMillis(minutes)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
    val sb = StringBuilder()
    if (hours > 0) {
        sb.append(hours.xt2Digit())
        sb.append(" Hr").append(" ")
    }
    sb.append(minutes.xt2Digit())
    sb.append(" Min").append(" ")
    sb.append(seconds.xt2Digit())
    sb.append(" Sec")
    return sb.toString()
}

/**
 * Function for converting HR MIN SEC to milliseconds.
 * @param hr hour from 0 to 12
 * @param min minutes from 0 to 30
 * @param sec seconds from 0 to 60
 * @return milliseconds.
 * @author Satnam Singh
 */
fun xtMillis(hr: Long, min: Long, sec: Long): Long {
    val hrMillis = TimeUnit.HOURS.toMillis(hr)
    val minMillis = TimeUnit.MINUTES.toMillis(min)
    val secMillis = TimeUnit.SECONDS.toMillis(sec)
    return hrMillis + minMillis + secMillis
}

/**
 * Function for converting Date objet to calendar.
 * @return Calendar Object.
 * @author Satnam Singh
 */
fun Date.xtCalendar() = Calendar.getInstance().let { cal ->
    cal.time = this
    cal
}

/**
 * Function providing list of days from given date.
 * @param upTo upto how many days(By Default 1 day)
 * @param from from which date(By Default current date).
 * @return list of date.
 * @author Satnam Singh
 */
fun xtWeekDays(upTo: Int = 1, from: Date = Date()) = mutableListOf<Date>().also {
    for (i in 0..upTo) {
        it.add(Calendar.getInstance().apply {
            time = from
            add(Calendar.DAY_OF_MONTH, i)
        }.time)
    }
}

/**
 * Function for checking if given date fall under today.
 * @param date2 date to compare with.
 * @return true or false.
 * @author Satnam Singh
 */
fun Date.xtIsToday(date2: Date = Date()) =
    !(this.xtStripTime() > date2.xtStripTime() && this.xtStripTime() < date2.xtStripTime())

fun Date.xtIsInTheFuture() = this.time > Date().time


fun getYesterday(locale: Locale = Locale.getDefault()): Date {
    val calendar = Calendar.getInstance(locale)
    calendar.add(Calendar.DATE, -1)
    return calendar.time
}

fun getTomorrow(): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, +1)
    return calendar.time
}

fun formatMillis(millis: Long, format: String = "yyyy-MM-dd"): String {
    val simple: DateFormat = SimpleDateFormat(format, Locale.getDefault())
    val result = Date(millis)
    return simple.format(result)
}

fun timesAgo(millis: Long?): String {

    return millis?.let { timeStamp ->
        val suffix = "ago"
        val pastTime = Date(timeStamp)
        val now = Date()
        val diff = now.time - pastTime.time
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        if (seconds < 60) {
            "$seconds seconds $suffix"
        } else if (minutes < 60) {
            "$minutes minutes $suffix"
        } else if (hours < 24) {
            "$hours hours $suffix"
        } else if (days >= 7) {
            if (days > 360) {
                "${(days / 360)} years $suffix"
            } else if (days > 30) {
                "${(days / 30)} months $suffix"
            } else {
                "${(days / 7)} week $suffix"
            }
        } else if (days < 7) {
            "$days days $suffix"
        } else ""
    } ?: run {
        ""
    }
}


//fun convertDateToAFormat(dateToFormat: String): String? {
////            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
//    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH)
//    val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
//    val date = inputFormat.parse(dateToFormat)
//    return outputFormat.format(date!!)
//}


fun compareTwoDatesIfEquals(date1: String, date2: String): Boolean {
    try {
        val sdformat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val d1 = sdformat.parse(date1)
        val d2 = sdformat.parse(date2)
        if (d1 != null) {
            return when {
                d1.compareTo(d2) == 0 -> {
                    true
                }

                else -> false
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}


fun combineDateTime(date: Date?, time: Date?): Date? {


    val dateCalendar = Calendar.getInstance()
    dateCalendar.time = date
    Timber.d("DATE_EXT: ${dateCalendar.timeInMillis}")
    val timeCalendar = Calendar.getInstance()
    timeCalendar.time = time
    Timber.d("DATE_EXT: ${timeCalendar.timeInMillis}")

    dateCalendar[Calendar.HOUR_OF_DAY] = timeCalendar[Calendar.HOUR_OF_DAY]
    dateCalendar[Calendar.MINUTE] = timeCalendar[Calendar.MINUTE]
    dateCalendar[Calendar.SECOND] = timeCalendar[Calendar.SECOND]
    Timber.d("DATE_EXT: RESULT: ${dateCalendar.timeInMillis}")
    return dateCalendar.time
}

fun combineDateTime(date: Long?, time: Long?): Long {


    val dateCalendar = Calendar.getInstance()
    if (date != null) {
        Timber.d("DATE_EXT: DATE: ${dateCalendar.timeInMillis}")
        dateCalendar.timeInMillis = date
    }

    val timeCalendar = Calendar.getInstance()
    if (time != null) {
        Timber.d("DATE_EXT: TIME: ${timeCalendar.timeInMillis}")
        timeCalendar.timeInMillis = time
    }


    dateCalendar[Calendar.HOUR_OF_DAY] = timeCalendar[Calendar.HOUR_OF_DAY]
    dateCalendar[Calendar.MINUTE] = timeCalendar[Calendar.MINUTE]
    dateCalendar[Calendar.SECOND] = timeCalendar[Calendar.SECOND]
    Timber.d("DATE_EXT: RESULT: ${dateCalendar.timeInMillis}")
    return dateCalendar.timeInMillis
}