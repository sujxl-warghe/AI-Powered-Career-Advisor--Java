package com.smartcareer.service;

import com.smartcareer.model.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class CareerAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(CareerAnalysisService.class);

    private final DocumentParserService documentParser;
    private final SkillExtractorService skillExtractor;
    private final GroqService groqService;

    public CareerAnalysisService(DocumentParserService documentParser,
                                  SkillExtractorService skillExtractor,
                                  GroqService groqService) {
        this.documentParser = documentParser;
        this.skillExtractor = skillExtractor;
        this.groqService = groqService;
    }

    /**
     * Main analysis: parse documents, extract skills, compute match.
     */
    public AnalysisResult analyze(MultipartFile resumeFile, String jdText, MultipartFile jdFile) throws IOException {
        // --- Parse resume ---
        String resumeText = documentParser.extractText(resumeFile);

        // --- Parse JD (file or pasted text) ---
        String resolvedJdText;
        if (jdFile != null && !jdFile.isEmpty()) {
            resolvedJdText = documentParser.extractText(jdFile);
        } else if (jdText != null && !jdText.isBlank()) {
            resolvedJdText = jdText;
        } else {
            throw new IllegalArgumentException("Please provide a job description (paste text or upload a file).");
        }

        // --- Extract skills ---
        List<String> resumeSkills = skillExtractor.extractSkills(resumeText);
        List<String> jdSkills = skillExtractor.extractSkills(resolvedJdText);

        // --- Compute match ---
        Set<String> resumeSet = new HashSet<>(resumeSkills);
        Set<String> jdSet = new HashSet<>(jdSkills);

        Set<String> matchedSkills = new TreeSet<>(resumeSet);
        matchedSkills.retainAll(jdSet);

        Set<String> missingSkills = new TreeSet<>(jdSet);
        missingSkills.removeAll(resumeSet);

        Set<String> extraSkills = new TreeSet<>(resumeSet);
        extraSkills.removeAll(jdSet);

        double matchScore = jdSet.isEmpty() ? 0.0 : (matchedSkills.size() * 100.0 / jdSet.size());

        // --- Learning resources for missing skills ---
        Map<String, String> learningResources = skillExtractor.getLearningResources(missingSkills);

        // --- Build result ---
        AnalysisResult result = new AnalysisResult();
        result.setResumeText(resumeText);
        result.setJdText(resolvedJdText);
        result.setResumeSkills(resumeSkills);
        result.setJdSkills(jdSkills);
        result.setMatchedSkills(matchedSkills);
        result.setMissingSkills(missingSkills);
        result.setExtraSkills(extraSkills);
        result.setMatchScore(matchScore);
        result.setResumeWordCount(wordCount(resumeText));
        result.setJdWordCount(wordCount(resolvedJdText));
        result.setResumeFileName(resumeFile.getOriginalFilename());
        result.setJdFileName(jdFile != null && !jdFile.isEmpty() ? jdFile.getOriginalFilename() : "Pasted text");
        result.setLearningResources(learningResources);

        log.info("Analysis complete: matchScore={}%, matched={}, missing={}",
                String.format("%.1f", matchScore), matchedSkills.size(), missingSkills.size());

        return result;
    }

    /**
     * Generate AI resume enhancements via Groq.
     */
    public String getResumeEnhancements(AnalysisResult result) {
        return groqService.enhanceResume(result.getResumeText(), result.getJdText(), result.getMissingSkills());
    }

    /**
     * Generate AI project ideas via Groq.
     */
    public String getProjectIdeas(AnalysisResult result) {
        return groqService.generateProjectIdeas(result.getResumeText(), result.getResumeSkills());
    }

    /**
     * Generate career guidance via Groq.
     */
    public String getCareerGuidance(AnalysisResult result) {
        return groqService.generateCareerGuidance(
                result.getResumeText(), result.getJdText(),
                result.getMatchedSkills(), result.getMissingSkills(),
                result.getMatchScore());
    }

    /**
     * Generate learning roadmap via Groq.
     */
    public String getLearningRoadmap(AnalysisResult result) {
        return groqService.generateLearningRoadmap(result.getMissingSkills(), result.getResumeText());
    }

    private int wordCount(String text) {
        if (text == null || text.isBlank()) return 0;
        return text.trim().split("\\s+").length;
    }
}
