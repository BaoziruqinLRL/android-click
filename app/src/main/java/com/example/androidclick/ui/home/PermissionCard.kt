package com.example.androidclick.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.androidclick.R
import com.example.androidclick.util.PermissionState

@Composable
fun PermissionCard(
    permissionState: PermissionState,
    onOpenAccessibility: () -> Unit,
    onOpenOverlay: () -> Unit,
    onOpenNotification: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.permission_status),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (permissionState.allGranted) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = stringResource(R.string.permission_all_ready),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                PermissionRow(
                    granted = permissionState.accessibility,
                    enabledText = stringResource(R.string.accessibility_enabled),
                    disabledText = stringResource(R.string.accessibility_disabled),
                    actionLabel = stringResource(R.string.open_accessibility_settings),
                    onAction = onOpenAccessibility
                )
                PermissionRow(
                    granted = permissionState.overlay,
                    enabledText = stringResource(R.string.overlay_permission_enabled),
                    disabledText = stringResource(R.string.overlay_permission_disabled),
                    actionLabel = stringResource(R.string.open_overlay_permission),
                    onAction = onOpenOverlay
                )
                PermissionRow(
                    granted = permissionState.notification,
                    enabledText = stringResource(R.string.notification_permission_enabled),
                    disabledText = stringResource(R.string.notification_permission_disabled),
                    actionLabel = stringResource(R.string.open_notification_permission),
                    onAction = onOpenNotification
                )
            }
        }
    }
}

@Composable
private fun PermissionRow(
    granted: Boolean,
    enabledText: String,
    disabledText: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (granted) Icons.Filled.CheckCircle else Icons.Filled.Warning,
            contentDescription = null,
            tint = if (granted) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = if (granted) enabledText else disabledText,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        if (!granted) {
            TextButton(onClick = onAction) {
                Text(actionLabel)
            }
        }
    }
}
