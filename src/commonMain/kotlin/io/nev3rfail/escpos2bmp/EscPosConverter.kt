package io.nev3rfail.escpos2bmp

import dev.mihon.bitmap.Bitmap
import dev.mihon.bitmap.BitmapFactory
import dev.mihon.bitmap.Canvas
import dev.mihon.bitmap.Rect
import qrcode.QRCode
import kotlin.math.max


const val ESC: Byte = 0x1B
const val GS: Byte = 0x1D

// Enums translated to sealed classes
sealed class State {
    data object Esc : State()
    data object Gs : State()
    data object None : State()
}

sealed class Justification {
    data object Left : Justification()
    data object Right : Justification()
    data object Center : Justification()
}

sealed class FontType {
    data object Regular : FontType()
    data object Bold : FontType()
}

data class FontSize(
    val x: Int,
    val y: Int
)

data class Point(val x: Int, val y: Int)

 data class EscPosState constructor(
    var state: State = State.None,
    var justification: Justification = Justification.Left,
    var isUnderline: Boolean = false,
    var fontStyle: FontType = FontType.Regular,
    var imageSlices: MutableList<Bitmap> = mutableListOf(), // Assuming Image is a type provided by your multiplatform library
    var qrSize: Byte = 0,
    var qrCorrLevel: Byte = 0,
    var qrBuff: MutableList<Byte> = mutableListOf(),
    var pos: Pair<Int, Int> = Pair(0, 0),
     val config: EscPosConfig
) {
     var currImg: Bitmap = Bitmap.zeroed(if (config.maxWidth > 0) config.maxWidth else config.fontSize.x, config.fontSize.y)
     var canvasWidth: Int = currImg.width
     var fontSize: FontSize = FontSize(config.fontSize.x,config.fontSize.y)
/*    constructor(config: EscPosConfig) : this(config=config) {

    }*/
}

data class EscPosConfig(
    val fontRegular: Font, // You'll need to initialize this with your actual font data
    val fontBold: Font, // You'll need to initialize this with your actual font data
    val fontSize: FontSize, // Assuming this is a scale factor, adjust the type if needed
    val maxWidth: Int=0,
) {

    fun makeState() = EscPosState(config=this)
}

class EscPosConverter {
    companion object {
        fun escpos2bitmap(escpos: ByteArray, fontSize: Int): Bitmap {
            return escpos2bitmap(escpos, EscPosConfig(
                Font.createFont(this::class.java.getResourceAsStream("/resources/SourceCodePro.ttf").readBytes(), fontSize.toFloat()),
                Font.createFont(this::class.java.getResourceAsStream("/resources/SourceCodePro-Bold.ttf").readBytes(), fontSize.toFloat()),
                FontSize(fontSize, fontSize)
            ))
        }

        fun escpos2bitmap(escpos: ByteArray, escPosConfig: EscPosConfig): Bitmap {
            return escpos2bitmap(escpos, escPosConfig.makeState())
        }

        private fun escpos2bitmap(escpos: ByteArray, escPosState: EscPosState): Bitmap {

            return do_render(do_parse(escpos, escPosState))
            /*var idx = 0
            while (idx < escpos.size) {
                println("0x${"%02x".format(escpos[idx])}")
                val old = idx
                when (escPosState.state) {
                    State.Esc -> onEsc(escpos, escPosState, intArrayOf(idx), escPosConfig)
                    State.Gs -> onGs(escpos, escPosState, intArrayOf(idx), escPosConfig)
                    State.None -> onAscii(escpos, escPosState,intArrayOf( idx), escPosConfig)
                }
                ++idx
            }*/



        }
    }
}


fun determine_line_width(escpos: ByteArray, escPosState: EscPosState): Pair<Int, Int> {
    escpos.indexOf(0x0a).let {
        if(it == -1) {
            throw IllegalArgumentException("can't determine width of the first line of the payload")
        }
        val result = do_parse(escpos.clone().sliceArray(0..it-1), escPosState.config.copy(maxWidth = escPosState.config.fontSize.x).makeState(), dry_run = true)
        return Pair(result.pos.first, 0)
    }

}

fun do_parse(escpos: ByteArray, escPosState: EscPosState, dry_run: Boolean = false): EscPosState {
    if (!dry_run && escPosState.config.maxWidth == 0) {
        determine_line_width(escpos, escPosState).let { (max_width, also) ->
            println("OF FUCK THE LENGTH ${max_width} / ${also}")
            // Update the state with the new maxWidth and call do_parse again
            return do_parse(escpos, escPosState.config.copy(maxWidth = max_width).makeState(), false)
        }
    } else {
        var idx = intArrayOf(0)
        while (idx[0] < escpos.size) {
            println("0x${"%02x".format(escpos[idx[0]])}")
            when (escPosState.state) {
                State.Esc -> onEsc(escpos, escPosState, idx)
                State.Gs -> onGs(escpos, escPosState, idx)
                State.None -> onAscii(escpos, escPosState, idx)
            }
            // Check to ensure idx is progressing to avoid an infinite loop within do_parse
            //if (idx[0] == escpos.size) break // or some other condition to ensure progress
        }
    }
    return escPosState
}
fun do_render(escPosState: EscPosState): Bitmap {
    val imgHeight = escPosState.imageSlices.sumOf { it.height }
    //val finalImage = Bitmap.createBitmap(escPosConfig.imgWidth, imgHeight)
    val finalImage = Bitmap.zeroed(escPosState.canvasWidth, imgHeight)
    var y = 0
    for (slice in escPosState.imageSlices) {
        Canvas(finalImage).drawBitmap(slice, 0, y)
        //finalImage.graphics.drawImage(slice, 0, y, null)
        y += slice.height
    }

    return finalImage
}


fun onAscii(buff: ByteArray, state: EscPosState, idx: IntArray) {
    val byte = buff[idx[0]]

    when (byte) {
        ESC -> {
            //println("switch to State.Esc")
            state.state = State.Esc
        }
        GS -> {
            //println("switch to State.Gs")
            state.state = State.Gs
        }
        0x0A.toByte() -> {
            //newline -- making new line
            val img = Bitmap.zeroed(state.config.maxWidth, state.fontSize.y) // Assuming createNewImage creates a new image
            state.currImg = img
            state.imageSlices.add(img)
            state.pos = Pair(0, 0)
        }
        /*0x00.toByte(), 0x03.toByte() -> {
            /** garbage, noop */
        }*/
        else -> {
            // Assuming v is the byte to decode
            val str = decodeIbm866(byte)  // Use the previously provided decodeIbm866 function

            val font = when (state.fontStyle) {
                FontType.Regular -> state.config.fontRegular
                FontType.Bold -> state.config.fontBold
            }

            val scale = state.fontSize

            // Draw text on the current image slice
            state.currImg.drawText(
                state.pos,
                scale.y.toFloat(),
                font, // createFont needs to be defined to create a Font from ByteArray and size
                str
            )

            val endX = state.pos.first + scale.x / 2

            if (state.isUnderline) {
                // Draw underline
                state.currImg.drawLine(
                    Pair(state.pos.first, state.pos.second + scale.y),
                    Pair(endX, state.pos.second + scale.y)
                )
            }

            state.pos = Pair(endX, state.pos.second)
        }
    }

    idx[0]++
}


fun onGs(buff: ByteArray, state: EscPosState, idx: IntArray) {
    println("on gs: 0x${buff[idx[0]].toString(16).padStart(2, '0')}")
    when (buff[idx[0]]) {
        0x28.toByte() -> {
            idx[0] += 1
            when (buff[idx[0]]) {
                0x6B.toByte() -> {
                    idx[0] += 1
                    onGs2d(buff, state, idx) // You'll need to define this function
                }
                else -> { /* no-op */ }
            }
        }
        0x56.toByte() -> {
            // cut
            idx[0] += 1
        }
        else -> { /* no-op */ }
    }

    state.state = State.None
}


fun onEsc(buff: ByteArray, state: EscPosState, idx: IntArray) {
    val byte = buff[idx[0]]

    when (byte) {
        0x61.toByte() -> {
            // Justification
            idx[0] += 1
            val justificationByte = buff[idx[0]]
            state.justification = when (justificationByte) {
                0.toByte(), 48.toByte() -> Justification.Left
                1.toByte(), 49.toByte() -> Justification.Center
                2.toByte(), 50.toByte() -> Justification.Right
                else -> Justification.Left
            }
        }
        0x45.toByte() -> {
            // Bold
            idx[0] += 1
            val boldnessByte = buff[idx[0]]
            state.fontStyle = if (boldnessByte == 1.toByte()) FontType.Bold else FontType.Regular
        }
        0x2D.toByte() -> {
            // Underline
            idx[0] += 1
            val underlineByte = buff[idx[0]]
            state.isUnderline = underlineByte == 1.toByte()
            if(!state.isUnderline) {
                println("ss")
            }
        }
        0x74.toByte() -> {
            // Select char table
            idx[0] += 1
            // Implementation for selecting character table, if needed
        }
        else -> { /* No operation for other bytes */ }
    }

    state.state = State.None
    idx[0] += 1 // Move to the next byte in the buffer
}

fun onGs2d(buff: ByteArray, state: EscPosState, idx: IntArray) {
    val pL = buff[idx[0]].toInt() and 0xFF
    val pH = buff[idx[0] + 1].toInt() and 0xFF
    val size = (pL or (pH shl 8)) - 3

    // Assuming _cn is not used further in this snippet
    val fn = buff[idx[0] + 3].toInt()

    idx[0] += 4

    println("call fn%03d".format(fn))
    when (fn) {
        67 -> {
            state.qrSize = buff[idx[0]]
        }
        69 -> {
            state.qrCorrLevel = buff[idx[0]]
        }
        80 -> {
            idx[0] += 1 // move to 'd1'
            state.qrBuff.clear()
            state.qrBuff.addAll(buff.sliceArray(idx[0] until idx[0] + size).toList())

            idx[0] += size -1
        }
        81 -> {
            val qrCode = QRCode(state.qrBuff.toByteArray().decodeToString(), state.qrSize.toInt() )
            //val qrWidth = state.config.maxWidth

            val img = Bitmap.zeroed(state.config.maxWidth, qrCode.computedSize)

            val canvas = Canvas(img)
            val qrCodeWidth = qrCode.computedSize
            val x = (state.config.maxWidth - qrCodeWidth) / 2


            val qrBytes = qrCode.renderToBytes("PNG")
            val f = BitmapFactory.decodeByteArray(qrBytes, 0, qrBytes.size)!!
            println("${img.width} == ${f.width}")
            canvas.drawBitmap(f, Rect(0,0, qrCodeWidth, qrCodeWidth), Rect(x, 0))
            state.imageSlices.add(img)

            idx[0] += 1
        }
    }

    state.state = State.None
}
