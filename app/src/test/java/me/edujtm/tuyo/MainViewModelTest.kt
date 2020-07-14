package me.edujtm.tuyo

import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.edujtm.tuyo.auth.AuthManager
import me.edujtm.tuyo.auth.AuthState
import me.edujtm.tuyo.auth.GoogleAccount
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test

typealias EventObserver<T> = (T) -> Unit

class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var mainVm : MainViewModel

    val account = GoogleAccount("123", "edujtm@test.com", "Eduardo",
        "photo")

    private val authManager : AuthManager = mockk()
    private val authStateObserver: Observer<AuthState<GoogleAccount>> = mockk(relaxed = true)
    private val eventObserver: EventObserver<MainViewModel.Event> = mockk(relaxed = true)

    @Before
    fun setup() {
        every { authManager.getUserAccount() } returns account
        mainVm = MainViewModel(authManager)
        mainVm.authState.observeForever(authStateObserver)
        mainVm.events.startListening(eventObserver)
    }

    @Test
    fun `authentication state should start as Unauthenticated`() {
        val mockManager = mockk<AuthManager>()
        every { mockManager.getUserAccount() } returns null

        val mainViewModel = MainViewModel(mockManager)

        assertThat(mainViewModel.authState.value, instanceOf(AuthState.Unauthenticated::class.java))
    }

    @Test
    fun `authentication state should start as Authenticated when there's already an user`() {
        val mockManager = mockk<AuthManager>()
        every { mockManager.getUserAccount() } returns account

        val mainViewModel = MainViewModel(mockManager)

        assertThat(mainViewModel.authState.value, instanceOf(AuthState.Authenticated::class.java))
    }

    @Test
    fun `sign in should emit sign in event with Google SignInIntent`() {
        val testIntent = Intent()
        every { authManager.getSignInIntent() } returns testIntent

        mainVm.signIn()

        verify(exactly = 1) { authManager.getSignInIntent() }
        verify(exactly = 1) { eventObserver(MainViewModel.Event.SignIn(testIntent) ) }
    }
}