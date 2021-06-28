import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlin.math.max
import androidx.compose.ui.graphics.Color as AndroidColor

enum class Color(val color: AndroidColor) {
    Black(AndroidColor.DarkGray),
    White(AndroidColor.White)
}

enum class PieceType {
    Pawn,
    Rook,
    Knight,
    Bishop,
    Queen,
    King;
}

data class Piece(val type: PieceType, val color: Color)
class Board(boardString: String) {
    private val board: Array<Array<Piece?>> = stringToPieces(boardString)

    init {
        assert(board.size == 8) { "incomplete board" }
        assert(board.all { it.size == 8} ) { "incomplete row"}
    }

    operator fun get(x: Int, y: Int): Piece? {
        return board[y][x]
    }

    companion object {
        val empty = Board("8/8/8/8/8/8/8/8")
    }
}

private fun stringToPieces(str: String): Array<Array<Piece?>> = str.split("/").map { row ->
    replaceSpaces(row).map(::charToPiece).toTypedArray()
}.toTypedArray()

private fun replaceSpaces(s: String): String {
    var str = s
    for (i in 1..8) {
        str = str.replace(i.toString(), " ".repeat(i))
    }
    return str
}

private fun charToPiece(c: Char): Piece? {
    val white = c.isUpperCase()
    val pieceType = when(c.toLowerCase())  {
        'p' -> PieceType.Pawn
        'n' -> PieceType.Knight
        'k' -> PieceType.King
        'q' -> PieceType.Queen
        'b' -> PieceType.Bishop
        'r' -> PieceType.Rook
        ' ' -> return null
        else -> throw IllegalArgumentException("$c is not a valid chess piece")
    }

    return Piece(pieceType, if(white) Color.White else Color.Black)
}

@Composable
fun ChessBoard(board: Board, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val fieldsize = max(this.size.width, this.size.height) / 8f
        background(fieldsize)
        for (row in 0 until 8) {
            for (col in 0 until 8) {
                val piece = board[col, row] ?: continue
                drawPiece(fieldsize.toInt(), piece, col, row)
            }
        }
    }
}

private fun DrawScope.background(fieldsize: Float) {
    for (row in 0 until 8)
        for(col in 0 until 8)
            drawRect(if((row+col) % 2 == 0) Color.White.color else Color.Black.color, Offset(col*fieldsize, row*fieldsize), Size(fieldsize, fieldsize))
}

private val pieces = Color.values().flatMap { color ->
    PieceType.values().map { pieceType ->
        Piece(pieceType, color)
    }
}

private fun loadPieceImage(piece: Piece): ImageBitmap = imageFromResource("pieces/${piece.color.name.toLowerCase()}/${piece.type.name.toLowerCase()}.png")

private val pieceImages = pieces.map {
    it to loadPieceImage(it)
}.toMap()

private fun DrawScope.drawPiece(fieldsize: Int, piece: Piece, col: Int, row: Int) {
    val image = pieceImages[piece] ?: throw Exception("missing piece image")
    drawImage(image, dstOffset = IntOffset(col * fieldsize, row * fieldsize), dstSize = IntSize(fieldsize, fieldsize))
}

