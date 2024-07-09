package com.luckyzyx.luckytool

import androidx.annotation.Keep

object DomeEntry {

    @JvmStatic
    @Keep
    fun getString(index: Int): String {
        return when (index) {
            1 -> "luckyzyxluckyzyx"
            2 -> "device_pcb"
            3 -> "device_sn"
            4 -> "device_prjName"
            else -> ""
        }
    }

    @JvmStatic
    @Keep
    fun getInt(index: Int): Int {
        return when (index) {
            1 -> 111
            2 -> 222
            else -> 0
        }
    }

    @JvmStatic
    @Keep
    fun getLong(index: Int): Long {
        return when (index) {
            1 -> 111L
            2 -> 222L
            else -> 0L
        }
    }

    @JvmStatic
    @Keep
    fun getFloat(index: Int): Float {
        return when (index) {
            1 -> 111F
            2 -> 222F
            else -> 0F
        }
    }

    @JvmStatic
    @Keep
    fun getList(index: Int): List<Any> {
        return when (index) {
            1 -> ArrayList<Any>(1).apply {
                add(1)
            }

            2 -> ArrayList<Any>(2).apply {
                add(1)
                add(2)
            }

            else -> ArrayList(0)
        }
    }
}