package cn.luorenmu.action.entity

/**
 * @author LoMu
 * Date 2024.11.30 18:08
 */
data class WebScreenshotSetting(
    var enable: Boolean = false,
    var show: Boolean = false,
    var webPool: Int = 1,
    var windowHeight: Int = 1080,
    var windowWidth: Int = 1920,
    var screenshotSettingList: MutableList<ScreenshotSetting> = mutableListOf(),
)

data class ScreenshotSetting(
    var id: String,
    var url: String,
    var cropX: Int = 0,
    var cropY: Int = 0,
    var cropWidth: Int = 0,
    var cropHeight: Int = 0,
    var scrollTimeout: Int = 2000,
)