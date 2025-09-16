package com.wizsuite.event.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateFormatHelper {
    companion object{
        var IND = Locale("en", "IN")

        var DATE_FORMAT_ONE = "yyyy.MM.dd G 'at' HH:mm:ss z" //2001.07.04 AD at 12:08:56 PDT
        var DATE_FORMAT_TWO = "EEE, MMM d, ''yy" //Wed, Jul 4, '01
        var DATE_FORMAT_THREE = "yyyyy.MMMM.dd GGG hh:mm aaa" //02001.July.04 AD 12:08 PM
        var DATE_FORMAT_FOUR = "EEE, d MMM yyyy HH:mm:ss Z" //Wed, 4 Jul 2001 12:08:56 -0700
        var EEE_MMM_dd_yyyy = "EEE MMM dd, yyyy" //Wed 4 Jul, 2001
        var EEE_MMM_dd_yyyy_hh_mm = "EEE MMM dd, yyyy hh:mm aaa" //Sun Sept 10, 2023 10:10 am
        var yyyy_MM_dd = "yyyy-MM-dd"
        var DATE_FORMAT_SEVEN = "dd-MMM-yyyy h:mm a"
        var dd_MM_yyyy = "dd-MM-yyyy"
        var EEE_MMM_d = "EEE, MMM d"
        var yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss"
        var MMM_d = "MMM d"
        var h_mm_AM_PM = "h:mm a"
        var MMM = "MMM"
        var dd = "dd"

        fun dateFormat(inputPattern: String?, outputPattern: String?, dateStr: String?): String? {

            val inputFormat = SimpleDateFormat(inputPattern, IND)
            val outputFormat = SimpleDateFormat(outputPattern, IND)
            var str: String? = null
            try {
                val date = inputFormat.parse(dateStr!!)!!
                str = outputFormat.format(date)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str
        }
    }
}