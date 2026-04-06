package com.example.rearcards.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.rearcards.cards.CardManager

/**
 * 卡片更新前台服务 - 保持卡片持续更新
 */
class CardUpdateService : Service() {
    private lateinit var cardManager: CardManager

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        cardManager = CardManager(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        cardManager.connect {
            cardManager.startAutoUpdate()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        cardManager.destroy()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "背屏卡片服务",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "保持背屏卡片持续更新"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("RearCards")
            .setContentText("背屏卡片正在运行")
            .setSmallIcon(android.R.drawable.ic_menu_today)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "rear_cards_service"
        private const val NOTIFICATION_ID = 1001
    }
}
