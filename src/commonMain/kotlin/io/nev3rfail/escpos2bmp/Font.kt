package io.nev3rfail.escpos2bmp

expect class Font {
    val name: String
    val size: Int

    fun isBold(): Boolean
    fun isItalic(): Boolean
}