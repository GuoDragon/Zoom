package com.example.zoom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue
import com.example.zoom.ui.theme.ZoomGray
import com.example.zoom.ui.theme.ZoomTextPrimary
import com.example.zoom.ui.theme.ZoomTextSecondary

@Composable
fun ZoomActionPageTopBar(
    title: String,
    onCancelClick: () -> Unit,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.width(88.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    TextButton(onClick = onCancelClick) {
                        Text(text = "Cancel", color = ZoomBlue, fontSize = 16.sp)
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                }

                Box(
                    modifier = Modifier.width(88.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    if (actionText != null) {
                        TextButton(onClick = onActionClick) {
                            Text(
                                text = actionText,
                                color = ZoomBlue,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFE7EBF0))
        }
    }
}

@Composable
fun ZoomSettingsSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = ZoomTextSecondary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
    )
}

@Composable
fun ZoomSettingSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, color = ZoomTextPrimary)
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = ZoomTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun ZoomSettingValueRow(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    showChevron: Boolean = true
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = ZoomTextPrimary
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = ZoomTextSecondary
            )
            if (showChevron) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color(0xFFB6BFCA),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(14.dp)
                )
            }
        }
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = ZoomTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ZoomInsetDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp),
        thickness = 0.6.dp,
        color = Color(0xFFE7EBF0)
    )
}

@Composable
fun ZoomPrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = ZoomBlue,
            disabledContainerColor = Color(0xFFE7EAF0)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 4.dp),
            fontSize = 18.sp
        )
    }
}

@Composable
fun ZoomPageSurface(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier.background(ZoomGray),
        content = content
    )
}

@Composable
fun KeyboardKey(
    label: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick)
            .background(Color.White, RoundedCornerShape(10.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, fontSize = 18.sp, color = ZoomTextPrimary)
    }
}
