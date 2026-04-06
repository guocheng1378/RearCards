package com.example.rearcards.cards

import android.os.Bundle
import hk.uwu.reareye.widgetapi.RearWidgetNoticeOptions

/**
 * 自定义文字卡片 - 显示用户自定义的文字内容
 */
class CustomTextCard(
    private var title: String = "📝 备忘",
    private var content: String = "点击编辑自定义内容",
    private var detail: String = "",
) : BaseCard(
    name = "自定义文字",
    business = "custom_text_card",
    priority = 300
) {
    override fun buildPayload(): Bundle = Bundle().apply {
        putString("title", title)
        putString("content", content)
        if (detail.isNotBlank()) {
            putString("detail", detail)
        }
    }

    override fun buildOptions() = RearWidgetNoticeOptions(
        sticky = true,
        showTimeTip = true,
        priority = priority
    )

    fun updateContent(title: String, content: String, detail: String = "") {
        this.title = title
        this.content = content
        this.detail = detail
    }
}
