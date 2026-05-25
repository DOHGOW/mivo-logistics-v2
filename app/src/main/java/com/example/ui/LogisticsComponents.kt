package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.85f,
    borderAlpha: Float = 0.12f,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .border(
                width = 1.dp,
                color = DarkText.copy(alpha = borderAlpha),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = alpha)
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            content()
        }
    }
}

@Composable
fun GradientActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = if (enabled) {
                        listOf(PrimaryOrange, DeepAmber)
                    } else {
                        listOf(LightGray, GrayText)
                    }
                )
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MivoSectionHeader(
    title: String,
    badgeText: String? = null,
    onViewAll: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                fontSize = 19.sp,
                fontWeight = FontWeight.W800,
                color = DarkText
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(PrimaryOrange.copy(alpha = 0.15f), CircleShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = PrimaryOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        if (onViewAll != null) {
            Text(
                text = "See All",
                color = PrimaryOrange,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onViewAll() }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun SimulatedInteractiveMap(
    modifier: Modifier = Modifier,
    progress: Float = 0.0f,
    activeBooking: Booking? = null,
    showNearbyVehicles: Boolean = true
) {
    val vectorPainter = rememberVectorPainter(Icons.Default.LocalShipping)
    val startIconPainter = rememberVectorPainter(Icons.Default.Warehouse)
    val destIconPainter = rememberVectorPainter(Icons.Default.Flag)

    // Pulse animation for destination marker
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_anim"
    )

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE2EBF0))
    ) {
        try {
            val width = size.width
            val height = size.height

            if (width > 0f && height > 0f) {
                val safeProgress = if (progress.isNaN() || progress.isInfinite()) 0.0f else progress.coerceIn(0.0f, 1.0f)

                // Define route landmarks explicitly
                val startOffset = Offset(width * 0.18f, height * 0.72f)
                val bendOffset1 = Offset(width * 0.42f, height * 0.48f)
                val bendOffset2 = Offset(width * 0.60f, height * 0.68f)
                val endOffset = Offset(width * 0.82f, height * 0.24f)

                // Draw basic map grid lines (highway lanes simulation)
                for (i in 1..4) {
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.45f),
                        start = Offset(0f, height * (i * 0.22f)),
                        end = Offset(width, height * (i * 0.22f)),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.45f),
                        start = Offset(width * (i * 0.20f), 0f),
                        end = Offset(width * (i * 0.20f), height),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Procedural highway curves representation
                val routePath = Path().apply {
                    moveTo(startOffset.x, startOffset.y)
                    cubicTo(bendOffset1.x, bendOffset1.y, bendOffset2.x, bendOffset2.y, endOffset.x, endOffset.y)
                }

                // Draw main highway route trunk
                drawPath(
                    path = routePath,
                    color = Color.White,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                drawPath(
                    path = routePath,
                    color = PrimaryOrange.copy(alpha = 0.75f),
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                )

                // Draw the visual completion trail
                if (safeProgress > 0.0f) {
                    try {
                        val progressPath = Path()
                        val measure = PathMeasure()
                        measure.setPath(routePath, false)
                        val sublength = measure.length * safeProgress
                        if (sublength > 0f && sublength <= measure.length) {
                            measure.getSegment(0f, sublength, progressPath, true)
                            drawPath(
                                path = progressPath,
                                color = SuccessGreen,
                                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    } catch (e: Exception) {
                        // Suppress path segment exceptions in pathological frames
                    }
                }

                // Draw pickup depot radius rings
                drawCircle(
                    color = PrimaryOrange.copy(alpha = 0.15f),
                    center = startOffset,
                    radius = 35.dp.toPx()
                )
                drawCircle(
                    color = PrimaryOrange,
                    center = startOffset,
                    radius = 8.dp.toPx()
                )

                // Draw destination pulsing rings
                val parsedPulse = if (pulseRadius.isNaN() || pulseRadius.isInfinite()) 12f else pulseRadius
                val prAlpha = (1f - (parsedPulse / 28f)).coerceIn(0f, 0.4f)
                val safeAlpha = if (prAlpha.isNaN()) 0f else prAlpha

                drawCircle(
                    color = SuccessGreen.copy(alpha = safeAlpha),
                    center = endOffset,
                    radius = parsedPulse * 2.dp.toPx()
                )
                drawCircle(
                    color = SuccessGreen,
                    center = endOffset,
                    radius = 8.dp.toPx()
                )

                // Vector labels for start & end
                translate(left = startOffset.x - 14.dp.toPx(), top = startOffset.y - 32.dp.toPx()) {
                    with(startIconPainter) {
                        draw(size = Size(28.dp.toPx(), 28.dp.toPx()), colorFilter = ColorFilter.tint(PrimaryOrange))
                    }
                }

                translate(left = endOffset.x - 10.dp.toPx(), top = endOffset.y - 32.dp.toPx()) {
                    with(destIconPainter) {
                        draw(size = Size(26.dp.toPx(), 26.dp.toPx()), colorFilter = ColorFilter.tint(SuccessGreen))
                    }
                }

                // Simulated secondary fleet trucks active (geographic clustering)
                if (showNearbyVehicles) {
                    val activeTruck1 = Offset(width * 0.30f, height * 0.22f)
                    val activeTruck2 = Offset(width * 0.75f, height * 0.80f)
                    val activeTruck3 = Offset(width * 0.52f, height * 0.15f)

                    listOf(activeTruck1, activeTruck2, activeTruck3).forEach { pos ->
                        drawCircle(
                            color = PrimaryOrange.copy(alpha = 0.10f),
                            center = pos,
                            radius = 20.dp.toPx()
                        )
                        drawCircle(
                            color = Color.White,
                            center = pos,
                            radius = 7.dp.toPx()
                        )
                        drawCircle(
                            color = PrimaryOrange,
                            center = pos,
                            radius = 4.dp.toPx()
                        )
                    }
                }

                // Draw current tracked truck moving on the bezier route
                val currentPosition = getOffsetOnCubicBezier(startOffset, bendOffset1, bendOffset2, endOffset, safeProgress)
                
                drawCircle(
                    color = if (safeProgress >= 1.0f) SuccessGreen.copy(alpha = 0.2f) else PrimaryOrange.copy(alpha = 0.25f),
                    center = currentPosition,
                    radius = 24.dp.toPx()
                )

                translate(left = currentPosition.x - 16.dp.toPx(), top = currentPosition.y - 16.dp.toPx()) {
                    with(vectorPainter) {
                        draw(
                            size = Size(32.dp.toPx(), 32.dp.toPx()),
                            colorFilter = ColorFilter.tint(if (safeProgress >= 1.0f) SuccessGreen else PrimaryOrange)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Guard system from Canvas runtime crashes
        }
    }
}

// Cubic Bezier interpolation calculator
private fun getOffsetOnCubicBezier(
    start: Offset,
    control1: Offset,
    control2: Offset,
    end: Offset,
    t: Float
): Offset {
    val safeT = if (t.isNaN() || t.isInfinite()) 0f else t.coerceIn(0f, 1f)
    val mt = 1f - safeT
    val mt2 = mt * mt
    val mt3 = mt2 * mt
    val t2 = safeT * safeT
    val t3 = t2 * safeT

    val x = mt3 * start.x + 3 * mt2 * safeT * control1.x + 3 * mt * t2 * control2.x + t3 * end.x
    val y = mt3 * start.y + 3 * mt2 * safeT * control1.y + 3 * mt * t2 * control2.y + t3 * end.y

    val safeX = if (x.isNaN() || x.isInfinite()) 0f else x
    val safeY = if (y.isNaN() || y.isInfinite()) 0f else y

    return Offset(safeX, safeY)
}

@Composable
fun MivoMilestoneTimeline(
    milestones: List<ShipmentMilestone>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        milestones.forEachIndexed { index, milestone ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Tracking dot and vertical bar segment
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                color = if (milestone.done) SuccessGreen else LightGray.copy(alpha = 0.5f),
                                shape = CircleShape
                            ).border(
                                width = 3.dp,
                                color = if (milestone.done) SuccessGreen.copy(alpha = 0.3f) else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                    if (index < milestones.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(38.dp)
                                .background(
                                    if (milestones[index + 1].done) SuccessGreen else LightGray.copy(alpha = 0.4f)
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = milestone.status.displayName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (milestone.done) DarkText else GrayText
                    )
                    Text(
                        text = milestone.timestamp,
                        fontSize = 13.sp,
                        color = if (milestone.done) PrimaryOrange else LightGray,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
fun SignatureConfirmationCanvas(
    onSignatureCaptured: () -> Unit,
    modifier: Modifier = Modifier
) {
    var points = remember { mutableStateListOf<Offset>() }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recipients Electronic Signature",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                text = "Clear Signature",
                fontSize = 13.sp,
                color = ErrorRed,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { points.clear() }
                    .padding(8.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(1.dp, GrayText.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        points.add(change.position)
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                try {
                    val localPoints = points.toList() // Avoid concurrent modifications during clears in draw frames
                    if (localPoints.isNotEmpty()) {
                        val path = Path()
                        val first = localPoints[0]
                        path.moveTo(first.x, first.y)
                        for (i in 1 until localPoints.size) {
                            val pt = localPoints[i]
                            path.lineTo(pt.x, pt.y)
                        }
                        drawPath(
                            path = path,
                            color = DarkText,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    } else {
                        // Draw a subtle baseline signature indicator
                        drawLine(
                            color = LightGray.copy(alpha = 0.5f),
                            start = Offset(40f, size.height - 30f),
                            end = Offset(size.width - 40f, size.height - 30f),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                        )
                    }
                } catch (e: Exception) {
                    // Suppress drawing race conditions
                }
            }
            if (points.isEmpty()) {
                Text(
                    text = "Write full legal name here directly",
                    color = LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}

private data class QuadData<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun FirebaseStatusChip(modifier: Modifier = Modifier) {
    val isLiveFirebase by com.example.firebase.FirebaseSyncManager.isLiveFirebase.collectAsState()
    val syncStatus by com.example.firebase.FirebaseSyncManager.syncStatus.collectAsState()

    val data = if (isLiveFirebase) {
        when (val s = syncStatus) {
            is com.example.firebase.FirebaseSyncManager.SyncStatus.Syncing -> {
                QuadData(
                    Color(0xFFFFFAF0),
                    PrimaryOrange,
                    "Syncing...",
                    Icons.Default.Sync
                )
            }
            is com.example.firebase.FirebaseSyncManager.SyncStatus.Error -> {
                QuadData(
                    Color(0xFFFFF0F0),
                    ErrorRed,
                    "Sync Failed",
                    Icons.Default.CloudOff
                )
            }
            else -> {
                QuadData(
                    Color(0xFFF0FFF4),
                    SuccessGreen,
                    "Firebase Connected",
                    Icons.Default.CloudDone
                )
            }
        }
    } else {
        QuadData(
            Color(0xFFF7FAFC),
            GrayText,
            "Local Sandbox Mode",
            Icons.Default.CloudOff
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(data.first)
            .border(1.dp, data.second.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .clickable {
                // Clicking forces manual diagnostic test log
                android.util.Log.d("FirebaseSync", "Firebase live status: $isLiveFirebase, Sync status: $syncStatus")
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (syncStatus is com.example.firebase.FirebaseSyncManager.SyncStatus.Syncing) {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                color = data.second,
                strokeWidth = 1.5.dp
            )
        } else {
            Icon(
                imageVector = data.fourth,
                contentDescription = data.third,
                tint = data.second,
                modifier = Modifier.size(13.dp)
            )
        }
        Text(
            text = data.third,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = data.second
        )
    }
}

