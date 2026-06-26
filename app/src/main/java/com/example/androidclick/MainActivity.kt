package com.example.androidclick

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.androidclick.ui.navigation.ClickerNavGraph
import com.example.androidclick.ui.overlay.FloatingControlBridge
import com.example.androidclick.ui.theme.ClickerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FloatingControlBridge.initialize(
            context = applicationContext,
            lifecycleOwner = this,
            savedStateRegistryOwner = this
        )

        setContent {
            ClickerTheme {
                ClickerNavGraph(modifier = Modifier.fillMaxSize())
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        FloatingControlBridge.resetPosition()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            FloatingControlBridge.destroy()
        }
    }
}
