package org.soccorsoweb.business.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024
)
public class CreaRichiestaServlet extends SoccorsoBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("nome", safeTrim(request.getParameter("nome")));
            payload.put("email", safeTrim(request.getParameter("email")));
            payload.put("posizione", safeTrim(request.getParameter("posizione")));
            payload.put("descrizione", safeTrim(request.getParameter("descrizione")));
            payload.put("coordinate", resolveCoordinate(request));
            payload.put("foto", saveUploadedFile(request));
            payload.put("captcha", safeTrim(request.getParameter("captcha")));

            request.getSession(true).setAttribute("ultima_richiesta", payload);

            response.sendRedirect(request.getContextPath() + "/home?success=richiesta-sent");
        } catch (IOException ex) {
            handleError(ex, request, response);
        }
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String resolveCoordinate(HttpServletRequest request) {
        String coordinate = safeTrim(request.getParameter("coordinate"));
        if (!coordinate.isEmpty()) {
            return coordinate;
        }

        String lat = safeTrim(request.getParameter("lat"));
        String lng = safeTrim(request.getParameter("lng"));
        if (!lat.isEmpty() && !lng.isEmpty()) {
            return lat + ", " + lng;
        }

        return "";
    }
    
    private boolean hasFormSubmission(HttpServletRequest request) {
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase(Locale.ROOT).contains("multipart/form-data")) {
            return true;
        }

        return request.getParameter("nome") != null
                || request.getParameter("email") != null
                || request.getParameter("posizione") != null
                || request.getParameter("descrizione") != null;
    }

    private String saveUploadedFile(HttpServletRequest request) throws IOException, ServletException {
        Part filePart = request.getPart("foto");
        if (filePart == null || filePart.getSize() == 0L) {
            return "";
        }

        String submittedFileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        if (submittedFileName == null || submittedFileName.isBlank()) {
            return "";
        }

        String uploadDirPath = request.getServletContext().getRealPath("/uploads");
        if (uploadDirPath == null) {
            uploadDirPath = System.getProperty("java.io.tmpdir");
        }

        Path uploadDir = Paths.get(uploadDirPath);
        Files.createDirectories(uploadDir);

        String fileName = System.currentTimeMillis() + "_" + submittedFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path target = uploadDir.resolve(fileName);
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return "/uploads/" + fileName;
    }
}
