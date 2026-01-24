package io.github.wawakaka.basicframeworkproject.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.FragmentActivityCallbacks
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyFragment
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.AppTopBar
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.koin.androidx.scope.createScope
import org.koin.core.scope.Scope

class MainActivity : AppCompatActivity(), FragmentActivityCallbacks, MainContract.View {

    private val scope: Scope by lazy { createScope(this) }
    private val presenter: MainPresenter by scope.inject()

    // Register permission launcher (must be before onCreate returns)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        presenter.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BasicFrameworkTheme {
                MainScreen()
            }
        }

        presenter.attach(this)
        presenter.checkPermission()
    }

    override fun onDestroy() {
        presenter.detach()
        scope.close()
        super.onDestroy()
    }

    @Composable
    private fun MainScreen() {
        Scaffold(
            topBar = {
                AppTopBar(
                    title = "Currency Rates",
                    showUpButton = false
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            // For now, embed Fragment in Compose
            // In M6, we'll replace this with full Compose navigation
            androidx.compose.ui.viewinterop.AndroidView(
                factory = { context ->
                    FragmentContainerView(context).apply {
                        id = R.id.fragment_container
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                update = { view ->
                    // Add CurrencyFragment if not already added
                    val fragmentManager = supportFragmentManager
                    val currentFragment = fragmentManager.findFragmentById(view.id)
                    if (currentFragment == null) {
                        fragmentManager.commit {
                            replace(view.id, CurrencyFragment())
                        }
                    }
                }
            )
        }
    }

    override fun setToolbar(title: String, showUpButton: Boolean) {
        // Toolbar is now part of Compose hierarchy
        // This callback is no longer needed with Compose
    }

    override fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                presenter.onPermissionResult(granted = true)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show rationale dialog explaining why we need the permission
                showPermissionRationaleDialog()
            }

            else -> {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        // TODO: Show Material 3 Compose AlertDialog explaining camera permission need
        // For now, proceed with permission request
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onPermissionGranted() {
        Log.d(TAG, "Camera permission granted")
    }

    override fun onPermissionDenied() {
        Log.d(TAG, "Camera permission denied")
    }

    companion object {
        val TAG: String? = MainActivity::class.java.canonicalName
    }
}
