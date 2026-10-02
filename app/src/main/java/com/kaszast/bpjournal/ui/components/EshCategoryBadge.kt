package com.kaszast.bpjournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.model.BloodPressureCategory
import com.kaszast.bpjournal.ui.theme.CatGrade1
import com.kaszast.bpjournal.ui.theme.CatGrade2
import com.kaszast.bpjournal.ui.theme.CatGrade3
import com.kaszast.bpjournal.ui.theme.CatHighNormal
import com.kaszast.bpjournal.ui.theme.CatIsolated
import com.kaszast.bpjournal.ui.theme.CatNormal
import com.kaszast.bpjournal.ui.theme.CatOptimal

@Composable
fun EshCategoryBadge(
    category: BloodPressureCategory,
    modifier: Modifier = Modifier
) {
    val (bgColor, labelRes, isAlert) = when (category) {
        BloodPressureCategory.OPTIMAL -> Triple(CatOptimal, R.string.category_optimal, false)
        BloodPressureCategory.NORMAL -> Triple(CatNormal, R.string.category_normal, false)
        BloodPressureCategory.HIGH_NORMAL -> Triple(CatHighNormal, R.string.category_high_normal, true)
        BloodPressureCategory.GRADE_1_HYPERTENSION -> Triple(CatGrade1, R.string.category_grade1, true)
        BloodPressureCategory.GRADE_2_HYPERTENSION -> Triple(CatGrade2, R.string.category_grade2, true)
        BloodPressureCategory.GRADE_3_HYPERTENSION -> Triple(CatGrade3, R.string.category_grade3, true)
        BloodPressureCategory.ISOLATED_SYSTOLIC -> Triple(CatIsolated, R.string.category_isolated_systolic, true)
    }

    Row(
        modifier = modifier
            .background(
                color = bgColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isAlert) Icons.Default.Warning else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = bgColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = labelRes).uppercase(),
            color = bgColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
    }
}
