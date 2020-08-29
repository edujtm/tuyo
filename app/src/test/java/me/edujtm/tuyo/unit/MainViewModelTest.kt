package me.edujtm.tuyo.unit

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import me.edujtm.tuyo.CoroutineTestRule
import me.edujtm.tuyo.MainViewModel
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


class MainViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

    @Test
    fun `checkGoogleApiServices should emit event to MainActivity`() =
        coroutineRule.testCoroutineScope.runBlockingTest {
            val mainViewModel = MainViewModel()
            val eventReceived = mutableListOf<MainViewModel.Event>()

            val job = launch {
                mainViewModel.events.consumeEach { event ->
                    eventReceived += event
                }
            }

            // WHEN: checkGoogleApiServices is called
            mainViewModel.checkGoogleApiServices()

            // THEN: an event should be sent to the MainViewModel
            assertEquals(eventReceived.size, 1)
            Assert.assertThat(
                eventReceived[0],
                instanceOf(MainViewModel.Event.CheckGooglePlayServices::class.java)
            )

            job.cancelAndJoin()
        }

    @Test
    fun `should consume events when subscribers are available (do not re-emit on resubscription)`() =
        coroutineRule.testCoroutineScope.runBlockingTest {
            // Avoids emitting events twice on due to resubscription on configuration changes
            val mainViewModel = MainViewModel()
            var eventsQnt = 0

            // GIVEN: a subscriber is available
            var job = launch {
                mainViewModel.events.consumeEach {
                    eventsQnt++
                }
            }

            // GIVEN: an event was emitted
            mainViewModel.checkGoogleApiServices()

            // WHEN: A new subscription is made
            job.cancelAndJoin()
            job = launch {
                mainViewModel.events.consumeEach {
                    eventsQnt++
                }
            }

            // THEN: The event should not be emitted again
            assertEquals(1, eventsQnt)

            job.cancelAndJoin()
    }

    @Test
    fun `should buffer events when no subscribers are available`() =
        coroutineRule.testCoroutineScope.runBlockingTest {
            val mainViewModel = MainViewModel()
            val events = mutableListOf<MainViewModel.Event>()

            // GIVEN: no subscribers available
            // GIVEN: an event was made
            mainViewModel.checkGoogleApiServices()

            // WHEN: a subscriber starts listening
            val job = launch {
                mainViewModel.events.consumeEach {
                    events += it
                }
            }

            // THEN: the event should be received
            assertEquals(MainViewModel.Event.CheckGooglePlayServices, events[0])

            job.cancelAndJoin()
        }
}