package com.paveltinnik.plotcreator.presentation.main

import kotlinx.coroutines.flow.StateFlow

interface MainComponent {

    val model: StateFlow<MainStore.State>

    fun onPlotClicked()
}