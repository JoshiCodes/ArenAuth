package de.joshicodes.aren.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.flywaydb.core.internal.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Logger;

@ApplicationScoped
public class UploadService {

    private final Logger LOG = Logger.getLogger("UploadService");

    @ConfigProperty(name = "aren.upload-dir")
    public String UPLOAD_DIR_NAME;
    private File UPLOAD_DIR;

    // Erlaubte Dateitypen und maximale Größe
    private final String[] ALLOWED_MIME_TYPES = {
            "image/png", "image/jpeg", "image/jpg", "image/webp"
    };
    private final long MAX_FILE_SIZE_DEFAULT = 5 * 1024 * 1024; // 5MB

    @ConfigProperty(name = "aren.max-upload-size", defaultValue = "5242880")
    long MAX_UPLOAD_SIZE;

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
            UPLOAD_DIR = new File(UPLOAD_DIR_NAME);
            if(!UPLOAD_DIR.exists()) {
                UPLOAD_DIR.mkdirs();
            }
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

        long uploadedSize = writeFileWithSizeCheck(inputStream, targetFile);

        LOG.info("File uploaded successfully: " + uniqueFileName + " (" + uploadedSize + " bytes) for " + uploadType);

        return id;
    }

    /**
     * Schreibt eine Datei und prüft die Größe während des Uploads
     */
    private long writeFileWithSizeCheck(InputStream inputStream, File targetFile)
            throws IOException {
        long totalSize = 0;
        byte[] buffer = new byte[8192];
        int read;

        try (FileOutputStream fos = new FileOutputStream(targetFile)) {
            while ((read = inputStream.read(buffer)) != -1) {
                totalSize += read;

                // Prüfe, ob maximale Größe überschritten wurde
                if (totalSize > MAX_UPLOAD_SIZE) {
                    targetFile.delete();
                    throw new IOException(
                            "File is too big. Maximum size: " + MAX_UPLOAD_SIZE + " bytes"
                    );
                }

                fos.write(buffer, 0, read);
            }
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

    public Pair<byte[], String> getFile(UploadType uploadType, String fileId) throws IOException {

        if(UPLOAD_DIR == null) {
            UPLOAD_DIR = new File(UPLOAD_DIR_NAME);
            if(!UPLOAD_DIR.exists()) {
                return null;
            }
        }

        File typeDir = new File(UPLOAD_DIR, uploadType.getDirName());

        try {
            // Finde die Datei mit der fileId (unabhängig von der Endung)
            File[] files = typeDir.listFiles((dir, name) -> name.startsWith(fileId + "."));

            if (files == null || files.length == 0) {
                return null;
            }

            File file = files[0];

            // Sicherheitsprüfung - verhindere Path Traversal
            if (!file.getCanonicalPath().startsWith(typeDir.getCanonicalPath())) {
                return null;
            }

            final String ending = file.getName().substring(file.getName().lastIndexOf(".") + 1);
            final String mimeType = toMimeType(ending);
            if(mimeType == null) {
                return null;
            }

            return Pair.of(
                    Files.readAllBytes(file.toPath()),
                    mimeType
            );
        } catch (IOException e) {
            return null;
        }
    }

    public boolean deleteFile(UploadType uploadType, String fileId) {

        if(UPLOAD_DIR == null) {
            return false;
        }

        File typeDir = new File(UPLOAD_DIR, uploadType.getDirName());

        try {
            // Finde die Datei mit der fileId (unabhängig von der Endung)
            File[] files = typeDir.listFiles((dir, name) -> name.startsWith(fileId + "."));

            if (files == null || files.length == 0) {
                return false;
            }

            File fileToDelete = files[0];

            // Sicherheitsprüfung - verhindere Path Traversal
            if (!fileToDelete.getCanonicalPath().startsWith(typeDir.getCanonicalPath())) {
                return false;
            }

            // Lösche die Datei
            boolean deleted = fileToDelete.delete();

            if (deleted) {
            } else {
            }

            return deleted;
        } catch (IOException e) {
            return false;
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
