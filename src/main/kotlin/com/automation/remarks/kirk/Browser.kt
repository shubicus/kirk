package com.automation.remarks.kirk

import com.automation.remarks.kirk.core.*
import com.automation.remarks.kirk.ext.autoClose
import org.openqa.selenium.Alert
import org.openqa.selenium.By
import org.openqa.selenium.Dimension
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.interactions.Actions

class Browser(val driver: WebDriver = ChromeDriver()) : SearchContext, Navigable {

    companion object

    var config: Configuration = loadConfig(Configuration::class)

    var baseUrl: String by baseUrl()

    var timeout: Int by timeout()

    var holdOpen: Boolean? by autoClosable()

    var poolingInterval: Double by poolingInterval()

    var startMaximized: Boolean? by startMaximized()

    var screenSize: List<Int> by screenSize()

    val actions = Actions(driver)

    fun with(block: Browser.() -> Unit): Browser {
        return this.apply(block)
    }

    val currentUrl: String by lazy {
        driver.currentUrl
    }

    val js: JsExecutor = JsExecutor(driver)

    override fun open(url: String) {
        driver.autoClose(holdOpen)
        if (screenSize.isNotEmpty()) {
            driver.manage().window().size = Dimension(screenSize[0], screenSize[1])
        } else if (startMaximized!!) {
            driver.manage().window().maximize()
        }

        if (isAbsoluteUrl(url)) {
            driver.navigate().to(url)
        } else {
            driver.navigate().to(baseUrl + url)
        }
    }

    private fun isAbsoluteUrl(url: String): Boolean {
        return (url.startsWith("http://") || url.startsWith("https://"))
    }

    fun to(url: String) {
        open(url)
    }

    fun interact(block: Actions.()->Unit){
        this.actions.apply(block).build().perform()
    }

    override fun <T : Page> to(pageClass: (Browser) -> T): T {
        val page = pageClass(this)
        page.url?.let { open(it) }
        return page
    }

    fun <T : Page> at(pageClass: (Browser) -> T): T {
        val page = pageClass(this)
        assert(page.at.invoke(this))
        return page
    }

    override fun <T : Page> at(pageClass: (Browser) -> T, closure: T.() -> Unit) {
        val page = pageClass(this)
        page.closure()
    }

    override fun element(by: By): KElement {
        return KElement(by, driver).apply {
            waitTimeout = timeout
            waitPoolingInterval = poolingInterval
        }
    }

    override fun all(by: By): KElementCollection {
        return KElementCollection(by, driver).apply {
            waitTimeout = timeout
            waitPoolingInterval = poolingInterval
        }
    }

    fun takeScreenshot(saveTo: String = "${System.getProperty("user.dir")}/build/screen_${System.currentTimeMillis()}.png") {
        ScreenshotContainer(driver, saveTo).takeScreenshotAsFile()
    }

    override fun back(): Browser {
        driver.navigate().back()
        return this
    }

    override fun forward(): Browser {
        driver.navigate().forward()
        return this
    }

    override fun refresh(): Browser {
        driver.navigate().refresh()
        return this
    }

    override fun quit() {
        driver.quit()
    }

    val alert: Alert
        get() = driver.switchTo().alert()

    val title: String
        get() = driver.title
}