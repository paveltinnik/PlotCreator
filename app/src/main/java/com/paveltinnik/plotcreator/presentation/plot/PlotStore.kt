package com.paveltinnik.plotcreator.presentation.plot

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.presentation.plot.PlotStore.Intent
import com.paveltinnik.plotcreator.presentation.plot.PlotStore.State
import com.paveltinnik.plotcreator.presentation.plot.PlotStore.Label
import kotlinx.coroutines.launch

interface PlotStore : Store<Intent, State, Label> {

    data class State(
        val waves: List<SineWave>,
    )

    sealed interface Intent {

        data class ChangeSineWave(val sineWave: SineWave) : Intent

        object ClickBack : Intent
    }

    sealed interface Label {

        object ClickBack : Label
    }
}

class PlotStoreFactory() {

    private val storeFactory: StoreFactory = DefaultStoreFactory()

    fun create(): PlotStore =
        object : PlotStore, Store<Intent, State, Label> by storeFactory.create(
            name = "PlotStore",
            initialState = State(listOf()),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}.apply {
            Log.d("Store", "PlotStore Created")
        }

    private sealed interface Action

    private sealed interface Msg {

        data class ChangeSineWave(val sineWave: SineWave) : Msg
    }

    private inner class BootstrapperImpl() : CoroutineBootstrapper<Action>() {
        override fun invoke() {
            scope.launch {

            }
        }
    }

    private inner class ExecutorImpl() : CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeAction(action: Action) {

        }

        override fun executeIntent(intent: Intent) {
            when (intent) {
                is Intent.ChangeSineWave -> {
                    dispatch(Msg.ChangeSineWave(sineWave = intent.sineWave))
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
                is Msg.ChangeSineWave -> {
                    val newWave = msg.sineWave
                    val newList = waves.map { wave ->
                        if (wave.id == newWave.id) newWave else wave
                    }

                    State(newList)
                }
            }
        }
    }
}