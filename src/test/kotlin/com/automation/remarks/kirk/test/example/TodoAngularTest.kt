package com.automation.remarks.kirk.test.example

import com.automation.remarks.kirk.Browser
import com.automation.remarks.kirk.conditions.have
import com.automation.remarks.kirk.ext.drive
import org.testng.annotations.Test

/**
 * Created by sergey on 09.07.17.
 */
// tag::TodoAngularTest[]
class TodoAngularTest{
    @Test fun testCanAddNewTaskAndDelete() {
        Browser.drive {
            to(::TodoPage) {
                addTasks("Item0")
                taskList.should(have.size(1))
                deleteTask("Item0")
                taskList.should(have.size(0))
            }
        }
    }

    @Test fun testCanDeactivateTask() {
        Browser.drive {
            to(::TodoPage) {
                addTasks("A", "B", "C")
                deactivateTask("A")
                counter.should(have.text("2"))
                goToCompletedTab()
                taskList.should(have.exactText("A"))
            }
        }
    }
}
// end::TodoAngularTest[]