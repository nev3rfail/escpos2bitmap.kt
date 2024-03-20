package io.nev3rfail.escpos2bmp

import dev.mihon.bitmap.Bitmap
import java.awt.Font as JavaFont

actual class Font (val inner: JavaFont) {
    actual val name: String get() = inner.name
    actual val size: Int get() = inner.size

    actual fun isBold(): Boolean = inner.isBold
    actual fun isItalic(): Boolean = inner.isItalic

    actual companion object {

        actual fun createFont(fontData: ByteArray, size: Float): Font {
                // Convert ByteArray to Font. Implementation may vary depending on the platform and library.
                val inputStream = fontData.inputStream()
                return Font(JavaFont.createFont(JavaFont.TRUETYPE_FONT, inputStream).deriveFont(size))
            }

        }

    }