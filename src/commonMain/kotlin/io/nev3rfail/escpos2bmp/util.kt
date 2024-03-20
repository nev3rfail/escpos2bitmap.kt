package io.nev3rfail.escpos2bmp

import dev.mihon.bitmap.Bitmap
import dev.mihon.bitmap.Canvas
import dev.mihon.bitmap.Rect
import java.io.File
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction

val decoder = Charset.forName("IBM866").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(
    CodingErrorAction.REPORT)



fun decodeIbm866(byte: Byte): String {
    val bytes: ByteArray = ByteArray(1)
    bytes[0] = byte
    val decoded = decoder.decode(ByteBuffer.wrap(bytes)).toString()
    println("0x${"%02x".format(byte)} ($byte) = $decoded")
    return decoded
}

expect fun Bitmap.drawText(pos: Pair<Int, Int>, scale: Float, font: Font, text: String)

expect fun Bitmap.drawLine(start: Pair<Int, Int>, end: Pair<Int, Int>)

expect fun Bitmap.fill(value: Byte)

expect fun Bitmap.Companion.zeroed(w: Int, h: Int, color: Int=255, config: Bitmap.Config = Bitmap.Config.RGB_565): Bitmap



fun Canvas.drawBitmap(source: Bitmap, x: Int, y: Int) {
    ////finalImage.graphics.drawImage(slice, 0, y, null)
    //("" as Bitmap).image.graphics.drawImage(source.image, top, left, null)
    drawBitmap(source, Rect(0, 0, source.width, source.height), Rect(x,y))
}

expect fun Bitmap.save(where: File)





