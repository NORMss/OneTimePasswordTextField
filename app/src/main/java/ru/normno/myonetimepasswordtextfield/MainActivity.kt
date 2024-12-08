package ru.normno.myonetimepasswordtextfield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.normno.myonetimepasswordtextfield.ui.theme.MyOneTimePasswordTextFieldTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyOneTimePasswordTextFieldTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = viewModel<OtpViewModel>()
                    val state = viewModel.state.collectAsStateWithLifecycle()
                    val focusRequesters = remember {
                        List(4) {
                            FocusRequester()
                        }
                    }
                    val focusManager = LocalFocusManager.current
                    val keyboardManager = LocalSoftwareKeyboardController.current

                    LaunchedEffect(state.value.focusedIndex) {
                        state.value.focusedIndex?.let { focusedIndex ->
                            if (state.value.focusedIndex != null) {
                                focusRequesters.getOrNull(focusedIndex)?.requestFocus()
                            }
                        }
                    }

                    LaunchedEffect(state.value.code, keyboardManager) {
                        val allNumbersEntered = state.value.code.none { it == null }
                        if (allNumbersEntered) {
                            focusRequesters.forEach {
                                it.freeFocus()
                            }
                            focusManager.clearFocus()
                            keyboardManager?.hide()
                        }
                    }


                    OtpScreen(
                        state = state.value,
                        onAction = { action ->
                            when (action) {
                                is OtpAction.OnEnterNumber -> {
                                    if (action.number != null) {
                                        focusRequesters[action.index].freeFocus()
                                    }
                                }

                                else -> Unit
                            }
                            viewModel.onAction(action)
                        },
                        focusRequesters = focusRequesters,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }
}