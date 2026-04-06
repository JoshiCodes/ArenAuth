package de.joshicodes.aren.service;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.internal.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.UUID;

@ApplicationScoped
public class UploadService {

    private static final Logger LOG = LoggerFactory.getLogger(UploadService.class);

    @ConfigProperty(name = "aren.upload-dir")
    public String UPLOAD_DIR_NAME;
    private File UPLOAD_DIR;

    private final String[] ALLOWED_MIME_TYPES = {
            "image/png", "image/jpeg", "image/jpg", "image/webp"
    };
    private final long MAX_FILE_SIZE_DEFAULT = 5 * 1024 * 1024; // 5MB

    @ConfigProperty(name = "aren.max-upload-size", defaultValue = "5242880")
    long MAX_UPLOAD_SIZE;

    @PostConstruct
    void init() {
        if(UPLOAD_DIR == null) {
            UPLOAD_DIR = new File(UPLOAD_DIR_NAME);
            if(!UPLOAD_DIR.exists()) {
                UPLOAD_DIR.mkdirs();
            }
        }
    }

    /**
     * Uploads a file and returns the filename
     * @param uploadType Type of upload (USER_AVATAR or PROJECT_AVATAR)
     * @param inputStream The file input stream
     * @param mimeType The mime type of the file
     * @param fileName Original filename (optional)
     * @return Generated filename or throws exception
     */
    public String uploadFile(final UploadType uploadType, final InputStream inputStream,
                             final String mimeType, final String fileName) throws IOException {

        System.out.println(uploadType + " : " + fileName + " : " + mimeType);

        if(UPLOAD_DIR == null) {
            return null;
        }

        if (!isAllowedMimeType(mimeType)) {
            System.out.println(mimeType);
            throw new IllegalArgumentException(
                    "MIME-Type not allowed: " + mimeType +
                            ". Allowed types: " + String.join(", ", ALLOWED_MIME_TYPES)
            );
        }

        File typeDir = new File(UPLOAD_DIR, uploadType.getDirName());
        if (!typeDir.exists()) {
            typeDir.mkdirs();
        }

        String fileExtension = getFileExtension(mimeType);
        if(fileExtension == null) {
            System.out.println(fileExtension);
            throw new IllegalArgumentException(
                    "Invalid file extension!"
            );
        }
        final String id = UUID.randomUUID().toString();
        String uniqueFileName = id + "." + fileExtension;
        File targetFile = new File(typeDir, uniqueFileName);

        long uploadedSize = writeFileWithSizeCheck(inputStream, targetFile, mimeType);

        LOG.info("File uploaded successfully: " + uniqueFileName + " (" + uploadedSize + " bytes) for " + uploadType);

        return id;
    }

    /**
     * Schreibt eine Datei und prüft die Größe während des Uploads
     */
    private long writeFileWithSizeCheck(InputStream inputStream, File targetFile, String mimeType)
            throws IOException {
        long totalSize = 0;
        byte[] buffer = new byte[8192];
        int read;

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            while ((read = inputStream.read(buffer)) != -1) {
                totalSize += read;

                if (totalSize > MAX_UPLOAD_SIZE) {
                    targetFile.delete();
                    throw new IOException(
                            "File is too big. Maximum size: " + MAX_UPLOAD_SIZE + " bytes"
                    );
                }

                fos.write(buffer, 0, read);
            }
        }

        if(targetFile.exists()) {
            byte[] bytes = Files.readAllBytes(targetFile.toPath());
            File resizedFile = new File(targetFile.getParent(), targetFile.getName().replace(".", "_512."));

            bytes = resizeImage(bytes, 512, mimeType);
            Files.write(resizedFile.toPath(), bytes);
            LOG.info("File uploaded successfully: {} ({} bytes)", resizedFile.getName(), totalSize);
        }

        return totalSize;
    }

    /**
     * Überprüft, ob der MIME-Typ erlaubt ist
     */
    private boolean isAllowedMimeType(String mimeType) {
        if (mimeType == null) {
            return false;
        }

        for (String allowed : ALLOWED_MIME_TYPES) {
            if (mimeType.equalsIgnoreCase(allowed)) {
                return true;
            }
        }
        return false;
    }

    private String getFileExtension(String mimeType) {
        return switch (mimeType.toLowerCase()) {
            case "image/png" -> "png";
            case "image/jpeg", "image/jpg" -> "jpg";
            case "image/webp" -> "webp";
            default -> null;
        };
    }

    private String toMimeType(String extension) {
        return switch (extension.toLowerCase()) {
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "webp" -> "image/webp";
            default -> null;
        };
    }

    public Pair<byte[], String> getFile(UploadType uploadType, String fileId, Integer size) throws IOException {

        if(UPLOAD_DIR == null) {
            return null;
        }

        File typeDir = new File(UPLOAD_DIR, uploadType.getDirName());

        try {
            File[] files = typeDir.listFiles((dir, name) -> name.startsWith(fileId + "."));

            if (files == null || files.length == 0) {
                return null;
            }

            File file = files[0];

            if (!file.getCanonicalPath().startsWith(typeDir.getCanonicalPath())) {
                return null;
            }

            final String ending = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            final String mimeType = toMimeType(ending);
            if(mimeType == null) {
                return null;
            }

            File resizedFile = new File(typeDir, fileId + "_" + size + "." + ending);
            if(resizedFile.exists()) {
                LOG.info("Loading cached resized image: {}", resizedFile.getName());
                return Pair.of(Files.readAllBytes(resizedFile.toPath()), mimeType);
            }

            byte[] bytes = Files.readAllBytes(file.toPath());

            bytes = resizeImage(bytes, size, mimeType);
            Files.write(resizedFile.toPath(), bytes);
            LOG.info("Resized resized image: {}", resizedFile.getName());

            return Pair.of(
                    bytes,
                    mimeType
            );
        } catch (IOException e) {
            return null;
        }
    }

    public int deleteFile(UploadType uploadType, String fileId) {

        if(UPLOAD_DIR == null) {
            return 0;
        }

        File typeDir = new File(UPLOAD_DIR, uploadType.getDirName());

        try {
            File[] files = typeDir.listFiles((dir, name) -> name.startsWith(fileId + ".") || name.startsWith(fileId + "_"));

            if (files == null || files.length == 0) {
                return 0;
            }

            int deletedFiles = 0;
            for(File fileToDelete : files) {
                if (!fileToDelete.getCanonicalPath().startsWith(typeDir.getCanonicalPath())) {
                    return 0;
                }

                boolean deleted = fileToDelete.delete();

                if (deleted) {
                    LOG.info("File deleted: " + fileToDelete);
                    deletedFiles++;
                } else {
                    LOG.warn("Failed to delete file: " + fileToDelete);
                }
            }

            return deletedFiles;
        } catch (IOException e) {
            return 0;
        }
    }

    private byte[] resizeImage(byte[] imageBytes, Integer targetSize, String mimeType) throws IOException {

        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if(originalImage == null) {
            return imageBytes;
        }

        BufferedImage resizedImage = bicubicResize(originalImage, targetSize, targetSize);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String format = mimeType.equals("image/png") ? "png" : "jpg";
        ImageIO.write(resizedImage, format, baos);

        LOG.info("Image resized to {}x{} with Bicubic", targetSize, targetSize);

        return baos.toByteArray();
    }

    private BufferedImage bicubicResize(BufferedImage original, int newWidth, int newHeight) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        double scaleX = (double) original.getWidth() / newWidth;
        double scaleY = (double) original.getHeight() / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                double srcX = x * scaleX;
                double srcY = y * scaleY;

                int x0 = (int) Math.floor(srcX);
                int y0 = (int) Math.floor(srcY);

                double dx = srcX - x0;
                double dy = srcY - y0;

                int[][] kernel = new int[4][4];
                for (int ky = -1; ky <= 2; ky++) {
                    for (int kx = -1; kx <= 2; kx++) {
                        int px = Math.clamp(original.getWidth() - 1, 0, x0 + kx);
                        int py = Math.clamp(original.getHeight() - 1, 0, y0 + ky);
                        kernel[ky + 1][kx + 1] = original.getRGB(px, py);
                    }
                }

                int rgb = bicubicInterpolate(kernel, dx, dy);
                resized.setRGB(x, y, rgb);
            }
        }

        return resized;
    }

    private int bicubicInterpolate(int[][] kernel, double dx, double dy) {
        double r = 0, g = 0, b = 0;

        for (int ky = 0; ky < 4; ky++) {
            for (int kx = 0; kx < 4; kx++) {
                double wx = cubicWeight(dx - (kx - 1));
                double wy = cubicWeight(dy - (ky - 1));
                double weight = wx * wy;

                int rgb = kernel[ky][kx];
                r += ((rgb >> 16) & 0xFF) * weight;
                g += ((rgb >> 8) & 0xFF) * weight;
                b += (rgb & 0xFF) * weight;
            }
        }

        r = Math.clamp(r, 0, 255);
        g = Math.clamp(g, 0, 255);
        b = Math.clamp(b, 0, 255);

        return ((int)r << 16) | ((int)g << 8) | (int)b;
    }

    private double cubicWeight(double t) {
        t = Math.abs(t);
        if (t <= 1) {
            return 1 - 2*t*t + t*t*t;
        } else if (t < 2) {
            return -4 + 8*t - 5*t*t + t*t*t;
        } else {
            return 0;
        }
    }

    public static enum UploadType {
        USER_AVATAR("user-avatars"),
        PROJECT_AVATAR("project-avatars");

        private final String dirName;

        UploadType(String dirName) {
            this.dirName = dirName;
        }

        public String getDirName() {
            return dirName;
        }
    }

}
