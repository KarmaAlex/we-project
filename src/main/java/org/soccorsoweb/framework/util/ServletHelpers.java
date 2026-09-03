package org.soccorsoweb.framework.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.soccorsoweb.framework.security.SecurityHelpers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

public class ServletHelpers {
        public static String saveUploadedFile(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("foto");
        if (filePart == null || filePart.getSize() == 0L) {
            return "";
        }

        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (submittedFileName == null || submittedFileName.isBlank()) {
            return "";
        }

        String servletPath = request.getServletContext() != null && request.getServletContext().getRealPath("/") != null
                ? request.getServletContext().getRealPath("/")
                : ".";
        Path uploadDir = Paths.get(servletPath, "uploads");
        Files.createDirectories(uploadDir);

        String fileName = System.currentTimeMillis() + "_" + SecurityHelpers.sanitizeFilename(submittedFileName);
        Path target = uploadDir.resolve(fileName);
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + fileName;
    }
    

}
