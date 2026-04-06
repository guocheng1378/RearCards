package com.example.rearcards.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.rearcards.cards.CardManager
import com.example.rearcards.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var cardManager: CardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardManager = CardManager(this)

        setupUI()
        connectApi()
    }

    private fun setupUI() {
        // 时钟卡片开关
        binding.switchClock.setOnCheckedChangeListener { _, isChecked ->
            toggleCard(cardManager.clockCard, isChecked)
        }

        // 天气卡片开关
        binding.switchWeather.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val city = binding.editCity.text.toString().ifBlank { "北京" }
                cardManager.weatherCard.setCity(city)
                lifecycleScope.launch(Dispatchers.IO) {
                    cardManager.weatherCard.refresh()
                    runOnUiThread { cardManager.enableCard(cardManager.weatherCard) }
                }
            } else {
                cardManager.disableCard(cardManager.weatherCard)
            }
        }

        // 步数卡片开关
        binding.switchSteps.setOnCheckedChangeListener { _, isChecked ->
            toggleCard(cardManager.stepCard, isChecked)
        }

        // 电池卡片开关
        binding.switchBattery.setOnCheckedChangeListener { _, isChecked ->
            toggleCard(cardManager.batteryCard, isChecked)
        }

        // 自定义文字卡片开关
        binding.switchCustom.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val title = binding.editTitle.text.toString().ifBlank { "📝 备忘" }
                val content = binding.editContent.text.toString().ifBlank { "自定义内容" }
                cardManager.customCard.updateContent(title, content)
            }
            toggleCard(cardManager.customCard, isChecked)
        }

        // 更新自定义卡片内容
        binding.btnUpdateCustom.setOnClickListener {
            val title = binding.editTitle.text.toString().ifBlank { "📝 备忘" }
            val content = binding.editContent.text.toString().ifBlank { "自定义内容" }
            cardManager.customCard.updateContent(title, content)
            if (cardManager.isCardEnabled(cardManager.customCard)) {
                cardManager.customCard.publish(cardManager.widgetClient, packageName)
            }
            Toast.makeText(this, "已更新", Toast.LENGTH_SHORT).show()
        }

        // 刷新天气
        binding.btnRefreshWeather.setOnClickListener {
            val city = binding.editCity.text.toString().ifBlank { "北京" }
            cardManager.weatherCard.setCity(city)
            lifecycleScope.launch(Dispatchers.IO) {
                cardManager.weatherCard.refresh()
                if (cardManager.isCardEnabled(cardManager.weatherCard)) {
                    cardManager.weatherCard.publish(cardManager.widgetClient, packageName)
                }
                runOnUiThread { Toast.makeText(this@MainActivity, "天气已更新", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun toggleCard(card: com.example.rearcards.cards.BaseCard, enable: Boolean) {
        if (enable) {
            cardManager.enableCard(card)
        } else {
            cardManager.disableCard(card)
        }
    }

    private fun connectApi() {
        cardManager.connect {
            runOnUiThread {
                Toast.makeText(this, "已连接背屏 API", Toast.LENGTH_SHORT).show()
                updateSwitchStates()
            }
        }
        cardManager.startAutoUpdate()
    }

    private fun updateSwitchStates() {
        binding.switchClock.isChecked = cardManager.isCardEnabled(cardManager.clockCard)
        binding.switchWeather.isChecked = cardManager.isCardEnabled(cardManager.weatherCard)
        binding.switchSteps.isChecked = cardManager.isCardEnabled(cardManager.stepCard)
        binding.switchBattery.isChecked = cardManager.isCardEnabled(cardManager.batteryCard)
        binding.switchCustom.isChecked = cardManager.isCardEnabled(cardManager.customCard)
    }

    override fun onDestroy() {
        super.onDestroy()
        cardManager.destroy()
    }
}
