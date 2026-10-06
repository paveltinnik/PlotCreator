package com.paveltinnik.plotcreator.presentation.ui.content

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.paveltinnik.plotcreator.presentation.plot.PlotComponent
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun Plot(
    modifier: Modifier = Modifier,
    component: PlotComponent,
) {
    val state by component.model.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val animatedPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "phase"
    )
    val phase = if (state.isAnimated) animatedPhase else 0f

    val arraySize = remember { (2 * PI * 1000).toInt() }
    val paths = remember { List(3) { Path() } }
    val pathSum = remember { Path() }
    val waveColors = listOf(Color.Yellow, Color.Green, Color.Red)
    val phaseStep = (2.0 * PI / 3.0).toFloat()

    Column(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val width = size.width
            val height = size.height
            val midY = height / 2
            val maxAmplitude = height / 4

            paths.forEach { it.reset() }
            pathSum.reset()

            if (state.waves.isNotEmpty()) {
                for (i in 0 until arraySize) {
                    val x = (i.toFloat() / arraySize) * width
                    val angle = i.toFloat() / 1000f

                    var sumDisp = 0f
                    state.waves.forEachIndexed { index, sineWave ->
                        val phaseShift = index * phaseStep
                        val disp = sin(angle - phaseShift + phase) * maxAmplitude * sineWave.amplitude
                        sumDisp += disp
                        val y = midY - disp

                        if (i == 0) {
                            paths.getOrNull(index)?.moveTo(x, y)
                        } else {
                            paths.getOrNull(index)?.lineTo(x, y)
                        }
                    }

                    val ySum = midY - sumDisp
                    if (i == 0) {
                        pathSum.moveTo(x, ySum)
                    } else {
                        pathSum.lineTo(x, ySum)
                    }
                }

                state.waves.forEachIndexed { index, _ ->
                    paths.getOrNull(index)?.let { path ->
                        drawPath(
                            path = path,
                            color = waveColors.getOrElse(index) { Color.Blue },
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }

            drawPath(pathSum, Color.Black, style = Stroke(width = 3.dp.toPx()))

            drawLine(
                color = Color.DarkGray,
                start = Offset(0f, midY),
                end = Offset(width, midY),
                strokeWidth = 1.dp.toPx()
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Animation")
                Switch(
                    checked = state.isAnimated,
                    onCheckedChange = { component.changeAnimationStatus(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            state.waves.forEach { sineWave ->
                Text(text = "Amplitude of ${sineWave.id}")
                Slider(
                    value = sineWave.amplitude,
                    onValueChange = {
                        component.changeSineParameters(sineWave.copy(amplitude = it))
                        Log.d("PlotContent", it.toString())
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
