package com.paveltinnik.plotcreator.presentation.ui.content

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.paveltinnik.plotcreator.domain.model.SignalPreset
import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.presentation.plot.PlotComponent
import com.paveltinnik.plotcreator.presentation.ui.theme.Green
import com.paveltinnik.plotcreator.presentation.ui.theme.Red
import com.paveltinnik.plotcreator.presentation.ui.theme.Yellow
import java.io.File
import java.io.FileOutputStream
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.core.graphics.createBitmap

private const val ARRAY_SIZE_FACTOR = 1000
private val WAVE_COLORS = listOf(Yellow, Green, Red)

@Composable
fun Plot(
    modifier: Modifier = Modifier,
    component: PlotComponent,
) {
    val state by component.model.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val animatedPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "phase"
    )
    val phase = if (state.isAnimated) animatedPhase else 0f

    Column(modifier = modifier.fillMaxSize()) {
        PlotCanvasBox(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            waves = state.waves,
            phase = phase,
            isSumVisible = state.isSumVisible
        )

        ControlsPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isAnimated = state.isAnimated,
            isSumVisible = state.isSumVisible,
            waves = state.waves,
            onAnimationStatusChanged = component::changeAnimationStatus,
            onSumVisibilityChanged = component::toggleSumVisibility,
            onSineWaveChanged = component::changeSineParameters,
            onPresetSelected = component::applyPreset
        )
    }
}

@Composable
private fun PlotCanvasBox(
    modifier: Modifier = Modifier,
    waves: List<SineWave>,
    phase: Float,
    isSumVisible: Boolean,
) {
    val context = LocalContext.current
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val arraySize = remember { (2 * PI * ARRAY_SIZE_FACTOR).toInt() }
    val paths = remember { List(3) { Path() } }
    val pathSum = remember { Path() }

    val textMeasurer = rememberTextMeasurer()
    val axisTextStyle = remember {
        TextStyle(color = Color.Gray, fontSize = 10.sp)
    }

    Box(modifier = modifier.clipToBounds()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.5f, 5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
        ) {
            val width = size.width
            val height = size.height
            val midY = height / 2f
            val maxAmplitude = height / 4f

            paths.forEach { it.reset() }
            pathSum.reset()

            withTransform({
                translate(left = offsetX, top = offsetY)
                scale(scaleX = zoomScale, scaleY = zoomScale, pivot = center)
            }) {
                drawGridLines(width, height, midY, maxAmplitude, textMeasurer, axisTextStyle)

                if (waves.isNotEmpty()) {
                    computeAndDrawWaves(
                        waves = waves,
                        phase = phase,
                        isSumVisible = isSumVisible,
                        arraySize = arraySize,
                        width = width,
                        midY = midY,
                        maxAmplitude = maxAmplitude,
                        paths = paths,
                        pathSum = pathSum
                    )
                }
            }

            drawPlotLegend(width, textMeasurer)
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (zoomScale != 1f || offsetX != 0f || offsetY != 0f) {
                OutlinedButton(
                    onClick = {
                        zoomScale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    }
                ) {
                    Text(text = "Reset View", fontSize = 12.sp)
                }
            }

            OutlinedButton(
                onClick = {
                    exportAndSharePlot(context, waves, isSumVisible, phase)
                }
            ) {
                Text(text = "Share PNG", fontSize = 12.sp)
            }
        }
    }
}

private fun exportAndSharePlot(
    context: Context,
    waves: List<SineWave>,
    isSumVisible: Boolean,
    phase: Float,
) {
    val width = 1200
    val height = 800
    val bitmap = createBitmap(width, height)
    val canvas = android.graphics.Canvas(bitmap)

    canvas.drawColor(android.graphics.Color.WHITE)

    val composeCanvas = androidx.compose.ui.graphics.Canvas(canvas)
    val drawScope = CanvasDrawScope()
    val textMeasurer = TextMeasurer(
        defaultFontFamilyResolver = createFontFamilyResolver(context),
        defaultDensity = Density(context),
        defaultLayoutDirection = LayoutDirection.Ltr
    )
    val axisTextStyle = TextStyle(color = Color.Gray, fontSize = 12.sp)

    drawScope.draw(
        density = Density(context),
        layoutDirection = LayoutDirection.Ltr,
        canvas = composeCanvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        val midY = height / 2f
        val maxAmplitude = height / 4f
        val arraySize = (2 * PI * ARRAY_SIZE_FACTOR).toInt()
        val paths = List(3) { Path() }
        val pathSum = Path()

        drawGridLines(width.toFloat(), height.toFloat(), midY, maxAmplitude, textMeasurer, axisTextStyle)

        if (waves.isNotEmpty()) {
            computeAndDrawWaves(
                waves = waves,
                phase = phase,
                isSumVisible = isSumVisible,
                arraySize = arraySize,
                width = width.toFloat(),
                midY = midY,
                maxAmplitude = maxAmplitude,
                paths = paths,
                pathSum = pathSum
            )
        }

        drawPlotLegend(width.toFloat(), textMeasurer)
    }

    try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val imageFile = File(cachePath, "plot_export.png")
        val stream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()

        val contentUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )

        if (contentUri != null) {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/png"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Plot Image"))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun DrawScope.drawGridLines(
    width: Float,
    height: Float,
    midY: Float,
    maxAmplitude: Float,
    textMeasurer: TextMeasurer,
    axisTextStyle: TextStyle,
) {
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    val gridColor = Color.LightGray.copy(alpha = 0.5f)

    // Y-Axis Lines (+1.0, +0.5, 0.0, -0.5, -1.0)
    val yLevels = listOf(
        1.0f to "+1.0",
        0.5f to "+0.5",
        0.0f to " 0.0",
        -0.5f to "-0.5",
        -1.0f to "-1.0"
    )

    yLevels.forEach { (level, label) ->
        val y = midY - (level * maxAmplitude)
        drawLine(
            color = if (level == 0.0f) Color.DarkGray else gridColor,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = if (level == 0.0f) 1.5.dp.toPx() else 1.dp.toPx(),
            pathEffect = if (level == 0.0f) null else dashEffect
        )

        val textLayoutResult = textMeasurer.measure(label, axisTextStyle)
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(8f, y - textLayoutResult.size.height / 2f)
        )
    }

    // X-Axis Lines (0 to 6π)
    val xDivisions = 6
    val xLabels = listOf("0", "π", "2π", "3π", "4π", "5π", "6π")
    for (div in 0..xDivisions) {
        val x = (div.toFloat() / xDivisions) * width
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, height),
            strokeWidth = 1.dp.toPx(),
            pathEffect = dashEffect
        )

        xLabels.getOrNull(div)?.let { labelText ->
            val textLayoutResult = textMeasurer.measure(labelText, axisTextStyle)
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    (x - textLayoutResult.size.width / 2f).coerceAtLeast(0f),
                    height - textLayoutResult.size.height - 4f
                )
            )
        }
    }
}

private fun DrawScope.computeAndDrawWaves(
    waves: List<SineWave>,
    phase: Float,
    isSumVisible: Boolean,
    arraySize: Int,
    width: Float,
    midY: Float,
    maxAmplitude: Float,
    paths: List<Path>,
    pathSum: Path,
) {
    for (i in 0 until arraySize) {
        val x = (i.toFloat() / arraySize) * width
        val angle = i.toFloat() / ARRAY_SIZE_FACTOR

        var sumDisp = 0f
        waves.forEachIndexed { index, sineWave ->
            if (sineWave.isVisible) {
                val userPhaseRad = (sineWave.phase * PI / 180.0).toFloat()
                val disp = sin(angle * sineWave.frequency + userPhaseRad + phase) * maxAmplitude * sineWave.amplitude
                sumDisp += disp
                val y = midY - disp

                val path = paths.getOrNull(index)
                if (i == 0) {
                    path?.moveTo(x, y)
                } else {
                    path?.lineTo(x, y)
                }
            }
        }

        val ySum = midY - sumDisp
        if (i == 0) {
            pathSum.moveTo(x, ySum)
        } else {
            pathSum.lineTo(x, ySum)
        }
    }

    // Draw Component Waves
    waves.forEachIndexed { index, sineWave ->
        if (sineWave.isVisible) {
            paths.getOrNull(index)?.let { path ->
                drawPath(
                    path = path,
                    color = WAVE_COLORS.getOrElse(index) { Color.Blue },
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }
    }

    // Draw Sum Wave
    if (isSumVisible) {
        drawPath(pathSum, Color.Black, style = Stroke(width = 3.dp.toPx()))
    }
}

private fun DrawScope.drawPlotLegend(
    width: Float,
    textMeasurer: TextMeasurer,
) {
    val legendX = width - 130.dp.toPx()
    val legendY = 10.dp.toPx()

    drawRect(
        color = Color.White.copy(alpha = 0.85f),
        topLeft = Offset(legendX, legendY),
        size = Size(120.dp.toPx(), 75.dp.toPx())
    )
    drawRect(
        color = Color.Gray,
        topLeft = Offset(legendX, legendY),
        size = Size(120.dp.toPx(), 75.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )

    val legendItems = listOf(
        "Wave 1" to WAVE_COLORS[0],
        "Wave 2" to WAVE_COLORS[1],
        "Wave 3" to WAVE_COLORS[2],
        "Sum Wave" to Color.Black
    )

    legendItems.forEachIndexed { idx, (title, color) ->
        val itemY = legendY + 10.dp.toPx() + (idx * 15.dp.toPx())
        drawLine(
            color = color,
            start = Offset(legendX + 10.dp.toPx(), itemY),
            end = Offset(legendX + 30.dp.toPx(), itemY),
            strokeWidth = if (title == "Sum Wave") 3.dp.toPx() else 2.dp.toPx()
        )
        val legendTextResult = textMeasurer.measure(
            title,
            TextStyle(color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
        )
        drawText(
            textLayoutResult = legendTextResult,
            topLeft = Offset(legendX + 35.dp.toPx(), itemY - legendTextResult.size.height / 2f)
        )
    }
}

@Composable
private fun ControlsPanel(
    modifier: Modifier = Modifier,
    isAnimated: Boolean,
    isSumVisible: Boolean,
    waves: List<SineWave>,
    onAnimationStatusChanged: (Boolean) -> Unit,
    onSumVisibilityChanged: (Boolean) -> Unit,
    onSineWaveChanged: (SineWave) -> Unit,
    onPresetSelected: (SignalPreset) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Animation",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isAnimated,
                onCheckedChange = onAnimationStatusChanged
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Show Sum Wave (Black)",
                style = MaterialTheme.typography.titleMedium
            )
            Switch(
                checked = isSumVisible,
                onCheckedChange = onSumVisibilityChanged
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Signal Presets",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SignalPreset.entries.forEach { preset ->
                AssistChip(
                    onClick = { onPresetSelected(preset) },
                    label = { Text(text = preset.title, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        waves.forEachIndexed { index, sineWave ->
            WaveControlCard(
                sineWave = sineWave,
                color = WAVE_COLORS.getOrElse(index) { Color.Gray },
                onSineWaveChanged = onSineWaveChanged
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun WaveControlCard(
    sineWave: SineWave,
    color: Color,
    onSineWaveChanged: (SineWave) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(color, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Wave ${sineWave.id}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Visible",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Switch(
                        checked = sineWave.isVisible,
                        onCheckedChange = { onSineWaveChanged(sineWave.copy(isVisible = it)) }
                    )
                }
            }

            if (sineWave.isVisible) {
                Spacer(modifier = Modifier.height(8.dp))

                // Amplitude Slider
                Text(
                    text = "Amplitude: ${"%.2f".format(sineWave.amplitude)}",
                    fontSize = 12.sp
                )
                Slider(
                    value = sineWave.amplitude,
                    valueRange = 0f..2f,
                    onValueChange = { onSineWaveChanged(sineWave.copy(amplitude = it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Frequency Slider
                Text(
                    text = "Frequency: ${"%.1f".format(sineWave.frequency)} Hz",
                    fontSize = 12.sp
                )
                Slider(
                    value = sineWave.frequency,
                    valueRange = 0.1f..5f,
                    onValueChange = { onSineWaveChanged(sineWave.copy(frequency = it)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Phase Slider
                Text(
                    text = "Phase: ${sineWave.phase.roundToInt()}°",
                    fontSize = 12.sp
                )
                Slider(
                    value = sineWave.phase,
                    valueRange = -180f..180f,
                    onValueChange = { onSineWaveChanged(sineWave.copy(phase = it)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
