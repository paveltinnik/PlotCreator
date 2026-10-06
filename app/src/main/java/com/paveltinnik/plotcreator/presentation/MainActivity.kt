package com.paveltinnik.plotcreator.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.paveltinnik.plotcreator.presentation.root.DefaultRootComponent
import com.paveltinnik.plotcreator.presentation.root.RootContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RootContent(DefaultRootComponent(defaultComponentContext()))
        }
    }
}