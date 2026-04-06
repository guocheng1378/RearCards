package com.example.rearcards.cards

import android.os.Bundle
import hk.uwu.reareye.widgetapi.RearWidgetApiClient
import hk.uwu.reareye.widgetapi.RearWidgetNoticeOptions
/**
 * 背屏卡片基类
 */
abstract class BaseCard(
    val name: String,
    val business: String,
    val priority: Int = 500,
) {
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
        client.disableBusinessDisplay(targetPackage, business)
        client.unregisterBusiness(targetPackage, business)
        isActive = false
    }

    /**
     * 发布/更新卡片内容
     * 注意：RearWidgetApiClient.postNotice() 返回 Unit（不返回 ticket）
     * 因此每次重新 post，不再依赖 updateNotice
     */
    fun publish(client: RearWidgetApiClient, targetPackage: String) {
        val payload = buildPayload()
        val options = buildOptions()
        client.postNotice(targetPackage, business, payload, options)
        isActive = true
    }

    /**
     * 移除卡片显示
     */
    fun remove(client: RearWidgetApiClient, targetPackage: String) {
        client.disableBusinessDisplay(targetPackage, business)
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
