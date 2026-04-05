package com.smartcareer.model;

import java.util.List;
import java.util.Set;

public class AnalysisResult {

    private String resumeText;
    private String jdText;
    private List<String> resumeSkills;
    private List<String> jdSkills;
    private Set<String> matchedSkills;
    private Set<String> missingSkills;
    private Set<String> extraSkills;
    private double matchScore;
    private int resumeWordCount;
    private int jdWordCount;
    private String resumeFileName;
    private String jdFileName;

    // AI-generated content
    private String resumeEnhancements;
    private String projectIdeas;
    private String careerGuidance;
    private String learningRoadmap;

    // Learning resources: skill -> url
    private java.util.Map<String, String> learningResources;

    public AnalysisResult() {}

    public String getResumeText() { return resumeText; }
    public void setResumeText(String resumeText) { this.resumeText = resumeText; }

    public String getJdText() { return jdText; }
    public void setJdText(String jdText) { this.jdText = jdText; }

    public List<String> getResumeSkills() { return resumeSkills; }
    public void setResumeSkills(List<String> resumeSkills) { this.resumeSkills = resumeSkills; }

    public List<String> getJdSkills() { return jdSkills; }
    public void setJdSkills(List<String> jdSkills) { this.jdSkills = jdSkills; }

    public Set<String> getMatchedSkills() { return matchedSkills; }
    public void setMatchedSkills(Set<String> matchedSkills) { this.matchedSkills = matchedSkills; }

    public Set<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(Set<String> missingSkills) { this.missingSkills = missingSkills; }

    public Set<String> getExtraSkills() { return extraSkills; }
    public void setExtraSkills(Set<String> extraSkills) { this.extraSkills = extraSkills; }

    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    public int getResumeWordCount() { return resumeWordCount; }
    public void setResumeWordCount(int resumeWordCount) { this.resumeWordCount = resumeWordCount; }

    public int getJdWordCount() { return jdWordCount; }
    public void setJdWordCount(int jdWordCount) { this.jdWordCount = jdWordCount; }

    public String getResumeFileName() { return resumeFileName; }
    public void setResumeFileName(String resumeFileName) { this.resumeFileName = resumeFileName; }

    public String getJdFileName() { return jdFileName; }
    public void setJdFileName(String jdFileName) { this.jdFileName = jdFileName; }

    public String getResumeEnhancements() { return resumeEnhancements; }
    public void setResumeEnhancements(String resumeEnhancements) { this.resumeEnhancements = resumeEnhancements; }

    public String getProjectIdeas() { return projectIdeas; }
    public void setProjectIdeas(String projectIdeas) { this.projectIdeas = projectIdeas; }

    public String getCareerGuidance() { return careerGuidance; }
    public void setCareerGuidance(String careerGuidance) { this.careerGuidance = careerGuidance; }

    public String getLearningRoadmap() { return learningRoadmap; }
    public void setLearningRoadmap(String learningRoadmap) { this.learningRoadmap = learningRoadmap; }

    public java.util.Map<String, String> getLearningResources() { return learningResources; }
    public void setLearningResources(java.util.Map<String, String> learningResources) { this.learningResources = learningResources; }

    public String getMatchCategory() {
        if (matchScore >= 80) return "excellent";
        if (matchScore >= 60) return "good";
        if (matchScore >= 40) return "moderate";
        if (matchScore >= 20) return "low";
        return "very-low";
    }

    public String getMatchLabel() {
        if (matchScore >= 80) return "🌟 Excellent Match";
        if (matchScore >= 60) return "✅ Good Match";
        if (matchScore >= 40) return "⚡ Moderate Match";
        if (matchScore >= 20) return "🔄 Low Match";
        return "🎯 Growth Opportunity";
    }
}
