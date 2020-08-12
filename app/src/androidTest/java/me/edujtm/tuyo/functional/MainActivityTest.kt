package me.edujtm.tuyo.functional

import android.content.Intent
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers.isClosed
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.R
import me.edujtm.tuyo.fakes.FakeAuthManager
import me.edujtm.tuyo.ui.login.LoginActivity
import org.junit.*

/**
 * Tests the navigation after the user has already logged in.
 * The way I've structured the app, MainActivity coincides with the Logged In
 * scope.
 */
class MainActivityTest {

    @get:Rule
    val activityScenario = object: IntentsTestRule<MainActivity>(MainActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            FakeAuthManager.autoLogin = true
        }

        override fun getActivityIntent(): Intent {
            return Intent().apply {
                putExtra(MainActivity.USER_EMAIL, "user@email.com")
            }
        }

        override fun afterActivityFinished() {
            super.afterActivityFinished()
            FakeAuthManager.autoLogin = false
        }
    }


    @Test
    fun test_user_is_able_to_logout_on_menu_item() {
        // The user navigates to main activity on the HomeFragment
        onView(withId(R.id.playlist_header_list))
            .check(matches(isDisplayed()))

        // WHEN: clicking in the logout menu item
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText("Logout")).perform(click())

        // THEN: the user should be redirected to login screen
        intending(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun test_user_can_access_liked_videos_on_drawer() {
        // User starts navigation on home fragment
        onView(withId(R.id.playlist_header_list)).check(matches(isDisplayed()))

        // The drawer is currently closed
        onView(withId(R.id.drawer_layout))
            .check(matches(isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open())

        // The user clicks on Liked Videos option
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.navigation_liked_videos))

        // The playlist should be visible
        onView(withId(R.id.playlist_recycler_view)).check(matches(isDisplayed()))
    }
}