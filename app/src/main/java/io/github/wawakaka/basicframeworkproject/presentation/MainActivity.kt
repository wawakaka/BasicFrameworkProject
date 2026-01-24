package io.github.wawakaka.basicframeworkproject.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.wawakaka.basicframeworkproject.R
import io.github.wawakaka.basicframeworkproject.base.FragmentActivityCallbacks
import io.github.wawakaka.basicframeworkproject.presentation.content.CurrencyFragment
import io.github.wawakaka.basicframeworkproject.presentation.ui.components.AppTopBar
import io.github.wawakaka.basicframeworkproject.presentation.ui.theme.BasicFrameworkTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Main Activity with TOAD pattern (Milestone 6)
 * Uses MainViewModel for permission state management
 */
class MainActivity : AppCompatActivity(), FragmentActivityCallbacks {

    // ========== TOAD Pattern (Milestone 6) ==========
    // Inject MainViewModel from Koin
    private val mainViewModel: MainViewModel by viewModel()

    // Register permission launcher (must be before onCreate returns)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        mainViewModel.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BasicFrameworkTheme {
                MainScreenContent(
                    viewModel = mainViewModel,
                    onRequestPermission = { requestPermission() }
                )
            }
        }
    }

    /**
     * Request camera permission from the system
     * Called via effect from MainViewModel
     */
    private fun requestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
                Log.d(TAG, "Permission already granted")
                mainViewModel.onPermissionResult(granted = true)
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show rationale dialog explaining why we need the permission
                showPermissionRationaleDialog()
            }

            else -> {
                // Request permission
                Log.d(TAG, "Requesting camera permission")
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun showPermissionRationaleDialog() {
        // TODO M7: Show Material 3 Compose AlertDialog explaining camera permission need
        // For now, proceed with permission request
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun setToolbar(title: String, showUpButton: Boolean) {
        // Toolbar is now part of Compose hierarchy
        // This callback is no longer needed with Compose
    }

    companion object {
        val TAG: String? = MainActivity::class.java.canonicalName
    }
}

// ========== COMPOSABLE UI ==========

/**
 * Main screen content with TOAD pattern
 * Manages permission flow and displays currency screen when granted
 */
@Composable
fun MainScreenContent(
    viewModel: MainViewModel,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Collect state with lifecycle awareness
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Handle effects (permission requests, navigation)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MainUiEffect.RequestPermission -> {
                    Log.d("MainScreen", "Effect: RequestPermission")
                    onRequestPermission()
                }
                is MainUiEffect.NavigateToCurrency -> {
                    Log.d("MainScreen", "Effect: NavigateToCurrency - permission granted, showing currency")
                }
            }
        }
    }

    // Initialize permission check on first composition
    LaunchedEffect(Unit) {
        viewModel.handleEvent(MainUiEvent.OnCheckPermission)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Currency Rates",
                showUpButton = false
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is MainUiState.CheckingPermission -> {
                    Text("Checking permissions...")
                }
                is MainUiState.PermissionGranted -> {
                    // Show currency screen via Fragment (will migrate to full Compose in future)
                    CurrencyFragmentContainer()
                }
                is MainUiState.PermissionDenied -> {
                    PermissionDeniedContent(
                        onRequestPermission = {
                            viewModel.handleEvent(MainUiEvent.OnRequestPermission)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Fragment container for CurrencyFragment
 * Temporary bridge until full Compose migration
 */
@Composable
fun CurrencyFragmentContainer(
    modifier: Modifier = Modifier
) {
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { context ->
            FragmentContainerView(context).apply {
                id = R.id.fragment_container
            }
        },
        modifier = modifier.fillMaxSize(),
        update = { view ->
            // Get FragmentManager from context
            val activity = view.context as? AppCompatActivity
            activity?.supportFragmentManager?.let { fragmentManager ->
                // Add CurrencyFragment if not already added
                val currentFragment = fragmentManager.findFragmentById(view.id)
                if (currentFragment == null) {
                    fragmentManager.commit {
                        replace(view.id, CurrencyFragment())
                    }
                }
            }
        }
    )
}

/**
 * Permission denied screen with retry button
 */
@Composable
fun PermissionDeniedContent(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Camera permission is required")
        Text("to use this application")
        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.padding(16.dp)
        )
        Button(onClick = onRequestPermission) {
            Text("Request Permission")
        }
    }
}
