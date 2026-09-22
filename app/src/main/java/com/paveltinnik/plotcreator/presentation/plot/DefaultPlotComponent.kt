package com.paveltinnik.plotcreator.presentation.plot

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.paveltinnik.plotcreator.domain.model.SineWave
import com.paveltinnik.plotcreator.presentation.extensions.componentScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DefaultPlotComponent(
    componentContext: ComponentContext,
    private val onBackClicked: () -> Unit,
) : PlotComponent, ComponentContext by componentContext {

    private val store = instanceKeeper.getStore {
        val storeFactory = PlotStoreFactory()
        storeFactory.create()
    }
    private val scope = componentScope()

    init {
        scope.launch {
            store.labels.collect {
                when (it) {
                    PlotStore.Label.ClickBack -> {
                        onBackClicked()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val model: StateFlow<PlotStore.State>
        get() = store.stateFlow

    override fun changeSineParameters(sineWave: SineWave) {
        store.accept(PlotStore.Intent.ChangeSineWave(sineWave))
    }

    override fun changeAnimationStatus(isAnimated: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onClickBack() {
        store.accept(PlotStore.Intent.ClickBack)
    }
}