package ru.ancndz.timeapp.core;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Генератор QR-кода.
 */
public class QrCodeGenerator {

    /**
     * Генерация изображения QR-кода.
     *
     * @param text
     *            текст
     * @param width
     *            ширина
     * @param height
     *            высота
     * @return изображение QR-кода
     * @throws WriterException
     *             ошибка записи
     * @throws IOException
     *             ошибка ввода-вывода
     */
    public static byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        final QRCodeWriter qrCodeWriter = new QRCodeWriter();
        final BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

        final BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        return baos.toByteArray();
    }
}
