package me.edujtm.tuyo.domain

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/** Allows injection of test coroutine dispatchers */
interface DispatcherProvider {
    val io : CoroutineContext
    val computation: CoroutineContext
    val main : CoroutineContext
}

/** Default implementation used for production code */
class DefaultDispatcherProvider
    @Inject constructor() : DispatcherProvider {
    override val io = Dispatchers.IO
    override val computation = Dispatchers.Default
    override val main = Dispatchers.Main
}