package com.develop25.trendit.service;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil {

    /**
     * imageBytes에서 이미지 MIME 타입을 감지하고 data URI 문자열을 생성합니다.
     * @param imageBytes 이미지의 바이트 배열
     * @return data:image/jpeg;base64,... 형식 문자열
     * @throws IOException 형식이 감지되지 않을 경우 예외 발생
     */
    public static String createDataUriFromImage(byte[] imageBytes) throws IOException {
        String mimeType = detectMimeType(imageBytes);
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        return "data:" + mimeType + ";base64," + base64Image;
    }

    /**
     * 간단한 방식으로 MIME 타입 감지 (JPEG/PNG 위주, 확장 가능)
     */
    private static String detectMimeType(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes)) {
            // ImageIO는 내부적으로 포맷 이름을 반환함 (e.g., jpeg, png)
            String formatName = ImageIO.getImageReaders(ImageIO.createImageInputStream(bais))
                    .next().getFormatName().toLowerCase();

            switch (formatName) {
                case "jpeg":
                case "jpg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                case "bmp":
                    return "image/bmp";
                default:
                    throw new IOException("지원되지 않는 이미지 포맷: " + formatName);
            }
        } catch (Exception e) {
            throw new IOException("이미지 MIME 타입을 감지할 수 없습니다.", e);
        }
    }
}
