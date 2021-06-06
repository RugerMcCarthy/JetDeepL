package edu.bupt.jetdeepl

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
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

    @ExperimentalAnimationApi
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.requestCopyToClipboardData.observe(this) {
            copyResultToClipboard(it)
        }
        setContent {
            var scaffoldState = rememberScaffoldState()
            JetDeepLTheme {
                Scaffold(
                    scaffoldState = scaffoldState,
                    snackbarHost = {
                        SnackbarHost(it) { data ->
                            Snackbar(
                                snackbarData = data
                            )
                        }
                    }
                ) {
                    TranslateLayout(viewModel, scaffoldState)
                }
            }
        }
    }
}

