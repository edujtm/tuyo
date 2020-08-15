package me.edujtm.tuyo.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import me.edujtm.tuyo.di.components.ActivityComponentProvider
import me.edujtm.tuyo.di.components.ComponentProvider

/**
 * Abstracts the ViewModel instantiation with the correct lifecycle for an Activity using a custom
 * [ViewModelProvider.Factory] that delegates instantiation to a [provider] factory. The lifecycle
 * is still managed by the [ViewModelProvider].
 *
 * This allows for the ViewModel dependencies to be provided by a DI container without having
 * to worry about managing the lifecycle scope.
 *
 * @param provider a factory function that specifies how to get the ViewModel
 * @return a lazy delegate which allows custom initialization of the ViewModel.
 */
inline fun <reified T : ViewModel> FragmentActivity.viewModel(
        crossinline provider: () -> T
) = viewModels<T> {
        object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = provider() as T
        }
}

/**
 * Abstracts the instantiation of a ViewModel that is scoped to a Fragment
 * while delegating lifecycle management to [ViewModelProvider].
 *
 * @param  provider  a factory function that specifies how to get the ViewModel
 * @return a lazy delegate which allows custom initialization of the ViewModel
 * @see FragmentActivity.viewModel
 */
inline fun <reified T : ViewModel> Fragment.viewModel(
        crossinline provider: () -> T
) = viewModels<T> {
        object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = provider() as T
        }
}

/**
 * Allows the fragment to retrieve the parent activity's ViewModel
 *
 * @see FragmentActivity.viewModel
 */
inline fun <reified T : ViewModel> Fragment.activityViewModel(
        crossinline provider: () -> T
) = activityViewModels<T> {
        object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = provider() as T
        }
}

/**
 * Starts an explicit intent. Extra information can be added by using [intentAddons].
 *
 * @param intentAddons lambda function that allows to apply modifications to the intent.
 */
inline fun <reified T : Activity> Activity.startActivity(noinline intentAddons: ((Intent) -> Unit)? = null) {
        val intent = Intent(this, T::class.java)
        intentAddons?.let { intent.apply(it) }
        startActivity(intent)
}

/**
 * Starts an implicit intent while checking if it's resolvable.
 *
 * @param intentAddons lambda function that configures the intent before using it.
 * @return ActivityInfo for the resolved activity.
 */
fun Context.startImplicit(intentAddons: (Intent) -> Unit) : ActivityInfo? {
        val intent = Intent().apply(intentAddons)
        val activityInfo = intent.resolveActivityInfo(packageManager, intent.flags)
        if (activityInfo != null && activityInfo.exported) {
                startActivity(intent)
        }
        return activityInfo
}

/**
 * Lifecycle aware delegate property that helps accessing ViewBinding instances from Fragments
 * without leaking views.
 */
fun <T : ViewBinding> Fragment.viewBinding(viewBindingFactory: (View) -> T)
        = FragmentViewBindingDelegate(this, viewBindingFactory)

/**
 * Simplifies accessing ViewBindings from an Activity.
 *
 * @param viewBindingFactory factory function that instantiates the ViewBinding (normally SomeViewBinding::inflate)
 */
inline fun <T : ViewBinding> AppCompatActivity.viewBinding(crossinline viewBindingFactory: (LayoutInflater) -> T) =
        lazy(LazyThreadSafetyMode.NONE) {
                viewBindingFactory.invoke(layoutInflater)
        }

/** Allows the application to expose the dagger component to activities */
val Activity.injector
        get() = (application as ComponentProvider).component

/** Allows activity scoped Components to be accessed by fragments */
val Fragment.activityInjector
        get() = (requireActivity() as ActivityComponentProvider).activityInjector