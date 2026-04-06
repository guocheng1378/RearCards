package com.example.rearcards.cards

import android.os.Bundle
import hk.uwu.reareye.widgetapi.RearWidgetNoticeOptions
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 时钟卡片 - 显示当前时间和日期
 */
class ClockCard(
    private val showSeconds: Boolean = true,
    private val showDate: Boolean = true,
    private val is24Hour: Boolean = true,
) : BaseCard(
    name = "时钟",
    business = "clock_card",
    priority = 50
) {
    override fun buildPayload(): Bundle {
        val now = Date()
        val timeFormat = if (is24Hour) {
            if (showSeconds) "HH:mm:ss" else "HH:mm"
        } else {
            if (showSeconds) "hh:mm:ss a" else "hh:mm a"
        }
        val dateFormat = "yyyy年MM月dd日 EEEE"

        return Bundle().apply {
            putString("title", "🕐 时钟")
            putString("time", SimpleDateFormat(timeFormat, Locale.getDefault()).format(now))
            if (showDate) {
                putString("date", SimpleDateFormat(dateFormat, Locale.CHINESE).format(now))
            }
        }
    }

    override fun buildOptions() = RearWidgetNoticeOptions(
        sticky = true,
        showTimeTip = false, // 时钟卡片自己显示时间，不需要系统时间提示
        priority = priority
    )
}
