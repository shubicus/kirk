package com.automation.remarks.kirk

import com.automation.remarks.kirk.conditions.ElementCondition
import com.automation.remarks.kirk.conditions.be
import com.automation.remarks.kirk.ext.classes
import com.automation.remarks.kirk.locators.ElementLocator
import com.automation.remarks.kirk.locators.InnerListWebElementLocator
import com.automation.remarks.kirk.locators.InnerWebElementLocator
import com.automation.remarks.kirk.locators.WebElementLocator
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement


/**
 * Created by sergey on 24.06.17.
 */
class KElement(locator: ElementLocator<WebElement>,
               driver: WebDriver) : Element<WebElement>(locator, driver) {

    constructor(locator: By, driver: WebDriver) :
            this(WebElementLocator(locator, driver), driver)

    val webElement: WebElement
        get() = locator.find()

    val text: String
        get() = webElement.text

    val tagName: String
        get() = webElement.tagName

    val isEnabled: Boolean
        get() = webElement.isEnabled

    val isDisplayed: Boolean
        get() = webElement.isDisplayed

    val isSelected: Boolean
        get() = webElement.isSelected

    val classes: List<String>
        get() = webElement.classes

    fun click() {
        execute { click() }
    }

    fun clear() {
        execute { clear() }
    }

    fun sendKeys(vararg keysToSend: CharSequence) {
        execute { sendKeys(*keysToSend) }
    }

    infix fun should(condition: ElementCondition) {
        super.should(condition)
    }

    fun setValue(value: String): KElement {
        return execute {
            clear()
            sendKeys(value)
        }
    }

    fun execute(commands: WebElement.() -> Unit): KElement {
        super.should(be.visible).commands()
        return this
    }

    override fun toString(): String {
        return locator.description
    }

    fun element(byCss: String): KElement {
        return element(By.cssSelector(byCss))
    }

    fun element(by: By): KElement {
        return KElement(InnerWebElementLocator(by, this), driver)
    }

    fun all(byCss: String): KElementCollection {
        return all(By.cssSelector(byCss))
    }

    fun all(by: By): KElementCollection {
        return KElementCollection(InnerListWebElementLocator(by, this), driver)
    }
}