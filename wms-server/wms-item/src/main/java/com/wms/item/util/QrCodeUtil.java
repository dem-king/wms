package com.wms.item.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.wms.common.exception.BizException;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码/条形码生成工具类
 * 基于ZXing库实现二维码和条形码图片生成
 */
@Slf4j
public class QrCodeUtil {

    private QrCodeUtil() {
    }

    /**
     * 生成二维码图片(PNG格式)
     *
     * @param content 二维码内容
     * @param width   图片宽度(像素)
     * @param height  图片高度(像素)
     * @return PNG格式图片字节数组
     */
    public static byte[] generateQrImage(String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (WriterException e) {
            log.error("生成二维码失败: content={}", content, e);
            throw new BizException("生成二维码失败");
        } catch (IOException e) {
            log.error("输出二维码图片失败: content={}", content, e);
            throw new BizException("输出二维码图片失败");
        }
    }

    /**
     * 生成条形码图片(PNG格式)
     * 使用Code128编码格式
     *
     * @param content 条形码内容
     * @param width   图片宽度(像素)
     * @param height  图片高度(像素)
     * @return PNG格式图片字节数组
     */
    public static byte[] generateBarcodeImage(String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            Code128Writer writer = new Code128Writer();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.CODE_128, width, height, hints);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("输出条形码图片失败: content={}", content, e);
            throw new BizException("输出条形码图片失败");
        } catch (Exception e) {
            log.error("生成条形码失败: content={}", content, e);
            throw new BizException("生成条形码失败");
        }
    }
}
