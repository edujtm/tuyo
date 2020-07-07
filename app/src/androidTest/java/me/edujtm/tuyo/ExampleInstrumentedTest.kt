package me.edujtm.tuyo

import android.app.Application
import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnitRunner
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.edujtm.tuyo.ui.login.LoginFragment
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.stopKoin
import org.koin.dsl.module


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    val activityRule = ActivityTestRule(MainActivity::class.java)

    lateinit var mockVm : MainViewModel

    @Before
    fun setup() {
        mockVm = mockk()

        loadKoinModules(module {
            viewModel { mockVm }
        })
    }

    @After
    fun cleanUp() {
        stopKoin()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("me.edujtm.tuyo", appContext.packageName)
    }

    // I don't know how to do fragment instrumentation test yet
    fun loginScreenCheckGoogleServices() {
        every { mockVm.checkGoogleApiServices() }

        val loginScenario = launchFragmentInContainer<LoginFragment>()

        verify(exactly = 1) { mockVm.checkGoogleApiServices() }
    }
}