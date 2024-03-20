package io.nev3rfail.escpos2bmp

import java.awt.Font

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction

/*fun createNewImage(width: Int, height: Int): BufferedImage {
    return BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
}*/

fun BufferedImage.fill(value: Byte) {
    val g = this.createGraphics()
    g.color = Color(value.toInt() and 0xFF, value.toInt() and 0xFF, value.toInt() and 0xFF)
    g.fillRect(0, 0, this.width, this.height)
    g.dispose()
}

fun drawTextOnImage(image: BufferedImage, pos: Pair<Int, Int>, scale: Float, font: Font, text: String) {
    val g = image.createGraphics() as Graphics2D
    g.color = Color.BLUE // Assuming the text color is blue as in the Rust example
    g.font = font.deriveFont(scale) // Set font size based on scale
    g.drawString(text, pos.first, pos.second + g.fontMetrics.ascent) // Adjust y position by font ascent for accurate positioning
    g.dispose()
}

fun drawLineSegmentOnImage(image: BufferedImage, start: Pair<Int, Int>, end: Pair<Int, Int>) {
    val g = image.createGraphics()
    g.color = Color.BLUE // Assuming the line color is blue as in the Rust example
    g.drawLine(start.first, start.second, end.first, end.second)
    g.dispose()
}


fun createFont(fontData: ByteArray, size: Float): Font {
    // Convert ByteArray to Font. Implementation may vary depending on the platform and library.
    val inputStream = fontData.inputStream()
    return Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(size)
}