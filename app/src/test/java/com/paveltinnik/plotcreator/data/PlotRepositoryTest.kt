package com.paveltinnik.plotcreator.data

import app.cash.turbine.test
import com.paveltinnik.plotcreator.domain.model.SignalPreset
import com.paveltinnik.plotcreator.domain.model.SineWave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlotRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var repository: PlotRepositoryImpl

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = PlotRepositoryImpl
        // Reset to initial state
        repository.setSineWaves(SignalPreset.THREE_PHASE.waves)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `sineWaves emits initial 3-phase waves`() = runTest {
        repository.sineWaves.test {
            val initialList = awaitItem()
            assertEquals(3, initialList.size)
            assertEquals(1.0f, initialList[0].amplitude)
            assertEquals(0f, initialList[0].phase)
            assertEquals(120f, initialList[1].phase)
            assertEquals(-120f, initialList[2].phase)
        }
    }

    @Test
    fun `changeSineParameter updates specific wave parameters correctly`() = runTest {
        repository.sineWaves.test {
            awaitItem() // Consume initial emission

            val updatedWave = SineWave(id = 1, amplitude = 1.8f, frequency = 2.5f, phase = 45f)
            repository.changeSineParameter(updatedWave)

            val updatedList = awaitItem()
            val wave1 = updatedList.first { it.id == 1 }
            assertEquals(1.8f, wave1.amplitude)
            assertEquals(2.5f, wave1.frequency)
            assertEquals(45f, wave1.phase)
        }
    }

    @Test
    fun `setSineWaves updates repository with preset waves`() = runTest {
        repository.sineWaves.test {
            awaitItem() // Consume initial emission

            val presetWaves = SignalPreset.SQUARE_WAVE.waves
            repository.setSineWaves(presetWaves)

            val updatedList = awaitItem()
            assertEquals(3, updatedList.size)
            assertEquals(0.33f, updatedList[1].amplitude)
            assertEquals(3.0f, updatedList[1].frequency)
            assertEquals(0.20f, updatedList[2].amplitude)
            assertEquals(5.0f, updatedList[2].frequency)
        }
    }
}
