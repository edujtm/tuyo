package me.edujtm.tuyo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.app_bar_main.*
import me.edujtm.tuyo.auth.AuthState
import me.edujtm.tuyo.common.observe
import me.edujtm.tuyo.ui.login.LoginFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.android.synthetic.main.activity_main.nav_view as navView

class MainActivity : AppCompatActivity() {

    private val mainViewModel : MainViewModel by viewModel()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // --- UI setup ---
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // --- navigation setup ---
        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home,
            R.id.navigation_liked_videos,
            R.id.navigation_search,
            R.id.navigation_login
        ), drawer)

        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_login -> hideOverlay()
                else -> showOverlay()
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // --- listeners ---
        // Common logic to all fragments, if for some reason the user goes offline
        // navigate to the login fragment.
        mainViewModel.authState.observe(this, Observer { authState ->
            when (authState) {
                 is AuthState.Unauthenticated -> navController.navigate(R.id.navigation_login)
            }
        })

        mainViewModel.events.observe(this) { event ->
            when (event) {
                is MainViewModel.Event.SignIn -> {
                    startActivityForResult(event.signInIntent, REQUEST_SIGN_IN)
                }
                is MainViewModel.Event.CheckGooglePlayServices -> {
                    checkGooglePlayAvailability()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SIGN_IN) {
            mainViewModel.authenticate(data)
        } else if (requestCode == REQUEST_PLAY_SERVICES) {
            mainViewModel.checkGoogleApiServices()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkGooglePlayAvailability() {
        val api = GoogleApiAvailability.getInstance()
        val resultCode = api.isGooglePlayServicesAvailable(this)
        mainViewModel.setGoogleApiResult(resultCode)
    }

    private fun hideOverlay() {
        fab.visibility = View.GONE
        supportActionBar?.hide()
        setDrawerEnabled(false)
    }

    private fun showOverlay() {
        fab.visibility = View.VISIBLE
        supportActionBar?.show()
        setDrawerEnabled(true)
    }

    private fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled)
            DrawerLayout.LOCK_MODE_UNLOCKED
        else
            DrawerLayout.LOCK_MODE_LOCKED_CLOSED

        drawer.setDrawerLockMode(lockMode)
        toggle.isDrawerIndicatorEnabled = enabled
    }

    companion object {
        const val REQUEST_SIGN_IN = 1000
        const val REQUEST_PLAY_SERVICES = 2000
    }
}