# RearCards - 背屏卡片合集

为小米 17 Pro / Pro Max 背屏提供多种实用卡片，基于 [REAR Widget API](https://github.com/killerprojecte/REAREye/tree/dev/rear-widget-api) 开发。

## 包含卡片

| 卡片 | 说明 | 更新频率 |
| --- | --- | --- |
| 🕐 **时钟** | 当前时间和日期 | 每秒 |
| 🌤 **天气** | 实时天气（温度、湿度、风向） | 每 10 分钟 |
| 👟 **步数** | 今日步数和目标进度 | 每 30 秒 |
| 🔋 **电池** | 电量、温度、电压 | 每 30 秒 |
| 📝 **自定义文字** | 用户自定义的标题和内容 | 手动更新 |

## 前置条件

- 已 Root 的小米 17 Pro / Pro Max
- 已安装 **LSPosed** 框架
- 已安装 **REAREye** 模块并启用

## 构建

```bash
git clone https://github.com/你的用户名/RearCards.git
cd RearCards
./gradlew assembleDebug
```

安装：

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 使用

1. 安装并打开应用
2. 授予必要权限（通知、运动传感器）
3. 开启想要显示的卡片
4. 天气卡片需要输入城市名称并联网
5. 自定义卡片可以输入任意标题和内容

## 开发新卡片

继承 `BaseCard` 类即可创建新卡片：

```kotlin
class MyCard : BaseCard(
    name = "我的卡片",
    business = "my_card",
    priority = 250
) {
    override fun buildPayload(): Bundle = Bundle().apply {
        putString("title", "自定义标题")
        putString("content", "卡片内容")
    }
}
```

然后在 `CardManager` 中注册即可。

## 许可证

MIT
