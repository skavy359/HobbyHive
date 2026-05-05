package com.example.hobbyhive.util

import android.Manifest
import android.os.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermission(onResult: (Boolean) -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS) { granted ->
            onResult(granted)
        }
        var showRationale by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) {
                if (permissionState.status.shouldShowRationale) {
                    showRationale = true
                } else {
                    permissionState.launchPermissionRequest()
                }
            }
        }

        if (showRationale) {
            AlertDialog(
                onDismissRequest = { showRationale = false },
                title = { Text("Notification Permission") },
                text = { Text("HobbyHive needs notification permission to send you hobby practice reminders.") },
                confirmButton = {
                    TextButton(onClick = {
                        showRationale = false
                        permissionState.launchPermissionRequest()
                    }) { Text("Allow") }
                },
                dismissButton = {
                    TextButton(onClick = { showRationale = false }) { Text("Deny") }
                }
            )
        }
    }
}
