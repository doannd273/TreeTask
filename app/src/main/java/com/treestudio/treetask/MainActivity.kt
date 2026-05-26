package com.treestudio.treetask

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.core.os.LocaleListCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doannd3.treetask.core.designsystem.debug.DebugOverlayWrapper
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.permission.AppPermission
import com.doannd3.treetask.core.permission.PermissionChecker
import com.doannd3.treetask.core.permission.PermissionStatus
import com.treestudio.treetask.ui.TreeTaskApp
import com.treestudio.treetask.ui.debug.buildDebugOverlayUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissionMap ->
        }

    private val permissionChecked by lazy {
        PermissionChecker(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestDebugNotificationPermissionIfNeeded()

        splashScreen.setKeepOnScreenCondition {
            viewModel.isLoadingMain
        }

        setContent {
            val languageTag = viewModel.appLanguageTag

            LaunchedEffect(languageTag) {
                if (!languageTag.isNullOrEmpty()) {
                    val current = AppCompatDelegate.getApplicationLocales().get(0)?.language
                    if (current != languageTag) {
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(languageTag),
                        )
                    }
                }
            }

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

    private fun requestDebugNotificationPermissionIfNeeded() {
        if (!BuildConfig.DEBUG) return

        val status = permissionChecked.check(AppPermission.PostNotification)
        when (status) {
            is PermissionStatus.Denied -> {
                notificationPermissionLauncher.launch(status.missingPermissions.toTypedArray())
            }

            PermissionStatus.Granted,
            PermissionStatus.NotRequired,
            -> {
                Unit
            }
        }
    }
}
