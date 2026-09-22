package com.paveltinnik.plotcreator.presentation.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.paveltinnik.plotcreator.presentation.extensions.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultMainComponent(
    componentContext: ComponentContext,
    private val onClickPlotButton: () -> Unit,
) : MainComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        val storeFactory = MainStoreFactory()
        storeFactory.create()
    }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    MainStore.Label.ClickPlotButton -> {
                        onClickPlotButton()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<MainStore.State>
        get() = store.stateFlow

    override fun onPlotClicked() {
        store.accept(MainStore.Intent.ClickPlotButton)
    }
}