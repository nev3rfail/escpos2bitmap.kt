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


inline fun getResource(r: String) = File(object {}.javaClass.getResource("/").toURI())
    .parentFile.parentFile.parentFile.parentFile.toPath().resolve("processedResources/jvm/main").resolve(r).toFile()
        .readBytes()
inline fun getTestResource(r: String) = File(object {}.javaClass.getResource("/").toURI())
    .parentFile.parentFile.parentFile.parentFile.toPath().resolve("processedResources/jvm/test").resolve(r).toFile()
    .readBytes()


class EscPosConverterTest : StringSpec({
    "should display image from base64" {
        val base64ImageString = "G3QHICAgICAgICAgICAgICAgIFRlc3QgdHJhZGVwb2ludCAgICAgICAgICAgICAgICAgCiAgICAgICAgICAgICAgIFRlc3QgY29tcGFueSBuYW1lICAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgIJONjzogMTIzNDU2Nzg5ICAgICAgICAgICAgICAgICAKLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tCiAgICAgICAgICAgICAgII+roOKlpq3rqSCkrqrjrKWt4iAgICAgICAgICAgICAgIAogICAgICAgICAgICAgICAgICAgICD8IDEyMyAgICAgICAgICAgICAgICAgICAgICAKG0UBkKWjLvwgiqDh4es6IBtFADExOTAwMjYxNCAbRQGHoKIu/CCRio46IBtFAEFWUTExMDMxMDEwNzAzChtFAYKgq+7ioDogG0UAQllOICAgIBtFAYSuqi3iIKegquDr4jogG0UAMjEuMDIuMjAyMiAxNzozMzoyOAobRQGKoOHhqOA6G0UALi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLlRlc3QKLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tChtFARstAZKuoqDgICAgICAgICAgICAgICAgICAgICAgICAgICAgii2iriAglqWtoCAgiOKuoxtFABstAAqKoKqupS3iriCtoKispa2uoqCtqKUg4q6ioOCtrqkgIDEgICAgIDAgICAgIDAgICAKGy0Br66nICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgkaqopKqgOiAwLjMzGy0ACoSrqK2trqUgraCorKWtrqKgrailIOKuoqDgoCDhICAgMSAgICAgMCAgICAgMCAgIApHVElOL0VBTiCqrqSurCAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIIqupDoKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAxMjM0NTY3ODkxMTExCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgII2EkSAyMCU6IDEuMgobLQEjIOLj4iCtoOWupKji4e8gqq6srKWt4qDgqKkg4q6iLiCvrqeo5qioIDIgICAgICMbLQAKk+Gr46OgICAgICAgICAgICAgICAgICAgICAgICAgICAxICAgICAwICAgICAwICAgCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIIqupDogMTIzMTIzMQogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICCNoKShoKKqoDoKICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgIDEyCiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgII2EkSAwJTogMAojIOLj4iCtoOWupKji4e8gqq6srKWt4qDgqKkg4q6iLiCvrqeo5qioIDMgICAgICMKLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tChtFAZGqqKSqoCCvriDnparjOiAbRQAwLjEyICAgICAgICAgICAgICAgICAbRQGI4q6jrjogG0UAMC4wMAotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0KG0UBjYSRIDAlOhtFAC4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4wLjAwChtFAY2EkSAyMCU6G0UALi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uMC45OQotLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0KG0UBiJKOg44giiCOj4uAkoU6G0UALi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4wCi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQobRQGCraXhpa2uOiAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAbRQAKG0UBII2gq6jnreusqDobRQAuLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLjEyLjM0ChtFASCBpaetoKuo563rrKg6G0UALi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4xMi4zNQobRQEgIIinIK2o5TogICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAbRQAKG0UBICAggaWnraCrMRtFAC4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLjEyLjM0ChtFASAgIIGlp62gqzIbRQAuLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uMC4wMQobRQEghOAu4a+u4a6hoKyoOhtFAC4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLjAKG0UBICCIpyCtqOU6ICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgICAgG0UAChtFASAgIICioK3hMRtFAC4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uMC4wMAobRQEgICCRpeDiqOSoqqDiG0UALi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLjAuMDAKG0UBGy0BkaSg56A6G0UAGy0AGy0BLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4uLi4bLQAbLQEwLjAwGy0ACi0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLQogICAgICAgICAgk4g6IDU5NEM0MUMwQzRFMjUzNDgwNzE3RDVGNiAgICAgICAgICAKG2ExHShrAwAxQwMdKGsDADFFMR0oaxsAMVAwNTk0QzQxQzBDNEUyNTM0ODA3MTdENUY2HShrAwAxUTAK"

        val escPos = getTestResource("sale.escpos")

        val fontRegular = Font.createFont( getResource("SourceCodePro.ttf"), 42F)
        val fontBold = Font.createFont( getResource("SourceCodePro-Bold.ttf"), 42F)
        val image = EscPosConverter.Companion.escpos2bitmap(
            escPos,
            EscPosConfig(fontRegular, fontBold, FontSize(16,16))
        )
        val latch = CountDownLatch(1)
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

        latch.await() // Blocks here until windowClosed event occurs
        println("Window closed, continuing execution.")
    }
})
