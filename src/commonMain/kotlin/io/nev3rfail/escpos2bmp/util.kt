package io.nev3rfail.escpos2bmp

import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction

val decoder = Charset.forName("IBM866").newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(
    CodingErrorAction.REPORT)



fun decodeIbm866(byte: Byte): String {
    // Assuming the byte is a valid IBM866 encoded character, this function decodes it
    // Since IBM866 is a single-byte character encoding, we can convert the byte to a char
    // This is a simplified example; for full IBM866 support, consider using a library or implementing a full mapping
    //val charCode = byte.toInt() and 0xFF // Convert to unsigned
    //return char(charCode).toString() // This is a naive conversion; use a proper IBM866 to Unicode mapping for real applications
    val bytes: ByteArray = ByteArray(1)// Your byte array here
    bytes[0] = byte
    //val charset = Charset.forName("IBM866")
    //return charset.decode(ByteBuffer.wrap(bytes)).get().toString()//.wrap()byte)

    /*    val decoded = String(bytes, charset);
        println("$byte = $decoded")
        if(byte.toInt() == 3) {
            val s = charset.decode(ByteBuffer.wrap(bytes)).get().toString()
            println("!!")
        }*/
    val decoded = decoder.decode(ByteBuffer.wrap(bytes)).toString()
    println("0x${"%02x".format(byte)} ($byte) = $decoded")
    return decoded
}
