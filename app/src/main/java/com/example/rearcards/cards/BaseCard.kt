package com.example.rearcards.cards

import android.os.Bundle
import hk.uwu.reareye.widgetapi.RearWidgetApiClient
import hk.uwu.reareye.widgetapi.RearWidgetNoticeOptions
import hk.uwu.reareye.widgetapi.RearWidgetNoticeTicket

/**
 * 背屏卡片基类
 */
abstract class BaseCard(
    val name: String,
    val business: String,
    val priority: Int = 500,
) {
    protected var ticket: RearWidgetNoticeTicket? = null
    protected var isActive = false

    /**
     * 注册卡片到背屏系统
     */
    fun register(client: RearWidgetApiClient, targetPackage: String) {
        client.registerBusinessWithoutFile(
            targetPackage = targetPackage,
            business = business,
            defaultPriority = priority
        )
    }

    /**
     * 注销卡片
     */
    fun unregister(client: RearWidgetApiClient, targetPackage: String) {
        ticket?.let { client.removeNotice(it) }
        client.unregisterBusiness(targetPackage, business)
        ticket = null
        isActive = false
    }

    /**
     * 发布/更新卡片内容
     */
    fun publish(client: RearWidgetApiClient, targetPackage: String) {
        val payload = buildPayload()
        val options = buildOptions()

        if (ticket == null) {
            ticket = client.postNotice(targetPackage, business, payload, options)
        } else {
            client.updateNotice(ticket!!, payload, options)
        }
        isActive = true
    }

    /**
     * 移除卡片显示
     */
    fun remove(client: RearWidgetApiClient) {
        ticket?.let { client.removeNotice(it) }
        ticket = null
        isActive = false
    }

    /**
     * 子类实现：构建卡片数据
     */
    protected abstract fun buildPayload(): Bundle

    /**
     * 子类可覆盖：构建显示选项
     */
    protected open fun buildOptions(): RearWidgetNoticeOptions {
        return RearWidgetNoticeOptions(
            sticky = true,
            showTimeTip = false,
            priority = priority
        )
    }
}
