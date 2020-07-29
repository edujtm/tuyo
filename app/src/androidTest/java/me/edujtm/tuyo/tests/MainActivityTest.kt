package me.edujtm.tuyo.tests

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.R
import me.edujtm.tuyo.ui.login.LoginActivity
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val activityScenario = object: IntentsTestRule<MainActivity>(MainActivity::class.java) {
        override fun getActivityIntent(): Intent {
            return Intent().apply {
                putExtra(MainActivity.USER_EMAIL, "user@email.com")
            }
        }
    }

    @Ignore("This test works but it needs the FakeAuthManager to be setup properly with dagger")
    @Test
    fun test_user_is_able_to_logout_on_menu_item() {
        // The user navigates to main activity on the HomeFragment
        onView(withId(R.id.text_home))
            .check(matches(ViewMatchers.isDisplayed()))

        // WHEN: clicking in the logout menu item
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText("Logout")).perform(click())

        // THEN: the user should be redirected to login screen
        intending(hasComponent(LoginActivity::class.java.name))
    }
}