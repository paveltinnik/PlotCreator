package com.paveltinnik.plotcreator.presentation.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.paveltinnik.plotcreator.presentation.ui.content.MainContent
import com.paveltinnik.plotcreator.presentation.ui.content.Plot
import com.paveltinnik.plotcreator.presentation.ui.theme.PlotCreatorTheme

@Composable
fun RootContent(
    component: RootComponent,
) {
    PlotCreatorTheme {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Children(
                stack = component.stack
            ) {
                when (val instance = it.instance) {

                    is RootComponent.Child.Main -> {
                        MainContent(
                            modifier = Modifier,
                            component = instance.component
                        )
                    }
                    is RootComponent.Child.Plot -> {
                        Plot(
                            modifier = Modifier,
                            component = instance.component
                        )
                    }
                }
            }
        }
    }
}