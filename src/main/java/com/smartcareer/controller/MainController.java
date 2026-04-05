package com.smartcareer.controller;

import com.smartcareer.config.MarkdownUtils;
import com.smartcareer.model.AnalysisResult;
import com.smartcareer.service.CareerAnalysisService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MainController {

    private static final Logger log = LoggerFactory.getLogger(MainController.class);
    private static final String SESSION_RESULT = "analysisResult";

    private final CareerAnalysisService analysisService;

    public MainController(CareerAnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    // ── Home page ──────────────────────────────────────────────────────────────
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // ── Analyze ────────────────────────────────────────────────────────────────
    @PostMapping("/analyze")
    public String analyze(
            @RequestParam("resumeFile") MultipartFile resumeFile,
            @RequestParam(value = "jdText", required = false) String jdText,
            @RequestParam(value = "jdFile", required = false) MultipartFile jdFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            if (resumeFile == null || resumeFile.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please upload your resume file.");
                return "redirect:/";
            }

            boolean hasJdText = jdText != null && !jdText.isBlank();
            boolean hasJdFile = jdFile != null && !jdFile.isEmpty();

            if (!hasJdText && !hasJdFile) {
                redirectAttributes.addFlashAttribute("error", "Please provide a job description (paste text or upload a file).");
                return "redirect:/";
            }

            log.info("Starting analysis for resume: {}", resumeFile.getOriginalFilename());
            AnalysisResult result = analysisService.analyze(resumeFile, jdText, jdFile);
            session.setAttribute(SESSION_RESULT, result);
            return "redirect:/results";

        } catch (IllegalArgumentException e) {
            log.warn("Validation error: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/";
        } catch (Exception e) {
            log.error("Analysis failed", e);
            redirectAttributes.addFlashAttribute("error", "Analysis failed: " + e.getMessage());
            return "redirect:/";
        }
    }

    // ── Results page ───────────────────────────────────────────────────────────
    @GetMapping("/results")
    public String results(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        AnalysisResult result = (AnalysisResult) session.getAttribute(SESSION_RESULT);
        if (result == null) {
            redirectAttributes.addFlashAttribute("error", "No analysis found. Please upload your documents first.");
            return "redirect:/";
        }
        model.addAttribute("result", result);
        return "results";
    }

    // ── AI: Resume Enhancements ────────────────────────────────────────────────
    @PostMapping("/ai/resume-enhance")
    public String resumeEnhance(HttpSession session, RedirectAttributes redirectAttributes) {
        AnalysisResult result = getResult(session, redirectAttributes);
        if (result == null) return "redirect:/";
        try {
            String raw = analysisService.getResumeEnhancements(result);
            result.setResumeEnhancements(MarkdownUtils.toHtml(raw));
            session.setAttribute(SESSION_RESULT, result);
        } catch (Exception e) {
            log.error("Resume enhancement failed", e);
            result.setResumeEnhancements("<p>⚠️ Failed to generate enhancements: " + e.getMessage() + "</p>");
            session.setAttribute(SESSION_RESULT, result);
        }
        return "redirect:/results#ai-section";
    }

    // ── AI: Project Ideas ──────────────────────────────────────────────────────
    @PostMapping("/ai/project-ideas")
    public String projectIdeas(HttpSession session, RedirectAttributes redirectAttributes) {
        AnalysisResult result = getResult(session, redirectAttributes);
        if (result == null) return "redirect:/";
        try {
            String raw = analysisService.getProjectIdeas(result);
            result.setProjectIdeas(MarkdownUtils.toHtml(raw));
            session.setAttribute(SESSION_RESULT, result);
        } catch (Exception e) {
            log.error("Project ideas generation failed", e);
            result.setProjectIdeas("<p>⚠️ Failed to generate project ideas: " + e.getMessage() + "</p>");
            session.setAttribute(SESSION_RESULT, result);
        }
        return "redirect:/results#ai-section";
    }

    // ── AI: Career Guidance ────────────────────────────────────────────────────
    @PostMapping("/ai/career-guidance")
    public String careerGuidance(HttpSession session, RedirectAttributes redirectAttributes) {
        AnalysisResult result = getResult(session, redirectAttributes);
        if (result == null) return "redirect:/";
        try {
            String raw = analysisService.getCareerGuidance(result);
            result.setCareerGuidance(MarkdownUtils.toHtml(raw));
            session.setAttribute(SESSION_RESULT, result);
        } catch (Exception e) {
            log.error("Career guidance generation failed", e);
            result.setCareerGuidance("<p>⚠️ Failed to generate guidance: " + e.getMessage() + "</p>");
            session.setAttribute(SESSION_RESULT, result);
        }
        return "redirect:/results#ai-section";
    }

    // ── AI: Learning Roadmap ───────────────────────────────────────────────────
    @PostMapping("/ai/learning-roadmap")
    public String learningRoadmap(HttpSession session, RedirectAttributes redirectAttributes) {
        AnalysisResult result = getResult(session, redirectAttributes);
        if (result == null) return "redirect:/";
        try {
            String raw = analysisService.getLearningRoadmap(result);
            result.setLearningRoadmap(MarkdownUtils.toHtml(raw));
            session.setAttribute(SESSION_RESULT, result);
        } catch (Exception e) {
            log.error("Learning roadmap generation failed", e);
            result.setLearningRoadmap("<p>⚠️ Failed to generate roadmap: " + e.getMessage() + "</p>");
            session.setAttribute(SESSION_RESULT, result);
        }
        return "redirect:/results#ai-section";
    }

    // ── Reset ──────────────────────────────────────────────────────────────────
    @PostMapping("/reset")
    public String reset(HttpSession session) {
        session.removeAttribute(SESSION_RESULT);
        return "redirect:/";
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private AnalysisResult getResult(HttpSession session, RedirectAttributes ra) {
        AnalysisResult result = (AnalysisResult) session.getAttribute(SESSION_RESULT);
        if (result == null && ra != null) {
            ra.addFlashAttribute("error", "Session expired. Please re-upload your documents.");
        }
        return result;
    }
}
