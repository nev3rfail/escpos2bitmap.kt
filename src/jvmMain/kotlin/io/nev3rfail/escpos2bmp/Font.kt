package io.nev3rfail.escpos2bmp

import java.awt.Font as JavaFont

actual class Font actual constructor(val javaFont: JavaFont) {
    actual val name: String get() = javaFont.name
    actual val size: Int get() = javaFont.size

    actual fun isBold(): Boolean = javaFont.isBold
    actual fun isItalic(): Boolean = javaFont.isItalic
}