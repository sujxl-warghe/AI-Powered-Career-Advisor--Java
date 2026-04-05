# 🎯 Smart Career Advisor — Java Spring Boot

AI-powered career advisor that analyzes your resume against a job description, identifies skill gaps, and generates personalized recommendations using **Groq LLaMA AI**.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- A free [Groq API key](https://console.groq.com)

### 1. Configure your Groq API key

Open `src/main/resources/application.properties` and replace:

```properties
groq.api.key=YOUR_GROQ_API_KEY
```

Get your free key at: https://console.groq.com

### 2. Run the application

```bash
mvn spring-boot:run
```

### 3. Open in browser

```
http://localhost:8080
```

---

## ✨ Features

| Feature | Description |
|---|---|
| 📄 Resume Parsing | PDF, DOCX, TXT support |
| 🔍 Skill Extraction | 200+ tech skills detected automatically |
| 📊 Job Match Score | Percentage match with matched/missing/extra skill breakdown |
| 📚 Learning Resources | Curated course links for every missing skill |
| 🤖 Resume Enhancement | Groq AI rewrites your resume to target the job |
| 💡 Project Ideas | 3 personalized, portfolio-worthy project concepts |
| 🗺️ Career Guidance | Personalized application and interview strategy |
| 📅 Learning Roadmap | 8-week skill acquisition plan |

---

## 🗂️ Project Structure

```
smart-career-advisor/
├── pom.xml
└── src/main/
    ├── java/com/smartcareer/
    │   ├── SmartCareerAdvisorApplication.java    ← Entry point
    │   ├── controller/
    │   │   └── MainController.java               ← All routes
    │   ├── service/
    │   │   ├── GroqService.java                  ← Groq API integration
    │   │   ├── CareerAnalysisService.java         ← Orchestration
    │   │   ├── DocumentParserService.java         ← PDF/DOCX/TXT parsing
    │   │   └── SkillExtractorService.java         ← Skill detection + resources
    │   ├── model/
    │   │   └── AnalysisResult.java               ← Data model
    │   └── config/
    │       ├── MarkdownUtils.java                ← Markdown → HTML
    │       └── ThymeleafConfig.java
    └── resources/
        ├── application.properties
        ├── templates/
        │   ├── index.html                        ← Upload page
        │   └── results.html                      ← Results page
        └── static/
            ├── css/style.css
            └── js/
                ├── app.js
                └── results.js
```

---

## ⚙️ Configuration

`src/main/resources/application.properties`:

```properties
# Required: your Groq API key
groq.api.key=YOUR_GROQ_API_KEY

# Groq model (llama3-8b-8192 is fast and free)
groq.model=llama3-8b-8192

# Server port
server.port=8080

# Max file upload size
spring.servlet.multipart.max-file-size=20MB
```

### Other available Groq models
- `llama3-8b-8192` (default — fast)
- `llama3-70b-8192` (more capable)
- `mixtral-8x7b-32768` (large context)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Frontend | Thymeleaf, HTML5, CSS3, Vanilla JS |
| Build | Maven |
| PDF Parsing | Apache PDFBox 3 |
| DOCX Parsing | Apache POI 5 |
| HTTP Client | OkHttp 4 |
| AI | Groq API (LLaMA 3) |

---

## 🔒 Privacy

All document processing happens locally on your machine. Files are never stored — they are parsed in memory and discarded after the session. Only the extracted text is sent to the Groq API for AI recommendations.

---

## 🐛 Troubleshooting

**"Groq API key not configured"** → Set `groq.api.key` in `application.properties`

**Port 8080 already in use** → Change `server.port=8081` in `application.properties`

**PDF parsing fails** → Ensure the PDF is not password-protected or image-only (scanned)

**Maven build errors** → Ensure Java 17+ is installed: `java -version`
