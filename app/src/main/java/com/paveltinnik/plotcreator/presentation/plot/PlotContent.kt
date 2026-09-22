package com.paveltinnik.plotcreator.presentation.plot

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun Plot(
    modifier: Modifier = Modifier,
    component: PlotComponent,
) {
    val state by component.model.collectAsState()

    var ampA by remember { mutableFloatStateOf(0.5f) }
    var ampB by remember { mutableFloatStateOf(0.5f) }
    var ampC by remember { mutableFloatStateOf(0.5f) }

//    val sineA = state.waves[0]
//    val sineB = state.waves[1]
//    val sineC = state.waves[2]
//
//    val ampA = state.waves[0].amplitude
//    val ampB = state.waves[1].amplitude
//    val ampC = state.waves[2].amplitude

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "phase"
    )

    val arraySize = remember { (2 * PI * 1000).toInt() }

    val pathA = remember { Path() }
    val pathB = remember { Path() }
    val pathC = remember { Path() }
    val pathSum = remember { Path() }

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

            pathA.reset()
            pathB.reset()
            pathC.reset()
            pathSum.reset()

            val phaseShift = (2.0 * PI / 3.0).toFloat()

            for (i in 0 until arraySize) {
                val x = (i.toFloat() / arraySize) * width
                val angle = i.toFloat() / 1000f

                val dispA = sin(angle + phase) * maxAmplitude * ampA
                val dispB = sin(angle - phaseShift + phase) * maxAmplitude * ampB
                val dispC = sin(angle - 2 * phaseShift + phase) * maxAmplitude * ampC

                val yA = midY - dispA
                val yB = midY - dispB
                val yC = midY - dispC
                val ySum = midY - (dispA + dispB + dispC)

                if (i == 0) {
                    pathA.moveTo(x, yA)
                    pathB.moveTo(x, yB)
                    pathC.moveTo(x, yC)
                    pathSum.moveTo(x, ySum)
                } else {
                    pathA.lineTo(x, yA)
                    pathB.lineTo(x, yB)
                    pathC.lineTo(x, yC)
                    pathSum.lineTo(x, ySum)
                }
            }

            drawPath(pathA, Color.Yellow, style = Stroke(width = 2.dp.toPx()))
            drawPath(pathB, Color.Green, style = Stroke(width = 2.dp.toPx()))
            drawPath(pathC, Color.Red, style = Stroke(width = 2.dp.toPx()))
            drawPath(pathSum, Color.Black, style = Stroke(width = 3.dp.toPx()))

            drawLine(
                color = Color.DarkGray,
                start = Offset(0f, midY),
                end = Offset(width, midY),
                strokeWidth = 1.dp.toPx()
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            state.waves.forEach {sineWave ->
                Text(text = "Amplitude A (Yellow)")
                Slider(
                    value = sineWave.amplitude,
                    onValueChange = { component.changeSineParameters(sineWave.copy(amplitude = it))
                                    Log.d("PlotContent", it.toString())},
                    modifier = Modifier.fillMaxWidth()
                )
            }


//            Text(text = "Amplitude B (Green)")
//            Slider(
//                value = ampB,
//                onValueChange = { component.changeSineParameters(sineB.copy(amplitude = it)) },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Text(text = "Amplitude C (Red)")
//            Slider(
//                value = ampC,
//                onValueChange = { component.changeSineParameters(sineC.copy(amplitude = it)) },
//                modifier = Modifier.fillMaxWidth()
//            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}