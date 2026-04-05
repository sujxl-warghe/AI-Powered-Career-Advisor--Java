package com.smartcareer.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentParserService {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserService.class);

    public String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("File has no name");
        }

        String lower = filename.toLowerCase();

        if (lower.endsWith(".pdf")) {
            return extractFromPdf(file);
        } else if (lower.endsWith(".docx")) {
            return extractFromDocx(file);
        } else if (lower.endsWith(".txt")) {
            return extractFromTxt(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + filename
                    + ". Supported types: PDF, DOCX, TXT");
        }
    }

    private String extractFromPdf(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             PDDocument doc = Loader.loadPDF(new RandomAccessReadBuffer(is))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(doc);
            log.debug("Extracted {} chars from PDF: {}", text.length(), file.getOriginalFilename());
            return text;
        } catch (Exception e) {
            log.error("Failed to parse PDF: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to read PDF file: " + e.getMessage(), e);
        }
    }

    private String extractFromDocx(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream();
             XWPFDocument doc = new XWPFDocument(is)) {
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            String text = paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .filter(t -> t != null && !t.isBlank())
                    .collect(Collectors.joining("\n"));
            log.debug("Extracted {} chars from DOCX: {}", text.length(), file.getOriginalFilename());
            return text;
        } catch (Exception e) {
            log.error("Failed to parse DOCX: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to read DOCX file: " + e.getMessage(), e);
        }
    }

    private String extractFromTxt(MultipartFile file) throws IOException {
        try {
            byte[] bytes = file.getBytes();
            String text = new String(bytes, StandardCharsets.UTF_8);
            log.debug("Extracted {} chars from TXT: {}", text.length(), file.getOriginalFilename());
            return text;
        } catch (Exception e) {
            log.error("Failed to parse TXT: {}", file.getOriginalFilename(), e);
            throw new IOException("Failed to read TXT file: " + e.getMessage(), e);
        }
    }
}
