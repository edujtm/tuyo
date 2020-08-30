package me.edujtm.tuyo.functional

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.longClick
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.DrawerMatchers
import androidx.test.espresso.contrib.NavigationViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.matcher.UriMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.R
import me.edujtm.tuyo.fakes.FakeAuthManager
import me.edujtm.tuyo.ui.adapters.PlaylistAdapter
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class PlaylistFragmentTest {

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
    fun test_long_click_on_item_should_open_action_mode() {
        mockYoutubeIntent()
        navigateToPlaylistFragment()

        // Not good
        Thread.sleep(1000)

        onView(withId(R.id.playlist_recycler_view))
            .perform(actionOnItemAtPosition<PlaylistAdapter.ViewHolder>(1, longClick()))

        onView(withId(R.id.action_copy))
            .check(matches(isDisplayed()))
    }

    private fun navigateToPlaylistFragment() {
        // The drawer is currently closed
        onView(withId(R.id.drawer_layout))
            .check(matches(DrawerMatchers.isClosed(Gravity.LEFT)))
            .perform(DrawerActions.open())

        // The user clicks on Liked Videos option
        onView(withId(R.id.nav_view))
            .perform(NavigationViewActions.navigateTo(R.id.navigation_liked_videos))

        onView(withId(R.id.playlist_recycler_view))
            .check(matches(isDisplayed()))
    }

    private fun mockYoutubeIntent() {
        // Mocks youtube intent
        val youtubeIntent = Matchers.allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(UriMatchers.hasScheme("vnd.youtube"))
        )
        // mocks webview in case youtube app is not available on emulator
        val webviewIntent = Matchers.allOf(
            hasAction(Intent.ACTION_VIEW),
            hasData(UriMatchers.hasHost("www.youtube.com"))
        )

        val activityResult = Instrumentation.ActivityResult(RESULT_OK, null)
        intending(youtubeIntent).respondWith(activityResult)
        intending(webviewIntent).respondWith(activityResult)
    }
}