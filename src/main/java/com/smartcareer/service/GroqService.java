package com.smartcareer.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Service
public class GroqService {

    private static final Logger log = LoggerFactory.getLogger(GroqService.class);
    private static final MediaType JSON_MEDIA = MediaType.get("application/json; charset=utf-8");

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GroqService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Enhance resume section to better match a job description, addressing missing skills.
     */
    public String enhanceResume(String resumeText, String jdText, Collection<String> missingSkills) {
        String prompt = String.format("""
                You are an expert career coach and resume writer. Given the candidate's resume, the job description, \
                and the missing skills, provide detailed, actionable suggestions to improve the resume.
                
                Provide:
                1. **Summary Enhancement** - Improved professional summary targeting this role
                2. **Skills to Highlight** - How to better present existing skills
                3. **Missing Skills Plan** - How to address each missing skill (courses, projects, certifications)
                4. **Bullet Point Rewrites** - Improved achievement-focused bullet points (give 3–5 examples)
                5. **Keywords to Add** - ATS-friendly keywords from the JD
                
                Resume:
                %s
                
                Job Description:
                %s
                
                Missing Skills: %s
                
                Please provide specific, actionable improvements:
                """,
                truncate(resumeText, 2000),
                truncate(jdText, 1500),
                String.join(", ", missingSkills));

        return callGroq(prompt, 1500);
    }

    /**
     * Generate personalized project ideas based on resume and skills.
     */
    public String generateProjectIdeas(String resumeText, Collection<String> skills) {
        String prompt = String.format("""
                You are a senior software engineering mentor. Based on the candidate's resume and skills, \
                suggest 3 impactful, portfolio-worthy project ideas that tackle real-world problems.
                
                For each project, provide:
                - **Project Title**: A catchy name
                - **Problem Solved**: The real-world problem it addresses
                - **Tech Stack**: Specific technologies to use (aligned with their skills)
                - **Key Features**: 3–4 main features to implement
                - **Why It Impresses Recruiters**: What makes this project stand out
                - **GitHub Tips**: How to document and present it
                
                Resume Summary:
                %s
                
                Candidate Skills: %s
                
                Generate 3 diverse project ideas (avoid generic CRUD apps; make them industry-relevant):
                """,
                truncate(resumeText, 2000),
                String.join(", ", skills));

        return callGroq(prompt, 1500);
    }

    /**
     * Provide comprehensive career guidance based on resume and target JD.
     */
    public String generateCareerGuidance(String resumeText, String jdText,
                                          Collection<String> matchedSkills, Collection<String> missingSkills,
                                          double matchScore) {
        String prompt = String.format("""
                You are an expert career advisor. Based on the resume, job description, and skill analysis below, \
                provide comprehensive, personalized career guidance.
                
                Skill Match Score: %.1f%%
                Matched Skills: %s
                Missing Skills: %s
                
                Provide:
                1. **Overall Assessment** - Honest evaluation of fit for this role
                2. **Immediate Actions (0–30 days)** - What to do right now
                3. **Short-term Goals (1–3 months)** - Skills to acquire and how
                4. **Application Strategy** - How to position yourself for this role
                5. **Interview Preparation** - Key topics to prepare for this role
                6. **Alternative Roles** - Similar roles that might be a better fit now
                
                Resume Summary:
                %s
                
                Job Description:
                %s
                """,
                matchScore,
                String.join(", ", matchedSkills),
                String.join(", ", missingSkills),
                truncate(resumeText, 1500),
                truncate(jdText, 1000));

        return callGroq(prompt, 1500);
    }

    /**
     * Generate a personalized learning roadmap for missing skills.
     */
    public String generateLearningRoadmap(Collection<String> missingSkills, String resumeText) {
        if (missingSkills.isEmpty()) {
            return "🎉 **Great news!** You already have all the required skills for this position. Focus on deepening your expertise and building impressive projects to stand out.";
        }

        String prompt = String.format("""
                You are a learning path expert and mentor. Create a structured, prioritized learning roadmap \
                for a candidate who needs to acquire these skills: %s
                
                Context from their resume:
                %s
                
                Provide:
                1. **Priority Order** - Which skills to learn first and why
                2. **Week-by-Week Plan** - A realistic 8-week learning schedule
                3. **Best Resources** - Top 2 free resources per skill (courses, docs, YouTube channels)
                4. **Practice Projects** - Mini-projects to build while learning
                5. **Milestone Checkpoints** - How to know when you've mastered each skill
                6. **Time Estimate** - Realistic hours needed for each skill
                
                Skills to learn: %s
                """,
                String.join(", ", missingSkills),
                truncate(resumeText, 1000),
                String.join(", ", missingSkills));

        return callGroq(prompt, 1500);
    }

    private String callGroq(String userMessage, int maxTokens) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("YOUR_GROQ_API_KEY")) {
            log.warn("Groq API key not configured");
            return "⚠️ **Groq API key not configured.** Please set your `groq.api.key` in `application.properties` and restart the application.\n\nGet your free API key at: https://console.groq.com";
        }

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("max_tokens", maxTokens);
            requestBody.put("temperature", 0.7);

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode message = messages.addObject();
            message.put("role", "user");
            message.put("content", userMessage);

            String json = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(json, JSON_MEDIA))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "No response body";
                    log.error("Groq API error {}: {}", response.code(), errorBody);
                    return String.format("⚠️ **Groq API Error (HTTP %d).** Please check your API key and try again.\n\nDetails: %s",
                            response.code(), errorBody);
                }

                String responseBody = response.body().string();
                JsonNode responseJson = objectMapper.readTree(responseBody);
                String content = responseJson
                        .path("choices")
                        .path(0)
                        .path("message")
                        .path("content")
                        .asText("");

                if (content.isBlank()) {
                    return "⚠️ Received an empty response from Groq. Please try again.";
                }

                log.debug("Groq response length: {} chars", content.length());
                return content;
            }

        } catch (IOException e) {
            log.error("Failed to call Groq API", e);
            return "⚠️ **Connection Error:** Could not reach the Groq API. Please check your internet connection and try again.\n\nError: " + e.getMessage();
        } catch (Exception e) {
            log.error("Unexpected error calling Groq", e);
            return "⚠️ **Unexpected Error:** " + e.getMessage();
        }
    }

    private String truncate(String text, int maxChars) {
        if (text == null) return "";
        return text.length() > maxChars ? text.substring(0, maxChars) + "..." : text;
    }
}
