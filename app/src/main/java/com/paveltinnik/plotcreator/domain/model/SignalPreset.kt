package com.paveltinnik.plotcreator.domain.model

enum class SignalPreset(
    val title: String,
    val waves: List<SineWave>
) {
    THREE_PHASE(
        title = "3-Phase AC",
        waves = listOf(
            SineWave(id = 1, amplitude = 1.0f, frequency = 1.0f, phase = 0f),
            SineWave(id = 2, amplitude = 1.0f, frequency = 1.0f, phase = 120f),
            SineWave(id = 3, amplitude = 1.0f, frequency = 1.0f, phase = -120f)
        )
    ),
    SQUARE_WAVE(
        title = "Square Wave",
        waves = listOf(
            SineWave(id = 1, amplitude = 1.0f, frequency = 1.0f, phase = 0f),
            SineWave(id = 2, amplitude = 0.33f, frequency = 3.0f, phase = 0f),
            SineWave(id = 3, amplitude = 0.20f, frequency = 5.0f, phase = 0f)
        )
    ),
    SAWTOOTH(
        title = "Sawtooth Wave",
        waves = listOf(
            SineWave(id = 1, amplitude = 1.0f, frequency = 1.0f, phase = 0f),
            SineWave(id = 2, amplitude = 0.50f, frequency = 2.0f, phase = 0f),
            SineWave(id = 3, amplitude = 0.33f, frequency = 3.0f, phase = 0f)
        )
    ),
    BEATS(
        title = "Beats",
        waves = listOf(
            SineWave(id = 1, amplitude = 1.0f, frequency = 2.0f, phase = 0f),
            SineWave(id = 2, amplitude = 1.0f, frequency = 2.2f, phase = 0f),
            SineWave(id = 3, amplitude = 0.0f, frequency = 1.0f, phase = 0f)
        )
    )
}
