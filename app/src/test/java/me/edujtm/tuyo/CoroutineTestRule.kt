package me.edujtm.tuyo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.edujtm.tuyo.domain.DispatcherProvider
import org.junit.rules.TestWatcher
import org.junit.runner.Description


class CoroutineTestRule(val testDispatcher: TestCoroutineDispatcher) : TestWatcher() {
    val testDispatchers = object: DispatcherProvider {
        override val computation = testDispatcher
        override val main = testDispatcher
        override val io = testDispatcher
    }

    val testCoroutineScope = TestCoroutineScope(testDispatcher)

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}