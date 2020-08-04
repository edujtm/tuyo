package me.edujtm.tuyo.unit

import me.edujtm.tuyo.MainViewModel
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test


class MainViewModelTest {

    @Test
    fun `checkGoogleApiServices should emit event to MainActivity`() {
        val mainViewModel = MainViewModel()
        val eventReceived = mutableListOf<MainViewModel.Event>()

        val subscription = mainViewModel.events.startListening { event ->
            eventReceived += event
        }

        // WHEN: checkGoogleApiServices is called
        mainViewModel.checkGoogleApiServices()

        // THEN: an event should be sent to the MainViewModel
        assertEquals(eventReceived.size, 1)
        Assert.assertThat(
            eventReceived[0],
            instanceOf(MainViewModel.Event.CheckGooglePlayServices::class.java)
        )

        // Close the events channel
        subscription.stopListening()
    }

}