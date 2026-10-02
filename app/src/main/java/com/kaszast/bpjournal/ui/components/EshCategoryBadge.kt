package com.kaszast.bpjournal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kaszast.bpjournal.R
import com.kaszast.bpjournal.model.BloodPressureCategory
import com.kaszast.bpjournal.ui.theme.CategoryGrade1
import com.kaszast.bpjournal.ui.theme.CategoryGrade2
import com.kaszast.bpjournal.ui.theme.CategoryGrade3
import com.kaszast.bpjournal.ui.theme.CategoryHighNormal
import com.kaszast.bpjournal.ui.theme.CategoryIsolated
import com.kaszast.bpjournal.ui.theme.CategoryNormal
import com.kaszast.bpjournal.ui.theme.CategoryOptimal

@Composable
fun EshCategoryBadge(
    category: BloodPressureCategory,
    modifier: Modifier = Modifier
) {
    val (bgColor, labelRes) = when (category) {
        BloodPressureCategory.OPTIMAL -> Pair(CategoryOptimal, R.string.category_optimal)
        BloodPressureCategory.NORMAL -> Pair(CategoryNormal, R.string.category_normal)
        BloodPressureCategory.HIGH_NORMAL -> Pair(CategoryHighNormal, R.string.category_high_normal)
        BloodPressureCategory.GRADE_1_HYPERTENSION -> Pair(CategoryGrade1, R.string.category_grade1)
        BloodPressureCategory.GRADE_2_HYPERTENSION -> Pair(CategoryGrade2, R.string.category_grade2)
        BloodPressureCategory.GRADE_3_HYPERTENSION -> Pair(CategoryGrade3, R.string.category_grade3)
        BloodPressureCategory.ISOLATED_SYSTOLIC -> Pair(CategoryIsolated, R.string.category_isolated_systolic)
    }

    Box(
        modifier = modifier
            .background(
                color = bgColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(id = labelRes),
            color = bgColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
