package com.paveltinnik.plotcreator.presentation.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.paveltinnik.plotcreator.presentation.main.MainComponent
import com.paveltinnik.plotcreator.presentation.plot.PlotComponent

interface RootComponent {

    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {

        class Main(val component: MainComponent) : Child

        class Plot(val component: PlotComponent) : Child
    }
}