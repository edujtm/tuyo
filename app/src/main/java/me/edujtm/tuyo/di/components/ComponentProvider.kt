package me.edujtm.tuyo.di.components

import me.edujtm.tuyo.TuyoApp

/**
 * Being used by [TuyoApp] to expose the main component
 * of the application.
 */
interface ComponentProvider {
    val component: AppComponent
}

