package me.edujtm.tuyo.di.components

import me.edujtm.tuyo.MainActivity

/**
 * Used by [MainActivity] to distribute dependencies
 */
interface ActivityComponentProvider {
    val activityInjector: MainActivityComponent
}