package com.nxlauncher.bridge

import android.content.Context
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.nxlauncher.ui.NXLauncherRoot

object NXComposeMenu {
    @JvmStatic
    fun create(context: Context, onPlay: Runnable, onAccount: Runnable): View {
        val view = ComposeView(context)
        view.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        view.setContent {
            NXLauncherRoot(
                onPlay = { onPlay.run() },
                onAccount = { onAccount.run() }
            )
        }
        return view
    }
}
