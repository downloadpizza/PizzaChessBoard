import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import androidx.compose.ui.window.application
import androidx.compose.ui.window.v1.KeyStroke
import androidx.compose.ui.window.v1.Menu
import androidx.compose.ui.window.v1.MenuBar
import androidx.compose.ui.window.v1.MenuItem
import java.awt.Dialog
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    val boards = mutableStateListOf<Board>()
    var boardIndex by remember { mutableStateOf(0) }
    var openDialogueWaiting by remember { mutableStateOf(false) }

    Window(
        size = IntSize(600, 600), menuBar = MenuBar(
            Menu(
                "File",
                MenuItem(
                    name = "Open",
                    onClick = {
                        openDialogueWaiting = true
                    },
                    shortcut = KeyStroke(Key.O)
                )
            )
        )
    ) {
        if (openDialogueWaiting) {
            AwtWindow(
                create = {
                    object : FileDialog(null as Frame?, "Choose a file", LOAD) {
                        override fun setVisible(value: Boolean) {
                            super.setVisible(value)
                            if (value) {
                                openDialogueWaiting = false;
                                if (file != null) {
                                    boards.clear()
                                    boards.addAll(File(directory).resolve(file).readLines().map(::Board))
                                } else {
                                    boards.clear()
                                }
                            }
                        }
                    }.apply {
                        this.title = title
                    }
                },
                dispose = FileDialog::dispose
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ChessBoard(boards.getOrElse(boardIndex) { Board.empty }, modifier = Modifier.fillMaxSize(0.8f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .absolutePadding(top=50.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(onClick = {
                    if (boardIndex - 1 >= 0) {
                        boardIndex -= 1
                    }
                }, enabled = boardIndex - 1 >= 0) {
                    Text("<")
                }

                Button(onClick = {
                    if (boardIndex + 1 < boards.size) {
                        boardIndex += 1
                        println("next $boardIndex")
                    }
                }, enabled = boardIndex + 1 < boards.size) {
                    Text(">")
                }
            }
        }
    }
}