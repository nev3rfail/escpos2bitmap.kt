package io.nev3rfail.escpos2bmp


expect class Font {
    val name: String
    val size: Int

    fun isBold(): Boolean
    fun isItalic(): Boolean

    companion object {

        fun createFont(fontData: ByteArray, size: Float): Font
    }
}