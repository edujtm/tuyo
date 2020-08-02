package me.edujtm.tuyo.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.edujtm.tuyo.di.components.ActivityComponentProvider
import me.edujtm.tuyo.di.components.ComponentProvider

// TODO: Comment these methods
inline fun <reified T : ViewModel> FragmentActivity.viewModel(
        crossinline provider: () -> T
) = viewModels<T> {
        object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = provider() as T
        }
}

inline fun <reified T : ViewModel> Fragment.viewModel(
        crossinline provider: () -> T
) = viewModels<T> {
        object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = provider() as T
        }
}


inline fun <reified T : ViewModel> Fragment.activityViewModel(
        crossinline provider: () -> T
) = activityViewModels<T> {
        object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = provider() as T
        }
}

inline fun <reified T : Activity> Activity.startActivity(noinline intentAddons: ((Intent) -> Unit)? = null) {
        val intent = Intent(this, T::class.java)
        intentAddons?.let { intent.apply(it) }
        startActivity(intent)
}

fun Context.startImplicit(intentAddons: (Intent) -> Unit) : ActivityInfo? {
        val intent = Intent().apply(intentAddons)
        val activityInfo = intent.resolveActivityInfo(packageManager, intent.flags)
        if (activityInfo != null && activityInfo.exported) {
                startActivity(intent)
        }
        return activityInfo
}

/** Allows the application to expose the dagger component to activities */
val Activity.injector
        get() = (application as ComponentProvider).component

val Fragment.activityInjector
        get() = (requireActivity() as ActivityComponentProvider).activityInjector