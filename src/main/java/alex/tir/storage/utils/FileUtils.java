package alex.tir.storage.utils;

import alex.tir.storage.exception.FileException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.core.io.InputStreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tika.Tika;
public final class FileUtils {

    private FileUtils() {
    }

    public static void saveFile(InputStreamSource fileInputStreamSource, String location) {
        Path filePath = Paths.get(location);
        try (InputStream fileInputStream = fileInputStreamSource.getInputStream()) {
            Files.createDirectories(filePath.getParent());
            Files.copy(fileInputStream, filePath);
        } catch (IOException exception) {
            throw new FileException(exception);
        }
    }


    public static void deleteFile(String fileLocation) {
        Path filePath = Paths.get(fileLocation);
        try {
            if (Files.isDirectory(filePath)) {
                throw new IllegalArgumentException(filePath + " is not a file");
            } else {
                Files.delete(filePath);
            }
        } catch (IOException exception) {
            throw new FileException(exception);
        }
    }


    public static String detectContentType(String fileName, InputStreamSource fileInputStreamSource) {
        Tika tika = new Tika();

        String fileNameDetect = tika.detect(fileName);
        if (!fileNameDetect.equals(MimeTypes.OCTET_STREAM)) {
            return fileNameDetect;
        }

        try (InputStream fileInputStream = fileInputStreamSource.getInputStream()) {
            return tika.detect(fileInputStream);
        } catch (IOException exception) {
            throw new FileException(exception);
        }
    }

}
