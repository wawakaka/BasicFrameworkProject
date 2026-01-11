package io.github.wawakaka.basicframeworkproject.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import io.github.wawakaka.basicframeworkproject.base.BaseActivity
import io.github.wawakaka.basicframeworkproject.base.FragmentActivityCallbacks
import io.github.wawakaka.basicframeworkproject.presentation.components.AppTopBar
import io.github.wawakaka.basicframeworkproject.theme.BasicFrameworkTheme
import org.koin.android.ext.android.getKoin

class MainActivity : BaseActivity(), FragmentActivityCallbacks, MainContract.View {

    private lateinit var presenter: MainPresenter

    // Register permission launcher (must be before onCreate returns)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        presenter.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Koin presenter
        presenter = getKoin().get<MainPresenter>()

        presenter.attach(this)

        // Use Compose for UI
        setContent {
            BasicFrameworkTheme {
                Column(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    AppTopBar(title = "BasicFramework")
                    // NavHost will be added here in Phase 3
                }
            }
        }

        init()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun setToolbar(title: String, showUpButton: Boolean) {
        // TODO: Update Compose toolbar title dynamically
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
        // TODO: Show Material 3 AlertDialog explaining camera permission need
        // For now, proceed with permission request
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onPermissionGranted() {
        Log.d(TAG, "Camera permission granted")
        // TODO: Enable camera-dependent features
    }

    override fun onPermissionDenied() {
        Log.d(TAG, "Camera permission denied")
        // TODO: Show message to user, offer to open app settings
    }

    private fun init() {
        presenter.checkPermission()
    }

    companion object {
        val TAG: String? = MainActivity::class.java.canonicalName
    }
}
