package com.kaszast.bpjournal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.ui.theme.AccentMint

@Composable
fun PulseRing(
    pulse: Int,
    modifier: Modifier = Modifier,
    tintColor: Color = AccentMint
) {
    val progress = ((pulse - 40f) / 140f).coerceIn(0.1f, 1f)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(76.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(70.dp)) {
                val strokeWidth = 7.dp.toPx()

                // Háttér gyűrű
                drawArc(
                    color = tintColor.copy(alpha = 0.15f),
                    startAngle = 140f,
                    sweepAngle = 260f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Aktív progresszív gyűrű
                drawArc(
                    color = tintColor,
                    startAngle = 140f,
                    sweepAngle = 260f * progress,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-1).dp),
                modifier = Modifier.padding(bottom = 2.dp)
            ) {
                Text(
                    text = "$pulse",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
                )
                Text(
                    text = "BPM",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        val statusText = when {
            pulse in 60..100 -> stringResource(R.string.pulse_normal)
            pulse < 60 -> stringResource(R.string.pulse_low)
            else -> stringResource(R.string.pulse_high)
        }
        Text(
            text = statusText,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
