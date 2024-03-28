package io.nev3rfail.escpos2bmp

import io.kotest.core.spec.style.StringSpec
import qrcode.render.QRCodeGraphicsFactory
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.util.*
import java.util.concurrent.CountDownLatch
import javax.swing.ImageIcon
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.SwingUtilities



class EscPosConverterTest : StringSpec({
    "should display image from base64" {

        val escPos = getTestResource("sale.escpos")
        val escPos2 = Base64.getDecoder().decode(TestBase64_1)

        val fontRegular = Font.createFont( getResource("SourceCodePro.ttf"), 42F)
        val fontBold = Font.createFont( getResource("SourceCodePro-Bold.ttf"), 42F)


        val latch = CountDownLatch(1)
        listOf(escPos, escPos2).forEach {
            val image = EscPosConverter.Companion.escpos2bitmap(
                it,
                EscPosConfig(fontRegular, fontBold, FontSize(16,16))
            )

            // Display the image in a JFrame
            SwingUtilities.invokeLater {
                val frame = JFrame("Test Image")
                frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                frame.addWindowListener(object : WindowAdapter() {
                    override fun windowClosed(e: WindowEvent) {
                        latch.countDown()
                    }
                })
                val icon = ImageIcon(image.image)
                val label = JLabel(icon)
                frame.add(label)
                frame.pack()
                frame.isVisible = true
            }

            //latch.await() // Blocks here until windowClosed event occurs
            println("Window closed, continuing execution.")
        }
        latch.await()

    }
})
