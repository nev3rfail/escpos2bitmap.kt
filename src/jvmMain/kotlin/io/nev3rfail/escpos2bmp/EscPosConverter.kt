package io.nev3rfail.escpos2bmp

import dev.mihon.bitmap.Bitmap
import qrcode.QRCode



class EscPosConverter {
    companion object {
        fun escpos2bitmap(escpos: ByteArray, fontSize: Int, imgWidth: Int): Bitmap {
            return escpos2bitmap(escpos, EscPosConfig(
                imgWidth,
                createFont(this::class.java.getResourceAsStream("/resources/SourceCodePro.ttf").readBytes(), fontSize.toFloat()),
                createFont(this::class.java.getResourceAsStream("/resources/SourceCodePro.ttf").readBytes(), fontSize.toFloat()),
                FontScale(fontSize, fontSize),
            ))
        }

        fun escpos2bitmap(escpos: ByteArray, escPosConfig: EscPosConfig): Bitmap {
            return escpos2bitmap(escpos, escPosConfig, EscPosState(currImg = Bitmap(escPosConfig.imgWidth, escPosConfig.fontScale.y, Bitmap.Config.RGB_565).apply {
                this.fill(255.toByte()) // Fill with white color
            }
            ))
        }

        fun escpos2bitmap(escpos: ByteArray,  escPosConfig: EscPosConfig, escPosState: EscPosState): Bitmap {
            var idx = intArrayOf(0)
            while (idx[0] < escpos.size) {
                println("0x${"%02x".format(escpos[idx[0]])}")
                val old = idx[0]
                when (escPosState.state) {
                    State.Esc -> onEsc(escpos, escPosState, idx, escPosConfig)
                    State.Gs -> onGs(escpos, escPosState, idx, escPosConfig)
                    State.None -> onAscii(escpos, escPosState, idx, escPosConfig)
                }
            }

            val imgHeight = escPosState.imageSlices.sumOf { it.height }
            //val finalImage = createNewImage(escPosConfig.imgWidth, imgHeight)
            val finalImage = Bitmap(escPosConfig.imgWidth, imgHeight, Bitmap.Config.RGB_565)
            finalImage.fill(255.toByte()) // Fill with white color

            var y = 0
            for (slice in escPosState.imageSlices) {
                finalImage.graphics.drawImage(slice, 0, y, null)
                y += slice.height
            }

            return finalImage
        }
    }
}



fun onAscii(buff: ByteArray, state: EscPosState, idx: IntArray, cfg: EscPosConfig) {
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
            val img = createNewImage(cfg.imgWidth, cfg.fontScale.y) // Assuming createNewImage creates a new image
            img.fill(255.toByte())

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

            val font = when (state.font) {
                FontType.Regular -> cfg.fontRegular
                FontType.Bold -> cfg.fontBold
            }

            val scale = cfg.fontScale

            // Draw text on the current image slice
            drawTextOnImage(
                state.currImg,
                state.pos,
                scale.y.toFloat(),
                font, // createFont needs to be defined to create a Font from ByteArray and size
                str
            )

            val endX = state.pos.first + scale.x / 2

            if (state.isUnderline) {
                // Draw underline
                drawLineSegmentOnImage(
                    state.currImg,
                    Pair(state.pos.first, state.pos.second + scale.y - 1),
                    Pair(endX, state.pos.second + scale.y - 1)
                )
            }

            state.pos = Pair(endX, state.pos.second)
        }
    }

    idx[0]++
}


fun onGs(buff: ByteArray, state: EscPosState, idx: IntArray, cfg: EscPosConfig) {
    println("on gs: 0x${buff[idx[0]].toString(16).padStart(2, '0')}")
    when (buff[idx[0]]) {
        0x28.toByte() -> {
            idx[0] += 1
            when (buff[idx[0]]) {
                0x6B.toByte() -> {
                    idx[0] += 1
                    onGs2d(buff, state, idx, cfg) // You'll need to define this function
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


fun onEsc(buff: ByteArray, state: EscPosState, idx: IntArray, cfg: EscPosConfig) {
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
            state.font = if (boldnessByte == 1.toByte()) FontType.Bold else FontType.Regular
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

fun onGs2d(buff: ByteArray, state: EscPosState, idx: IntArray, cfg: EscPosConfig) {
    val pL = buff[idx[0]].toInt() and 0xFF
    val pH = buff[idx[0] + 1].toInt() and 0xFF
    val size = (pL or (pH shl 8)) - 3

    // Assuming _cn is not used further in this snippet
    val fn = buff[idx[0] + 3].toInt()

    idx[0] += 4

    println("call fn%03d".format(fn))
    //println("--- ${buff[idx[0]]} ${buff[idx[0]+1]} ${buff[idx[0]+2]} ${buff[idx[0]+3]} ${buff[idx[0]+4]} ${buff[idx[0]+5]}")

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


            val qrCode = QRCode(state.qrBuff.toString(), state.qrSize.toInt() and 0xff)

            // FIXME: crutch for height because something goes wrong inside libs
            val crutchedWidth = cfg.imgWidth / 4

            val img = Bitmap.createBitmap(crutchedWidth, qrCode.computedSize, Bitmap.Config.RGB_565)

            val canvas = Canvas(img)
            val qrCodeWidth = qrCode.computedSize
            val x = (crutchedWidth - qrCodeWidth) / 2



            val qrBytes = qrCode.renderToBytes("PNG")
            val f = BitmapFactory.decodeByteArray(qrBytes, 0, qrBytes.size)!!
            canvas.drawBitmap(f, Rect(0,0, qrCodeWidth, qrCodeWidth), Rect(x, 0))
            state.imageSlices.add(img.image)

            idx[0] += 1
        }
    }

    state.state = State.None
}
