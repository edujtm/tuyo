package me.edujtm.tuyo.functional

import android.app.Activity.RESULT_OK
import android.app.Instrumentation
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import me.edujtm.tuyo.MainActivity
import me.edujtm.tuyo.R
import me.edujtm.tuyo.ui.login.LoginActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4ClassRunner::class)
class LoginActivityTest {

    @get:Rule
    val intentTestRule = IntentsTestRule(LoginActivity::class.java)

    @Test
    fun test_login_activity_should_try_to_get_user_account() {

        // GIVEN:
        val expectedIntent: Matcher<Intent> = Matchers.allOf(
            hasAction("com.google.android.gms.common.account.CHOOSE_ACCOUNT"),
            hasExtraWithKey("allowableAccountTypes")
        )
        val activityResult = createAccountPickerResultStub()
        intending(expectedIntent).respondWith(activityResult)

        // WHEN: clicking on the login button
        onView(withId(R.id.login_btn)).perform(click())

        // THEN: the intent for user account should be sent
        intended(expectedIntent)
        // the user should be redirected to main activity on success
        intended(hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun test_failed_login_should_show_snackbar_message() {
        // GIVEN: an account picking intent
        val expectedIntent: Matcher<Intent> = Matchers.allOf(
            hasAction("com.google.android.gms.common.account.CHOOSE_ACCOUNT"),
            hasExtraWithKey("allowableAccountTypes")
        )
        val activityResult = createAccountPickerErrorStub()
        intending(expectedIntent).respondWith(activityResult)

        // WHEN: clicking on the login button
        onView(withId(R.id.login_btn)).perform(click())

        // THEN: the intent for user account should be sent
        intended(expectedIntent)
        // And a error snackbar should be shown to the user
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("Authentication error")))
    }

    private fun createAccountPickerErrorStub(): Instrumentation.ActivityResult {
        return Instrumentation.ActivityResult(RESULT_OK, null)
    }

    private fun createAccountPickerResultStub(): Instrumentation.ActivityResult {
        val resultIntent = Intent()
        return Instrumentation.ActivityResult(RESULT_OK, resultIntent)
    }
}

