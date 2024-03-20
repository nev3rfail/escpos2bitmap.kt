package io.nev3rfail.escpos2bmp

import dev.mihon.bitmap.Bitmap
import dev.mihon.bitmap.BitmapFactory
import dev.mihon.bitmap.Canvas
import dev.mihon.bitmap.Rect
import java.awt.Font


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

data class FontScale(
    val x: Int,
    val y: Int
)

data class EscPosState(
    var state: State = State.None,
    var justification: Justification = Justification.Left,
    var isUnderline: Boolean = false,
    var font: FontType = FontType.Regular,
    var imageSlices: MutableList<Bitmap> = mutableListOf(), // Assuming Image is a type provided by your multiplatform library
    var qrSize: Byte = 0,
    var qrCorrLevel: Byte = 0,
    var qrBuff: MutableList<Byte> = mutableListOf(),
    var pos: Pair<Int, Int> = Pair(0, 0),
    var currImg: Bitmap // Assuming Image is a type provided by your multiplatform library
)

data class EscPosConfig(
    val imgWidth: Int,
    val fontRegular: Font, // You'll need to initialize this with your actual font data
    val fontBold: Font, // You'll need to initialize this with your actual font data
    val fontScale: FontScale // Assuming this is a scale factor, adjust the type if needed
)
