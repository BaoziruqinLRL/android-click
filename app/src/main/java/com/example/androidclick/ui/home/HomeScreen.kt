package com.example.androidclick.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidclick.R
import com.example.androidclick.domain.model.ClickState
import com.example.androidclick.util.NotificationPermissionRequester
import com.example.androidclick.util.PermissionChecker

@Composable
fun HomeScreen(
    onCreateScript: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = context as? android.app.Activity
    val clickState by viewModel.clickServiceState.collectAsStateWithLifecycle()
    val form = viewModel.form
    val isClicking = clickState.state == ClickState.Running ||
        clickState.state == ClickState.Paused
    var startError by remember { mutableStateOf<String?>(null) }

    val isAccessibilityEnabled = viewModel.isAccessibilityEnabled

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshAccessibility(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        viewModel.refreshAccessibility(context)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // P2-02: 首次进入时请求通知权限
    DisposableEffect(Unit) {
        activity?.let { NotificationPermissionRequester.request(it) }
        onDispose { }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.titleLarge
        )

        PermissionCard(
            permissionState = viewModel.permissionState,
            onOpenAccessibility = { viewModel.openAccessibilitySettings(context) },
            onOpenOverlay = { activity?.let { PermissionChecker.requestOverlayPermission(it) } },
            onOpenNotification = { activity?.let { NotificationPermissionRequester.request(it) } }
        )

        // P2-07: 悬浮窗显示/隐藏开关
        FloatingBarToggle(
            showFloatingBar = viewModel.showFloatingBar,
            onToggle = viewModel::toggleFloatingBar
        )

        Text(
            text = stringResource(R.string.debug_section_title),
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = form.x,
                onValueChange = viewModel::updateX,
                label = { Text("X") },
                modifier = Modifier.weight(1f),
                enabled = !isClicking,
                singleLine = true
            )
            OutlinedTextField(
                value = form.y,
                onValueChange = viewModel::updateY,
                label = { Text("Y") },
                modifier = Modifier.weight(1f),
                enabled = !isClicking,
                singleLine = true
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = form.intervalMs,
                onValueChange = viewModel::updateInterval,
                label = { Text(stringResource(R.string.interval_ms)) },
                modifier = Modifier.weight(1f),
                enabled = !isClicking,
                singleLine = true
            )
            OutlinedTextField(
                value = form.repeatCount,
                onValueChange = viewModel::updateRepeatCount,
                label = { Text(stringResource(R.string.repeat_count)) },
                modifier = Modifier.weight(1f),
                enabled = !isClicking,
                singleLine = true
            )
        }

        Text(
            text = stringResource(R.string.repeat_count_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        StatusCard(clickState = clickState)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    startError = viewModel.startClicking(context)
                },
                modifier = Modifier.weight(1f),
                enabled = isAccessibilityEnabled && !isClicking
            ) {
                Text(stringResource(R.string.start_clicking))
            }
            OutlinedButton(
                onClick = { viewModel.stopClicking(context) },
                modifier = Modifier.weight(1f),
                enabled = isClicking
            ) {
                Text(stringResource(R.string.stop_clicking))
            }
        }

        startError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedButton(
            onClick = onCreateScript,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.create_script))
        }
    }
}

@Composable
private fun StatusCard(clickState: com.example.androidclick.service.ClickServiceState) {
    val statusText = when (clickState.state) {
        ClickState.Idle -> stringResource(R.string.state_idle)
        ClickState.Running -> stringResource(R.string.state_running)
        ClickState.Paused -> stringResource(R.string.state_paused)
        ClickState.Stopped -> stringResource(R.string.state_stopped)
    }
    val countText = if (clickState.totalCount < 0) {
        "${clickState.currentCount} / ∞"
    } else {
        "${clickState.currentCount} / ${clickState.totalCount}"
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = stringResource(R.string.current_status, statusText))
            Text(text = stringResource(R.string.click_count, countText))
            clickState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FloatingBarToggle(
    showFloatingBar: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "显示悬浮控制条",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "开启后在其它应用上方显示连点控制按钮",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = showFloatingBar,
                onCheckedChange = onToggle
            )
        }
    }
}
