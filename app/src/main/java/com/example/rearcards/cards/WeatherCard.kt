package com.example.rearcards.cards

import android.os.Bundle
import hk.uwu.reareye.widgetapi.RearWidgetNoticeOptions
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 天气卡片 - 显示当前天气信息
 */
class WeatherCard(
    private var city: String = "北京",
    private val apiKey: String = "", // 和风天气 API Key（可选）
) : BaseCard(
    name = "天气",
    business = "weather_card",
    priority = 100
) {
    private var temperature = "--"
    private var condition = "获取中..."
    private var humidity = "--"
    private var windDir = "--"
    private var icon = "☀️"

    override fun buildPayload(): Bundle = Bundle().apply {
        putString("title", "🌤 $city")
        putString("content", "$icon $condition $temperature°C")
        putString("detail", "湿度: $humidity% | 风向: $windDir")
        putString("update_time", SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()))
    }

    override fun buildOptions() = RearWidgetNoticeOptions(
        sticky = true,
        showTimeTip = true,
        priority = priority
    )

    /**
     * 更新天气数据
     * 使用 wttr.in 免费 API（无需 key）
     */
    suspend fun refresh() {
        try {
            val url = URL("https://wttr.in/$city?format=j1")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val text = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(text)
                val current = json.getJSONArray("current_condition").getJSONObject(0)

                temperature = current.getString("temp_C")
                humidity = current.getString("humidity")
                windDir = current.getString("winddir16Point")
                condition = current.getJSONArray("weatherDesc")
                    .getJSONObject(0).getString("value")

                icon = when {
                    condition.contains("晴") || condition.contains("Clear") -> "☀️"
                    condition.contains("多云") || condition.contains("Partly") -> "⛅"
                    condition.contains("阴") || condition.contains("Overcast") -> "☁️"
                    condition.contains("雨") || condition.contains("Rain") -> "🌧️"
                    condition.contains("雪") || condition.contains("Snow") -> "❄️"
                    condition.contains("雷") || condition.contains("Thunder") -> "⛈️"
                    condition.contains("雾") || condition.contains("Fog") -> "🌫️"
                    else -> "🌤"
                }
            }
        } catch (e: Exception) {
            condition = "获取失败"
        }
    }

    fun setCity(city: String) {
        this.city = city
    }
}
