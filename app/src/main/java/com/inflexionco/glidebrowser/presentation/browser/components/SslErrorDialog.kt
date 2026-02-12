package com.inflexionco.glidebrowser.presentation.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.domain.model.SslErrorInfo
import com.inflexionco.glidebrowser.ui.components.TvButton

/**
 * Dialog to display SSL certificate errors with details
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SslErrorDialog(
    sslError: SslErrorInfo,
    onProceed: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onCancel) {
        Column(
            modifier = modifier
                .width(600.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(32.dp)
        ) {
            // Title
            Text(
                text = "Security Warning",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFE53935) // Red
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error description
            Text(
                text = sslError.errorType.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // URL
            Text(
                text = "URL: ${sslError.url}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Certificate details section
            if (sslError.certificateIssuer != null || sslError.certificateSubject != null) {
                Text(
                    text = "Certificate Details:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                sslError.certificateIssuer?.let {
                    DetailRow("Issued by:", it)
                }

                sslError.certificateSubject?.let {
                    DetailRow("Issued to:", it)
                }

                sslError.certificateValidFrom?.let {
                    DetailRow("Valid from:", it)
                }

                sslError.certificateValidTo?.let {
                    DetailRow("Valid until:", it)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Warning message
            Text(
                text = "Proceeding may put your information at risk. We recommend going back to safety.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TvButton(
                    text = "Go Back (Recommended)",
                    onClick = onCancel,
                    modifier = Modifier.width(280.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                TvButton(
                    text = "Proceed Anyway",
                    onClick = onProceed,
                    modifier = Modifier.width(220.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}