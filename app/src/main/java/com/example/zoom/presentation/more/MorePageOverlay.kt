package com.example.zoom.presentation.more

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zoom.ui.theme.ZoomBlue

@Composable
fun MorePageOverlay(onDismiss: () -> Unit) {
    var uiState by remember { mutableStateOf<MoreUiState?>(null) }
    val dismissInteraction = remember { MutableInteractionSource() }

    val view = remember {
        object : MoreContract.View {
            override fun showContent(content: MoreUiState) {
                uiState = content
            }
        }
    }

    LaunchedEffect(Unit) {
        MorePresenter(view).loadData()
    }

    uiState?.let { screenState ->
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.06f))
                    .clickable(
                        interactionSource = dismissInteraction,
                        indication = null,
                        onClick = onDismiss
                    )
            )

            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp, bottom = 20.dp)
                ) {
                    PromoBanner(
                        badge = screenState.promoBadge,
                        promoText = screenState.promoText,
                        linkText = screenState.promoLinkText
                    )
                    Text(
                        text = screenState.reorderText,
                        color = ZoomBlue,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 18.dp, end = 20.dp, bottom = 10.dp)
                    )
                    ShortcutGrid(actions = screenState.shortcutActions)
                }
            }
        }
    }
}

@Composable
private fun PromoBanner(
    badge: String,
    promoText: String,
    linkText: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF8B4DFF),
                            Color(0xFF68D9D6),
                            Color(0xFFF3DEFF),
                            Color(0xFF99D8FF)
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column {
                Text(
                    text = badge,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFF7B39F2), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = buildAnnotatedString {
                        append(promoText)
                        append(" ")
                        withStyle(
                            style = SpanStyle(
                                color = ZoomBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(linkText)
                        }
                    },
                    color = Color(0xFF243243),
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ShortcutGrid(actions: List<MoreShortcutUiState>) {
    HorizontalDivider(color = Color(0xFFE8EBF0))
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 8.dp),
        userScrollEnabled = false
    ) {
        items(actions) { action ->
            ShortcutItem(action = action)
        }
    }
}

@Composable
private fun ShortcutItem(action: MoreShortcutUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {})
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Color(0xFFF8F9FB), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.label,
                tint = Color(0xFF6C7788),
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = action.label,
            fontSize = 12.sp,
            color = Color(0xFF616B7A),
            textAlign = TextAlign.Center
        )
    }
}
