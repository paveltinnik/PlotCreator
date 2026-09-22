package com.paveltinnik.plotcreator.presentation.root

import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.paveltinnik.plotcreator.presentation.main.DefaultMainComponent
import com.paveltinnik.plotcreator.presentation.main.MainComponent
import com.paveltinnik.plotcreator.presentation.plot.DefaultPlotComponent
import com.paveltinnik.plotcreator.presentation.plot.PlotComponent
import com.paveltinnik.plotcreator.presentation.plot.PlotStoreFactory
import kotlinx.parcelize.Parcelize

class DefaultRootComponent(
    componentContext: ComponentContext,
) : RootComponent, ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Main,
        handleBackButton = true,
        childFactory = ::child,
        serializer = null,
    )

    @OptIn(DelicateDecomposeApi::class)
    private fun child(
        config: Config,
        componentContext: ComponentContext,
    ): RootComponent.Child {
        return when (config) {
            Config.Main -> {
                val component = DefaultMainComponent(
                    componentContext = componentContext,
                    onClickPlotButton = { navigation.push(Config.Plot) }
                )
                RootComponent.Child.Main(component)
            }

            Config.Plot -> {
                val component = DefaultPlotComponent(
                    componentContext = componentContext,
                    onBackClicked = { navigation.pop() },
                )
                RootComponent.Child.Plot(component)
            }
        }
    }

    private sealed interface Config : Parcelable {

        @Parcelize
        object Main : Config

        @Parcelize
        object Plot : Config
    }
}