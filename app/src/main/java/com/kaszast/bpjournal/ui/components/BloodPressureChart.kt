package com.kaszast.bpjournal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.kaszast.bpjournal.R
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.data.AggregatedAverage
import com.kaszast.bpjournal.ui.theme.DarkDiastolic
import com.kaszast.bpjournal.ui.theme.DarkSystolic
import com.kaszast.bpjournal.ui.theme.LightDiastolic
import com.kaszast.bpjournal.ui.theme.LightSystolic

@Composable
fun BloodPressureChart(
    dataPoints: List<AggregatedAverage>,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val sysColor = if (isDark) DarkSystolic else LightSystolic
    val diaColor = if (isDark) DarkDiastolic else LightDiastolic
    val targetZoneColor = if (isDark) Color(0xFF0D9488).copy(alpha = 0.15f) else Color(0xFF10B981).copy(alpha = 0.12f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDark) 0.dp else 1.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // 1. Sor: Cím és időszak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.chart_title),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(R.string.chart_past_7_days),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Sor: Jelmagyarázat külön sorban
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartLegend(color = sysColor, label = stringResource(R.string.systolic_label))
                Spacer(modifier = Modifier.width(18.dp))
                ChartLegend(color = diaColor, label = stringResource(R.string.diastolic_label))
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (dataPoints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.chart_no_data),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
                return@Column
            }

            // Canvas grafikon
            val minScale = 50f
            val maxScale = 160f
            val surfaceColor = MaterialTheme.colorScheme.surface

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                val width = size.width
                val height = size.height

                fun yForValue(v: Float): Float {
                    val clamped = v.coerceIn(minScale, maxScale)
                    return height - ((clamped - minScale) / (maxScale - minScale)) * height
                }

                // Célzóna háttérkitöltése (80 és 120 Hgmm között)
                val y120 = yForValue(120f)
                val y80 = yForValue(80f)
                drawRect(
                    color = targetZoneColor,
                    topLeft = Offset(0f, y120),
                    size = Size(width, y80 - y120)
                )

                // Vízszintes referencia rácsvonalak (60, 80, 100, 120, 140 Hgmm)
                val gridValues = listOf(60f, 80f, 100f, 120f, 140f)
                for (gv in gridValues) {
                    val y = yForValue(gv)
                    drawLine(
                        color = Color.Gray.copy(alpha = if (isDark) 0.2f else 0.15f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                if (dataPoints.size == 1) {
                    val dp = dataPoints.first()
                    val cx = width / 2f
                    val cySys = yForValue(dp.averageSystolic.toFloat())
                    val cyDia = yForValue(dp.averageDiastolic.toFloat())

                    drawCircle(color = sysColor, radius = 6f, center = Offset(cx, cySys))
                    drawCircle(color = diaColor, radius = 6f, center = Offset(cx, cyDia))
                    return@Canvas
                }

                val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

                val sysPath = Path()
                val diaPath = Path()

                dataPoints.forEachIndexed { index, dp ->
                    val x = index * stepX
                    val ySys = yForValue(dp.averageSystolic.toFloat())
                    val yDia = yForValue(dp.averageDiastolic.toFloat())

                    if (index == 0) {
                        sysPath.moveTo(x, ySys)
                        diaPath.moveTo(x, yDia)
                    } else {
                        // Sima görbület pontok között
                        val prevX = (index - 1) * stepX
                        val prevYSys = yForValue(dataPoints[index - 1].averageSystolic.toFloat())
                        val prevYDia = yForValue(dataPoints[index - 1].averageDiastolic.toFloat())

                        val midX = (prevX + x) / 2f
                        sysPath.cubicTo(midX, prevYSys, midX, ySys, x, ySys)
                        diaPath.cubicTo(midX, prevYDia, midX, yDia, x, yDia)
                    }
                }

                // Vonalak rajzolása
                drawPath(path = sysPath, color = sysColor, style = Stroke(width = 3.5f))
                drawPath(path = diaPath, color = diaColor, style = Stroke(width = 3.5f))

                // Adatpontok körökkel
                dataPoints.forEachIndexed { index, dp ->
                    val x = index * stepX
                    val ySys = yForValue(dp.averageSystolic.toFloat())
                    val yDia = yForValue(dp.averageDiastolic.toFloat())

                    drawCircle(color = surfaceColor, radius = 5.5f, center = Offset(x, ySys))
                    drawCircle(color = sysColor, radius = 4f, center = Offset(x, ySys))

                    drawCircle(color = surfaceColor, radius = 5.5f, center = Offset(x, yDia))
                    drawCircle(color = diaColor, radius = 4f, center = Offset(x, yDia))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Célzóna felirat és alsó dátumok
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dataPoints.first().shortLabel.ifEmpty { dataPoints.first().periodLabel },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.chart_target_zone, stringResource(R.string.unit_mmhg)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isDark) DarkDiastolic else LightDiastolic
                )
                if (dataPoints.size > 1) {
                    Text(
                        text = dataPoints.last().shortLabel.ifEmpty { dataPoints.last().periodLabel },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
