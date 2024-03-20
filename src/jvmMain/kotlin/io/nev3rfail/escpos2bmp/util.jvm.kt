package io.nev3rfail.escpos2bmp

import dev.mihon.bitmap.Bitmap
import java.awt.Color
import java.awt.Graphics2D
import java.io.File

actual fun Bitmap.drawText(
    pos: Pair<Int, Int>,
    scale: Float,
    font: Font,
    text: String
) {
    val g = image.createGraphics() as Graphics2D
    g.color = Color.BLUE // Assuming the text color is blue as in the Rust example
    g.font = font.inner.deriveFont(scale) // Set font size based on scale
    g.drawString(text, pos.first, pos.second + g.fontMetrics.ascent) // Adjust y position by font ascent for accurate positioning
    g.dispose()
}

actual fun Bitmap.drawLine(
    start: Pair<Int, Int>,
    end: Pair<Int, Int>
) {
    val g = image.createGraphics()
    g.color = Color.BLUE // Assuming the line color is blue as in the Rust example
    g.drawLine(start.first, start.second, end.first, end.second)
    g.dispose()
   // ("" as Bitmap).image.graphics.drawImage(slice, 0, y, null)
}

actual fun Bitmap.fill(value: Byte) {
    val g = image.createGraphics()
    g.color = Color(value.toInt() and 0xFF, value.toInt() and 0xFF, value.toInt() and 0xFF)
    g.fillRect(0, 0, this.width, this.height)
    g.dispose()
}


actual fun Bitmap.Companion.zeroed(
    w: Int,
    h: Int,
    color: Int,
    config: Bitmap.Config
): Bitmap {
    return createBitmap(w,h,config).apply {
        this.fill(color.toByte())
    }
}

actual fun Bitmap.save(where: File) {
}