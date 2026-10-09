package com.paveltinnik.plotcreator.presentation.plot

import app.cash.turbine.test
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.paveltinnik.plotcreator.data.PlotRepositoryImpl
import com.paveltinnik.plotcreator.domain.model.SignalPreset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlotStoreTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var store: PlotStore

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        PlotRepositoryImpl.setSineWaves(SignalPreset.THREE_PHASE.waves)
        val factory = PlotStoreFactory()
        store = factory.create()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct default values`() {
        val state = store.state
        assertFalse(state.isAnimated)
        assertTrue(state.isSumVisible)
    }

    @Test
    fun `ToggleAnimation intent updates isAnimated state`() = runTest {
        store.stateFlow.test {
            val initialState = awaitItem()
            assertFalse(initialState.isAnimated)

            store.accept(PlotStore.Intent.ToggleAnimation(isAnimated = true))

            val updatedState = awaitItem()
            assertTrue(updatedState.isAnimated)
        }
    }

    @Test
    fun `ToggleSumVisibility intent updates isSumVisible state`() = runTest {
        store.stateFlow.test {
            val initialState = awaitItem()
            assertTrue(initialState.isSumVisible)

            store.accept(PlotStore.Intent.ToggleSumVisibility(isSumVisible = false))

            val updatedState = awaitItem()
            assertFalse(updatedState.isSumVisible)
        }
    }

    @Test
    fun `ApplyPreset intent updates store state with preset waves`() = runTest {
        store.stateFlow.test {
            awaitItem() // Initial state

            store.accept(PlotStore.Intent.ApplyPreset(SignalPreset.SAWTOOTH))

            val stateWithPreset = awaitItem()
            assertEquals(3, stateWithPreset.waves.size)
            assertEquals(0.50f, stateWithPreset.waves[1].amplitude)
            assertEquals(2.0f, stateWithPreset.waves[1].frequency)
        }
    }
}
