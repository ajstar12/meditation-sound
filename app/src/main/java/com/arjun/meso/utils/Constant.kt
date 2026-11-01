package com.arjun.meso.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date

class Constant {

    companion object {
        var tag = "Arjun_Test"
        fun getCurrentDate(outputFormat: String?): String {
            //2022-10-03 15:07:04
            //yyyy-MM-dd HH:mm:ss

            var resultDate = ""
            try {
                val outputSdf = SimpleDateFormat(outputFormat)

                resultDate = outputSdf.format(Date())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return resultDate
        }

        fun log(type: String, data: String, e: Throwable?) {
            Log.e(tag, tag + " " + type + " : " + data, e)
        }

        fun log(type: String, data: String) {
            Log.e(tag, tag + " " + type + " : " + data)
        }

        fun log(type: String, params: Map<String, String>) {
            var result = ""
            for ((key, value) in params) {
                result = """
            $result
            $key:$value
            """.trimIndent()
            }
            Log.e(tag, tag + " " + type + " : " + result)
        }
    }
}