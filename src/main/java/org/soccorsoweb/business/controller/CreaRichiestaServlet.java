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
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.soccorsoweb.data.DataLayer;
import org.soccorsoweb.data.DataException;
import org.soccorsoweb.data.dao.DescRichiestaDAO;
import org.soccorsoweb.data.dao.RichiestaDAO;
import org.soccorsoweb.framework.security.SecurityHelpers;
import org.soccorsoweb.model.DescRichiesta;
import org.soccorsoweb.model.Richiesta;
import org.soccorsoweb.model.enums.StatoRichiesta;

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

        if (!SecurityHelpers.isValidCsrfToken(request, request.getParameter("csrf"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            DataLayer dataLayer = (DataLayer) request.getAttribute("datalayer");
            RichiestaDAO richiestaDAO = (RichiestaDAO) dataLayer.getDAO(Richiesta.class);
            DescRichiestaDAO descRichiestaDAO = (DescRichiestaDAO) dataLayer.getDAO(DescRichiesta.class);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("nome", SecurityHelpers.sanitizeTextInput(request.getParameter("nome")));
            payload.put("email", SecurityHelpers.sanitizeTextInput(request.getParameter("email")));
            payload.put("posizione", SecurityHelpers.sanitizeTextInput(request.getParameter("posizione")));
            payload.put("descrizione", SecurityHelpers.sanitizeTextInput(request.getParameter("descrizione")));
            payload.put("coordinate", SecurityHelpers.sanitizeTextInput(request.getParameter("coordinate")));
            payload.put("foto", SecurityHelpers.sanitizeFileLink(saveUploadedFile(request)));
            payload.put("captcha", SecurityHelpers.sanitizeTextInput(request.getParameter("captcha")));

            String richiestaSignature = SecurityHelpers.buildRequestSignature(
                    String.valueOf(payload.getOrDefault("nome", "")),
                    String.valueOf(payload.getOrDefault("email", "")),
                    String.valueOf(payload.getOrDefault("posizione", "")),
                    String.valueOf(payload.getOrDefault("descrizione", "")),
                    String.valueOf(payload.getOrDefault("coordinate", "")),
                    String.valueOf(payload.getOrDefault("foto", ""))
            );
            if (isDuplicateRequest(richiestaDAO, richiestaSignature)) {
                request.getSession(true).setAttribute("ultima_richiesta", payload);
                response.sendRedirect(request.getContextPath() + "/home?success=richiesta-sent");
                return;
            }

            Richiesta richiesta = richiestaDAO.createRichiesta();
            richiesta.setNome((String) payload.get("nome"));
            richiesta.setEmail((String) payload.get("email"));
            richiesta.setIP(request.getRemoteAddr());
            richiesta.setStato(StatoRichiesta.IN_ATTESA);
            richiesta.setString(richiestaSignature);
            richiesta.setVerificato(false);
            richiesta.setData(LocalDateTime.now());
            richiestaDAO.storeRichiesta(richiesta);

            DescRichiesta dettaglio = descRichiestaDAO.createDescRichiesta();
            dettaglio.setPosizione((String) payload.get("posizione"));
            dettaglio.setFoto((String) payload.get("foto"));
            dettaglio.setDescrizione((String) payload.get("descrizione"));
            descRichiestaDAO.storeDescRichiesta(dettaglio, richiesta);

            request.getSession(true).setAttribute("ultima_richiesta", payload);
            response.sendRedirect(request.getContextPath() + "/home?success=richiesta-sent");
        } catch (IOException | DataException ex) {
            handleError(ex, request, response);
        }
    }

    private boolean isDuplicateRequest(RichiestaDAO richiestaDAO, String signature) throws DataException {
        List<Richiesta> richieste = richiestaDAO.getRichieste();
        for (Richiesta richiesta : richieste) {
            if (signature.equals(richiesta.getString())) {
                return true;
            }
        }
        return false;
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
