package io.github.wawakaka.basicframeworkproject.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.BaseActivity
import io.github.wawakaka.basicframeworkproject.base.FragmentActivityCallbacks
import io.github.wawakaka.basicframeworkproject.databinding.ActivityMainBinding
import org.koin.android.ext.android.getKoin
import org.koin.core.scope.Scope

class MainActivity : BaseActivity(), FragmentActivityCallbacks, MainContract.View {

    private lateinit var binding: ActivityMainBinding
    private var scope: Scope? = null
    private lateinit var presenter: MainPresenter

    // Register permission launcher (must be before onCreate returns)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        presenter.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Koin presenter
        presenter = getKoin().get<MainPresenter>()

        presenter.attach(this)
        init()
    }

    override fun onDestroy() {
        presenter.detach()
        scope?.close()
        super.onDestroy()
    }

    override fun setToolbar(title: String, showUpButton: Boolean) {
        supportActionBar?.apply {
            this.title = title
            this.setDisplayShowHomeEnabled(true)
            this.setDisplayHomeAsUpEnabled(true)
        }
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
        setSupportActionBar(binding.toolbar)
        presenter.checkPermission()
    }

    companion object {
        val TAG: String? = MainActivity::class.java.canonicalName
    }
}
