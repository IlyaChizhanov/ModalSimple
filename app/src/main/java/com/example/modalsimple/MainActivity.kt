@file:OptIn(ExperimentalMaterialApi::class)

package com.example.modalsimple

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Screen()
                }
            }
        }
    }
}


private val _switch: MutableStateFlow<Boolean?> = MutableStateFlow(null)
val switch: StateFlow<Boolean?> get() = _switch.asStateFlow()

private val _singleEvent: MutableSharedFlow<Any> = MutableSharedFlow()
val singleEvent: SharedFlow<Any> get() = _singleEvent.asSharedFlow()

fun click() {
    GlobalScope.launch {
        Log.d("VA-1426", "emit ShowPlaylist")
        _switch.emit(!(_switch.value ?: false))
        _singleEvent.emit(Any())
    }
}

@Composable
fun Screen() {
    val modalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    Log.d(TAG, "Recompose")
    LaunchedEffect(Unit) {
        Log.d(TAG, "LaunchedEffect")
        singleEvent.onEach {
            Log.d(TAG, "onEach")
            try {
                modalBottomSheetState.show()
            } catch (e: Throwable) {
                Log.e(TAG, e.toString())
                throw e
            }
        }.launchIn(this)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Button(onClick = {
            click()
        }) {
            Text(text = "Click")
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            ModalScreen()
        },
        sheetState = modalBottomSheetState,
    ) {}
}

@Composable
fun ModalScreen() {
    val switch by switch.collectAsState()

    switch?.let {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Log.d(TAG, "Switch: $switch")
            if (it) {
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .background(Color.Red)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(1.dp))
}

private const val TAG = "TAG"