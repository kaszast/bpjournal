package com.kaszast.bpjournal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.data.AggregatedAverage
import com.kaszast.bpjournal.ui.theme.CategoryNormal
import com.kaszast.bpjournal.ui.theme.SlatePrimary
import com.kaszast.bpjournal.ui.theme.TealSecondary

@Composable
fun BloodPressureChart(
    dataPoints: List<AggregatedAverage>,
    modifier: Modifier = Modifier
) {
    if (dataPoints.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nincs elegendő adat a grafikonhoz",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Jelmagyarázat (Legend)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ChartLegendItem(color = SlatePrimary, label = "Szisztolés (átlag)")
            Spacer(modifier = Modifier.width(16.dp))
            ChartLegendItem(color = TealSecondary, label = "Diasztolés (átlag)")
            Spacer(modifier = Modifier.width(16.dp))
            ChartLegendItem(color = Color(0xFFDC2626), label = "Pulzus")
        }

        Spacer(modifier = Modifier.height(12.dp))

        val minSys = 50f
        val maxSys = 200f

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height

            fun yForValue(v: Float): Float {
                val clamped = v.coerceIn(minSys, maxSys)
                return height - ((clamped - minSys) / (maxSys - minSys)) * height
            }

            // Normál célzóna zöldesszürke háttérsávja (80-120 Hgmm)
            val y120 = yForValue(120f)
            val y80 = yForValue(80f)
            drawRect(
                color = CategoryNormal.copy(alpha = 0.08f),
                topLeft = Offset(0f, y120),
                size = Size(width, y80 - y120)
            )

            // Vízszintes referencia rácsvonalak (80, 120, 160 Hgmm)
            val gridValues = listOf(80f, 120f, 160f)
            for (gv in gridValues) {
                val y = yForValue(gv)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1f
                )
            }

            if (dataPoints.size == 1) {
                // Egyetlen pont esetén körök kirajzolása
                val dp = dataPoints.first()
                val cx = width / 2f
                val cySys = yForValue(dp.averageSystolic.toFloat())
                val cyDia = yForValue(dp.averageDiastolic.toFloat())
                val cyPulse = yForValue(dp.averagePulse.toFloat())

                drawCircle(color = SlatePrimary, radius = 7f, center = Offset(cx, cySys))
                drawCircle(color = TealSecondary, radius = 7f, center = Offset(cx, cyDia))
                drawCircle(color = Color(0xFFDC2626), radius = 5f, center = Offset(cx, cyPulse))
                return@Canvas
            }

            val stepX = width / (dataPoints.size - 1).coerceAtLeast(1)

            val sysPath = Path()
            val diaPath = Path()
            val pulsePath = Path()

            dataPoints.forEachIndexed { index, dp ->
                val x = index * stepX
                val ySys = yForValue(dp.averageSystolic.toFloat())
                val yDia = yForValue(dp.averageDiastolic.toFloat())
                val yPulse = yForValue(dp.averagePulse.toFloat())

                if (index == 0) {
                    sysPath.moveTo(x, ySys)
                    diaPath.moveTo(x, yDia)
                    pulsePath.moveTo(x, yPulse)
                } else {
                    sysPath.lineTo(x, ySys)
                    diaPath.lineTo(x, yDia)
                    pulsePath.lineTo(x, yPulse)
                }
            }

            // Vonalak rajzolása
            drawPath(path = sysPath, color = SlatePrimary, style = Stroke(width = 3.5f))
            drawPath(path = diaPath, color = TealSecondary, style = Stroke(width = 3.5f))
            drawPath(path = pulsePath, color = Color(0xFFDC2626).copy(alpha = 0.7f), style = Stroke(width = 2f))

            // Pontok rajzolása a töréspontokon
            dataPoints.forEachIndexed { index, dp ->
                val x = index * stepX
                val ySys = yForValue(dp.averageSystolic.toFloat())
                val yDia = yForValue(dp.averageDiastolic.toFloat())

                drawCircle(color = Color.White, radius = 5f, center = Offset(x, ySys))
                drawCircle(color = SlatePrimary, radius = 4f, center = Offset(x, ySys))

                drawCircle(color = Color.White, radius = 5f, center = Offset(x, yDia))
                drawCircle(color = TealSecondary, radius = 4f, center = Offset(x, yDia))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Alsó időszaki címkék (első és utolsó pont)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = dataPoints.first().periodLabel,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
            if (dataPoints.size > 1) {
                Text(
                    text = dataPoints.last().periodLabel,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ChartLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
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
