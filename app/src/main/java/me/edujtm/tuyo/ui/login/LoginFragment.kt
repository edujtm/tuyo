package me.edujtm.tuyo.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.login_btn as loginBtn
import kotlinx.android.synthetic.main.fragment_login.name_et as nameInput
import kotlinx.android.synthetic.main.fragment_login.password_et as passwordInput
import me.edujtm.tuyo.MainViewModel
import me.edujtm.tuyo.R
import me.edujtm.tuyo.auth.AuthState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment() {

    private val mainViewModel: MainViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            mainViewModel.denyAuthentication()
            requireActivity().finish()
        }

        mainViewModel.authState.observe(viewLifecycleOwner, Observer { authState ->
            when (authState) {
                is AuthState.InvalidAuthentication -> view.showMessage(authState.error.message)
                is AuthState.Authenticated -> navController.popBackStack()
            }
        })

        loginBtn.setOnClickListener {
            val name = nameInput.text.toString()
            val password = passwordInput.text.toString()
            mainViewModel.authenticate(name, password)
        }
    }

    private fun View.showMessage(message: String?) {
        val error = message ?: "Unknown error"
        Snackbar.make(this, error, Snackbar.LENGTH_LONG).show()
    }
}