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
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import java.lang.RuntimeException


class MainViewModelTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule(TestCoroutineDispatcher())

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
            mainViewModel.sendEvent(MainViewModel.Event.UiError(RuntimeException("Some event")))

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
            mainViewModel.sendEvent(MainViewModel.Event.UiError(RuntimeException("Some event")))

            // WHEN: a subscriber starts listening
            val job = launch {
                mainViewModel.events.consumeEach {
                    events += it
                }
            }

            // THEN: the event should be received
            assertThat(events[0], instanceOf(MainViewModel.Event.UiError::class.java))

            job.cancelAndJoin()
        }
}