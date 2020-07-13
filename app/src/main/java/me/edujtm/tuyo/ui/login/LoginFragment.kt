package me.edujtm.tuyo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.SignInButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.login_btn as loginBtn
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.auth.AuthState
import me.edujtm.tuyo.common.activityViewModel
import me.edujtm.tuyo.common.injector

class LoginFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModel {
        requireActivity().injector.mainViewModel
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        val loginBtn = root.findViewById<SignInButton>(R.id.login_btn)
        loginBtn.setSize(SignInButton.SIZE_WIDE)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            mainViewModel.denyAuthentication()
            requireActivity().finish()
        }

        // I followed this pattern from the Google documentation on conditional navigation
        // but I'm afraid that showing a Snackbar from LiveData might cause it to reappear
        // on resubscription.
        // I believe it wont happen because the login screen is never supposed to be on the
        // background of the backstack, but I'm not sure.
        mainViewModel.authState.observe(viewLifecycleOwner, Observer { authState ->
            when (authState) {
                is AuthState.InvalidAuthentication -> view.showMessage(authState.error.message)
                is AuthState.Authenticated -> navController.popBackStack()
            }
        })

        loginBtn.setOnClickListener {
            mainViewModel.signIn()
        }
    }

    private fun View.showMessage(message: String?) {
        val error = message ?: "Unknown error"
        Snackbar.make(this, error, Snackbar.LENGTH_LONG).show()
    }
}