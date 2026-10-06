package com.paveltinnik.plotcreator.presentation.plot

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.paveltinnik.plotcreator.data.PlotRepositoryImpl
import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.domain.usecase.ChangeSineWaveUsecase
import com.paveltinnik.plotcreator.domain.usecase.GetSineWavesUsecase
import com.paveltinnik.plotcreator.presentation.plot.PlotStore.Intent
import com.paveltinnik.plotcreator.presentation.plot.PlotStore.State
import com.paveltinnik.plotcreator.presentation.plot.PlotStore.Label
import kotlinx.coroutines.launch

interface PlotStore : Store<Intent, State, Label> {

    data class State(
        val waves: List<SineWave>,
        val isAnimated: Boolean = false
    )

    sealed interface Intent {

        data class ChangeSineWave(val sineWave: SineWave) : Intent

        data class ToggleAnimation(val isAnimated: Boolean) : Intent

        object ClickBack : Intent
    }

    sealed interface Label {

        object ClickBack : Label
    }
}

class PlotStoreFactory {

    private val storeFactory: StoreFactory = DefaultStoreFactory()
    private val getSineWavesUsecase = GetSineWavesUsecase(PlotRepositoryImpl)
    private val changeSineWaveUsecase = ChangeSineWaveUsecase(PlotRepositoryImpl)

    fun create(): PlotStore =
        object : PlotStore, Store<Intent, State, Label> by storeFactory.create(
            name = "PlotStore",
            initialState = State(listOf(), false),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}.apply {
            Log.d("Store", "PlotStore Created")
        }

    private sealed interface Action {

        data class WavesLoaded(val waves: List<SineWave>): Action
    }

    private sealed interface Msg {

        data class WavesLoaded(val waves: List<SineWave>): Msg

        data class ChangeSineWave(val sineWave: SineWave) : Msg

        data class ToggleAnimation(val isAnimated: Boolean) : Msg
    }

    private inner class BootstrapperImpl() : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {
                getSineWavesUsecase().collect {
                    dispatch(Action.WavesLoaded(it))
                }
            }
        }
    }

    private inner class ExecutorImpl() : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {
            when (action) {
                is Action.WavesLoaded -> {
                    dispatch(Msg.WavesLoaded(action.waves))
                }
            }
        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ChangeSineWave -> {
                    val state = state()
                    changeSineWaveUsecase(intent.sineWave)
                    dispatch(Msg.ChangeSineWave(sineWave = intent.sineWave))
                }

                is Intent.ToggleAnimation -> {
                    dispatch(Msg.ToggleAnimation(intent.isAnimated))
                }

                Intent.ClickBack -> {
                    publish(Label.ClickBack)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return when (msg) {
                is Msg.WavesLoaded -> {
                    copy(waves = msg.waves)
                }

                is Msg.ChangeSineWave -> {
                    val newWave = msg.sineWave
                    val newList = waves.map { wave ->
                        if (wave.id == newWave.id) newWave else wave
                    }

                    copy(waves = newList)
                }

                is Msg.ToggleAnimation -> {
                    copy(isAnimated = msg.isAnimated)
                }
            }
        }
    }
}
