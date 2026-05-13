package com.treestudio.treetask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doannd3.treetask.core.designsystem.debug.DebugOverlayWrapper
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.treestudio.treetask.ui.TreeTaskApp
import com.treestudio.treetask.ui.debug.buildDebugOverlayUiState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestPermissions()

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoadingMain
        }

        setContent {
            TreeTaskTheme {
                viewModel.startDestination?.let { dest ->
                    key(dest) {
                        val overlayState =
                            buildDebugOverlayUiState(
                                env = BuildConfig.ENV,
                                isDebug = BuildConfig.DEBUG,
                            )
                        DebugOverlayWrapper(
                            isVisible = overlayState.show,
                            label = overlayState.label.orEmpty(),
                        ) {
                            val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
                            TreeTaskApp(
                                startDestination = dest,
                                isOnline = isOnline,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissions() {
    }
}
