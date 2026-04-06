package com.example.rearcards.cards

import android.content.Context
import com.example.rearcards.RearCardsApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * 卡片管理器 - 统一管理所有背屏卡片
 */
class CardManager(private val context: Context) {
    private val client = RearCardsApp.instance.widgetClient
    private val targetPackage = context.packageName
    private val scope = CoroutineScope(Dispatchers.Main)
    private var updateJob: Job? = null

    // 所有卡片实例
    val clockCard = ClockCard()
    val weatherCard = WeatherCard()
    val stepCard = StepCounterCard(context)
    val batteryCard = BatteryCard(context)
    val customCard = CustomTextCard()

    val allCards: List<BaseCard> get() = listOf(clockCard, weatherCard, stepCard, batteryCard, customCard)

    // 启用状态
    private val enabledCards = mutableSetOf<String>()

    /**
     * 连接 API 并注册所有启用的卡片
     */
    fun connect(onConnected: (() -> Unit)? = null) {
        client.bind(context) {
            // 默认启用时钟和电池
            enableCard(clockCard)
            enableCard(batteryCard)
            onConnected?.invoke()
        }
    }

    /**
     * 启用并注册一张卡片
     */
    fun enableCard(card: BaseCard) {
        if (!client.isConnected()) return
        card.register(client, targetPackage)
        card.publish(client, targetPackage)
        enabledCards.add(card.business)
    }

    /**
     * 禁用并移除一张卡片
     */
    fun disableCard(card: BaseCard) {
        card.unregister(client, targetPackage)
        enabledCards.remove(card.business)
    }

    /**
     * 切换卡片启用状态
     */
    fun toggleCard(card: BaseCard) {
        if (card.business in enabledCards) {
            disableCard(card)
        } else {
            enableCard(card)
        }
    }

    fun isCardEnabled(card: BaseCard): Boolean = card.business in enabledCards

    /**
     * 启动定时更新
     * - 时钟：每秒更新
     * - 天气：每 10 分钟
     * - 步数/电池：每 30 秒
     */
    fun startAutoUpdate() {
        updateJob?.cancel()
        updateJob = scope.launch {
            var tick = 0L

            while (isActive) {
                // 每秒更新时钟
                if (clockCard.business in enabledCards) {
                    clockCard.publish(client, targetPackage)
                }

                // 每 30 秒更新步数和电池
                if (tick % 30 == 0L) {
                    if (stepCard.business in enabledCards) {
                        stepCard.publish(client, targetPackage)
                    }
                    if (batteryCard.business in enabledCards) {
                        batteryCard.publish(client, targetPackage)
                    }
                }

                // 每 10 分钟更新天气
                if (tick % 600 == 0L && weatherCard.business in enabledCards) {
                    launch(Dispatchers.IO) {
                        weatherCard.refresh()
                        weatherCard.publish(client, targetPackage)
                    }
                }

                // 自定义卡片不需要自动更新
                if (customCard.business in enabledCards && tick == 0L) {
                    customCard.publish(client, targetPackage)
                }

                delay(1000)
                tick++
            }
        }

        stepCard.startListening()
    }

    /**
     * 停止自动更新
     */
    fun stopAutoUpdate() {
        updateJob?.cancel()
        updateJob = null
        stepCard.stopListening()
    }

    /**
     * 释放所有资源
     */
    fun destroy() {
        stopAutoUpdate()
        allCards.forEach { it.unregister(client, targetPackage) }
        client.unbind()
    }
}
