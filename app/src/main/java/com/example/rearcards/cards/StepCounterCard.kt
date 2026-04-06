package com.example.rearcards.cards

import android.content.Context
import android.os.Bundle
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import hk.uwu.reareye.widgetapi.RearWidgetNoticeOptions

/**
 * 步数卡片 - 显示今日步数
 */
class StepCounterCard(
    context: Context,
) : BaseCard(
    name = "步数",
    business = "step_counter_card",
    priority = 200
), SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var totalSteps = 0f
    private var initialSteps = -1f
    private var todaySteps = 0
    private var stepGoal = 10000

    override fun buildPayload(): Bundle = Bundle().apply {
        putString("title", "👟 今日步数")
        putString("steps", "$todaySteps")
        putString("goal", "$stepGoal")
        putString("progress", "${((todaySteps.toFloat() / stepGoal) * 100).toInt()}%")
        putString("emoji", if (todaySteps >= stepGoal) "🎉" else if (todaySteps > 5000) "💪" else "🚶")
    }

    override fun buildOptions() = RearWidgetNoticeOptions(
        sticky = true,
        showTimeTip = true,
        priority = priority
    )

    fun startListening() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                totalSteps = it.values[0]
                if (initialSteps < 0) {
                    initialSteps = totalSteps
                }
                todaySteps = (totalSteps - initialSteps).toInt()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun setStepGoal(goal: Int) {
        stepGoal = goal
    }
}
