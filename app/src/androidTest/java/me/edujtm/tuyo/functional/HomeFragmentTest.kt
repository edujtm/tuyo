package me.edujtm.tuyo.functional

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.R
import me.edujtm.tuyo.fakes.FakeAuthManager
import me.edujtm.tuyo.ui.adapters.PlaylistHeaderAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class HomeFragmentTest {

    val itemPosition = 3

    @get:Rule
    val intentRule = object : IntentsTestRule<MainActivity>(MainActivity::class.java) {
        override fun beforeActivityLaunched() {
            FakeAuthManager.autoLogin = true
        }

        override fun getActivityIntent(): Intent {
            return Intent().putExtra(
                MainActivity.USER_EMAIL, FakeAuthManager.FAKE_ACCOUNT.email
            )
        }

        override fun afterActivityFinished() {
            super.afterActivityFinished()
            FakeAuthManager.autoLogin = false
        }
    }

    @Test
    fun test_should_open_playlist_items_when_clicking_on_playlist_header() {
        // The user starts on the Home Fragment with a list of headers
        onView(withId(R.id.playlist_header_list)).check(matches(isDisplayed()))

        // When the user clicks on an playlist header item
        onView(withId(R.id.playlist_header_list))
            .perform(actionOnItemAtPosition<PlaylistHeaderAdapter.ViewHolder>(itemPosition, click()))

        // The playlist items should be displayed
        onView(withId(R.id.playlist_recycler_view)).check(matches(isDisplayed()))
    }
}