package com.njit.takeoutsystem.utils;

import java.nio.file.Files;
import java.nio.file.Path;

public final class UploadPathUtil {
    private UploadPathUtil() {
    }

    public static Path uploadRoot() {
        Path workDir = Path.of("").toAbsolutePath();
        if ("backend".equals(workDir.getFileName().toString())) {
            Path projectUploads = workDir.getParent().resolve("uploads");
            if (Files.isDirectory(projectUploads)) {
                return projectUploads;
            }
            return workDir.resolve("uploads");
        }
        Path uploads = workDir.resolve("uploads");
        if (Files.isDirectory(uploads)) {
            return uploads;
        }
        if (Files.isDirectory(workDir.resolve("backend"))) {
            return workDir.resolve("backend").resolve("uploads");
        }
        return uploads;
    }

    public static Path dishUploadDirectory() {
        return uploadRoot().resolve("dishes");
    }
}
