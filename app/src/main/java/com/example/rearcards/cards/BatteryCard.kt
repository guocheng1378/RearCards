package com.example.rearcards.cards

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import hk.uwu.reareye.widgetapi.RearWidgetNoticeOptions

/**
 * 电池卡片 - 显示设备电池状态
 */
class BatteryCard(
    private val context: Context,
) : BaseCard(
    name = "电池",
    business = "battery_card",
    priority = 150
) {
    override fun buildPayload(): Bundle {
        val batteryStatus = getBatteryStatus()
        val level = batteryStatus.level
        val isCharging = batteryStatus.isCharging
        val temperature = batteryStatus.temperature
        val voltage = batteryStatus.voltage

        val statusEmoji = when {
            isCharging && level >= 90 -> "🔌"
            isCharging -> "⚡"
            level <= 15 -> "🪫"
            level <= 30 -> "🔋"
            else -> "🔋"
        }

        return Bundle().apply {
            putString("title", "$statusEmoji 电池")
            putString("level", "$level%")
            putString("status", if (isCharging) "充电中" else "未充电")
            putString("temperature", "${temperature / 10}°C")
            putString("voltage", "${voltage}mV")
            putString("bar", buildBatteryBar(level))
        }
    }

    override fun buildOptions() = RearWidgetNoticeOptions(
        sticky = true,
        showTimeTip = true,
        priority = priority
    )

    private fun getBatteryStatus(): BatteryInfo {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val percentage = if (scale > 0) (level * 100) / scale else 0

        val status = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL

        val temperature = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val voltage = intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0

        return BatteryInfo(percentage, isCharging, temperature, voltage)
    }

    private fun buildBatteryBar(level: Int): String {
        val filled = level / 10
        val empty = 10 - filled
        return "█".repeat(filled) + "░".repeat(empty)
    }

    private data class BatteryInfo(
        val level: Int,
        val isCharging: Boolean,
        val temperature: Int,
        val voltage: Int,
    )
}
