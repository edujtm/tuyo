package me.edujtm.tuyo.common

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Lifecycle aware delegate property that allows fragments to access ViewBindings in a
 * simplified manner and avoids leaking Views by forgetting to null out the reference
 * in Fragment::onDestroyView.
 *
 * The normal usage is as such:
 * ```
 * class MyFragment : Fragment(R.layout.fragment) {
 *    private val binding : FragmentBinding by FragmentViewBindingDelegate(this, FragmentBinding::bind)
 * }
 * ```
 * but a extension function was made to reduce verbosity
 * @see me.edujtm.tuyo.common.viewBinding
 */
class FragmentViewBindingDelegate<T : ViewBinding>(
    val fragment: Fragment,
    val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding : T? = null

    init {
        // Allows the binding to be nulled out when views are destroyed, not the fragment
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observe(fragment) { viewLifecycleOwner ->
                    viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                        override fun onDestroy(owner: LifecycleOwner) {
                            binding = null
                        }
                    })
                }
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
             throw IllegalStateException("Cannot access view bindings when fragment views are destroyed")
        }

        return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
    }
}