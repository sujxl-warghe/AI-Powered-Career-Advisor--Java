package com.smartcareer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class SmartCareerAdvisorApplication {
    public static void main(String[] args) {
        // Load .env file if present
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String apiKey = dotenv.get("GROQ_API_KEY");
        if (apiKey != null && !apiKey.isBlank()) {
            System.setProperty("groq.api.key", apiKey);
        }

        SpringApplication.run(SmartCareerAdvisorApplication.class, args);
    }
}
