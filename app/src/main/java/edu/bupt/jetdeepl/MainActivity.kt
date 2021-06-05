package edu.bupt.jetdeepl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.rememberScaffoldState
import edu.bupt.jetdeepl.model.MainViewModel
import edu.bupt.jetdeepl.ui.TranslateLayout
import edu.bupt.jetdeepl.ui.theme.JetDeepLTheme

class MainActivity : ComponentActivity() {
    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var scaffoldState = rememberScaffoldState()
            val viewModel: MainViewModel by viewModels()
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

