package com.nasdroid.apitester

import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.nasdroid.api.websocket.jsonrpc.JsonRpcWebsocketClient
import kotlinx.coroutines.runBlocking

fun main() = application {
    val state = rememberWindowState()
    val client = remember { JsonRpcWebsocketClient() }
    Window(
        title = "TrueNAS API Tester",
        onCloseRequest = {
            // Must disconnect
            runBlocking {
                client.disconnect()
            }
            exitApplication()
        },
        state = state
    ) {
        App(client)
    }
}
