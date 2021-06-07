package edu.bupt.jetdeepl

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Insets
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ExperimentalAnimatedInsets
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import edu.bupt.jetdeepl.model.MainViewModel
import edu.bupt.jetdeepl.ui.TranslateLayout
import edu.bupt.jetdeepl.ui.theme.JetDeepLTheme

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    private fun copyResultToClipboard(result: String) {
        val clipboardManager: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("translate", result)
        clipboardManager.setPrimaryClip(clipData)
    }

    @ExperimentalAnimatedInsets
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        viewModel.requestCopyToClipboardData.observe(this) {
            copyResultToClipboard(it)
        }
        setContent {
            var scaffoldState = rememberScaffoldState()
            JetDeepLTheme {
                var systemUiController = rememberSystemUiController()
                systemUiController.setSystemBarsColor(MaterialTheme.colors.background, true)
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Scaffold(
                        scaffoldState = scaffoldState,
                        snackbarHost = {
                            SnackbarHost(it) { data ->
                                Snackbar(
                                    snackbarData = data
                                )
                            }
                        },
                        modifier = Modifier.padding(
                            paddingValues = rememberInsetsPaddingValues(
                                insets = LocalWindowInsets.current.systemBars
                            )
                        )
                    ) {
                        TranslateLayout(viewModel, scaffoldState)
                    }
                }
            }
        }
    }
}

