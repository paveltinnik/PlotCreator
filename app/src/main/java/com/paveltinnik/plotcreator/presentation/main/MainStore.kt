package com.paveltinnik.plotcreator.presentation.main

import android.util.Log
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.paveltinnik.plotcreator.presentation.main.MainStore.Intent
import com.paveltinnik.plotcreator.presentation.main.MainStore.Label
import com.paveltinnik.plotcreator.presentation.main.MainStore.State

interface MainStore : Store<Intent, State, Label> {

    class State

    sealed interface Intent {
        object ClickPlotButton : Intent
    }

    sealed interface Label {
        object ClickPlotButton : Label
    }
}

class MainStoreFactory {

    private val storeFactory: StoreFactory = DefaultStoreFactory()

    fun create(): MainStore =
        object : MainStore, Store<Intent, State, Label> by storeFactory.create(
            name = "MainStore",
            initialState = State(),
            bootstrapper = BootstrapperImpl(),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}.apply {
            Log.d("MainStore", "MainStore Created")
        }

    private sealed interface Action

    private sealed interface Msg

    private class BootstrapperImpl : CoroutineBootstrapper<Action>() {
        override fun invoke() {}
    }

    private class ExecutorImpl() : CoroutineExecutor<Intent, Action, State, Msg, Label>() {

        override fun executeIntent(intent: Intent) {
            when (intent) {
                Intent.ClickPlotButton -> {
                    publish(Label.ClickPlotButton)
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(msg: Msg): State {
            return this
        }
    }
}