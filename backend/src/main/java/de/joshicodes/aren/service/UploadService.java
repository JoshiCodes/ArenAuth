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

    @ConfigProperty(name = "aren.avatar-gradient.from", defaultValue = "#222222")
    String GRADIENT_FROM;

    @ConfigProperty(name = "aren.avatar-gradient.to", defaultValue = "#ffffff")
    String GRADIENT_TO;

    @ConfigProperty(name = "aren.avatar-initials.allowed-chars", defaultValue = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
    String ALLOWED_INITIALS_FOR_GENERATIONS;

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

        if(UPLOAD_DIR == null) {
            return null;
        }

        if (!isAllowedMimeType(mimeType)) {
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

        int newWidth = targetSize;
        int newHeight = targetSize;

        BufferedImage originalImage = ImageIO.read(new java.io.ByteArrayInputStream(imageBytes));
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

        java.awt.Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(scaledImage, 0, 0, null);
        g2d.dispose();

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        String format = mimeType.equals("image/png") ? "png" : "jpg";
        ImageIO.write(resizedImage, format, baos);

        LOG.info("Image resized to {}x{}.", targetSize, targetSize);

        return baos.toByteArray();
    }

    private Color parseHexColor(String hex) {
        String clean = hex.trim();
        if (clean.startsWith("#")) {
            clean = clean.substring(1);
        }
        if (clean.length() == 3) {
            clean = "" + clean.charAt(0) + clean.charAt(0)
                    + clean.charAt(1) + clean.charAt(1)
                    + clean.charAt(2) + clean.charAt(2);
        }
        if (clean.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        int r = Integer.parseInt(clean.substring(0, 2), 16);
        int g = Integer.parseInt(clean.substring(2, 4), 16);
        int b = Integer.parseInt(clean.substring(4, 6), 16);
        return new Color(r, g, b);
    }

    private Color randomColorInRange(Color min, Color max) {
        int rMin = Math.min(min.getRed(), max.getRed());
        int rMax = Math.max(min.getRed(), max.getRed());
        int gMin = Math.min(min.getGreen(), max.getGreen());
        int gMax = Math.max(min.getGreen(), max.getGreen());
        int bMin = Math.min(min.getBlue(), max.getBlue());
        int bMax = Math.max(min.getBlue(), max.getBlue());

        int r = rMin + (int) (Math.random() * (rMax - rMin + 1));
        int g = gMin + (int) (Math.random() * (gMax - gMin + 1));
        int b = bMin + (int) (Math.random() * (bMax - bMin + 1));

        return new Color(r, g, b);
    }



    private Character findInitial(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char ch = Character.toUpperCase(trimmed.charAt(i));
            if (ALLOWED_INITIALS_FOR_GENERATIONS.indexOf(ch) >= 0) {
                return ch;
            }
        }

        return null;
    }

    public Pair<byte[], String> getInitialAvatar(String name, int size) throws IOException {

        if (UPLOAD_DIR == null) {
            return null;
        }

        Character initialChar = findInitial(name);
        String initialKey = (initialChar != null) ? initialChar.toString() : "unknown";

        File defaultDir = new File(UPLOAD_DIR, "default-avatars");
        if (!defaultDir.exists()) {
            defaultDir.mkdirs();
        }

        final String extension = "png";
        final String mimeType = "image/png";
        File targetFile = new File(defaultDir, "default_" + initialKey + "." + extension);
        File resizedFile = new File(defaultDir, "default_" + initialKey + "_" + size + "." + extension);

        if (resizedFile.exists()) {
            byte[] existing = Files.readAllBytes(targetFile.toPath());
            return Pair.of(existing, mimeType);
        }

        if (targetFile.exists()) {
            byte[] existing = Files.readAllBytes(targetFile.toPath());
            byte[] resized = resizeImage(existing, size, mimeType);
            Files.write(resizedFile.toPath(), resized);
            return Pair.of(resized, mimeType);
        }

        int baseSize = size > 0 ? size : 512;
        BufferedImage image = new BufferedImage(baseSize, baseSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Color minColor = parseHexColor(GRADIENT_FROM != null ? GRADIENT_FROM : "#222222");
            Color maxColor = parseHexColor(GRADIENT_TO != null ? GRADIENT_TO : "#ffffff");

            Color c1 = randomColorInRange(minColor, maxColor);
            Color c2 = randomColorInRange(minColor, maxColor);

            GradientPaint gradient = new GradientPaint(
                    0, 0, c1,
                    baseSize, baseSize, c2
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, baseSize, baseSize);

            if (initialChar != null) {
                String text = initialChar.toString();
                int fontSize = (int) (baseSize * 0.6); // 60%
                Font font = new Font("SansSerif", Font.BOLD, fontSize);
                g2d.setFont(font);

                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();

                int x = (baseSize - textWidth) / 2;
                int y = (baseSize - fm.getHeight()) / 2 + fm.getAscent();

                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);
            } else {
                // No Initial found.
                String resourcePath = "/img/default_overlay.png";
                try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
                    if (in != null) {
                        BufferedImage overlay = ImageIO.read(in);
                        int overlaySize = (int) (baseSize * 0.8); // 80%
                        Image scaledOverlay = overlay.getScaledInstance(overlaySize, overlaySize, Image.SCALE_SMOOTH);
                        int x = (baseSize - overlaySize) / 2;
                        int y = (baseSize - overlaySize) / 2;
                        g2d.drawImage(scaledOverlay, x, y, null);
                    } else {
                        LOG.warn("Fallback default avatar resource not found at {}", resourcePath);
                    }
                }
            }

        } finally {
            g2d.dispose();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] pngBytes = baos.toByteArray();
        Files.write(targetFile.toPath(), pngBytes);

        byte[] finalBytes = resizeImage(pngBytes, size, mimeType);
        Files.write(resizedFile.toPath(), finalBytes);
        LOG.info("Generated initial avatar for '{}': {}", name, targetFile.getName());

        return Pair.of(finalBytes, mimeType);
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
