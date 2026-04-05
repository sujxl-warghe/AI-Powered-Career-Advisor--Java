package com.smartcareer.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class SkillExtractorService {

    private static final List<String> COMMON_SKILLS = Arrays.asList(
        // Programming Languages
        "python", "java", "c++", "c#", "go", "rust", "typescript", "javascript",
        "swift", "kotlin", "ruby", "php", "perl", "objective-c", "r", "matlab",
        "dart", "scala", "groovy", "lua", "visual basic", "assembly", "fortran",
        "cobol", "delphi", "abap", "sas",
        // Web & Frameworks
        "html", "html5", "css", "css3", "react", "reactjs", "react native",
        "angular", "angularjs", "vue", "vuejs", "svelte", "ember", "backbone",
        "jquery", "bootstrap", "tailwind", "material-ui", "redux", "webpack",
        "babel", "next.js", "nuxt.js", "meteor", "express", "expressjs",
        "node", "nodejs", "fastapi", "flask", "django", "spring", "spring boot",
        "struts", "grails", "laravel", "symfony", "cakephp", "zend", "codeigniter",
        "yii", "asp.net", "dotnet", "dotnet core", "rails", "ruby on rails",
        "phoenix", "play", "fiber", "hapi", "koa", "adonis", "nestjs",
        "quasar", "alpinejs", "stimulus", "pyramid", "tornado", "bottle",
        "web2py", "cherrypy", "coldfusion", "servlet", "jsp", "blade",
        "sinatra", "mason", "mojolicious", "plack", "rocket", "actix", "vapor",
        // Full Stack concepts
        "full stack", "frontend", "backend", "rest", "graphql", "grpc",
        "microservices", "api", "mvc", "spa", "ssr", "pwa",
        // Data & ML
        "pandas", "numpy", "scikit-learn", "tensorflow", "pytorch", "keras",
        "xgboost", "catboost", "mlops", "machine learning", "deep learning",
        "nlp", "computer vision", "data analysis", "data science",
        // Databases
        "sql", "mysql", "postgresql", "mssql", "sqlite", "oracle", "mongodb",
        "redis", "cassandra", "dynamodb", "couchdb", "elasticsearch", "neo4j",
        "arangodb", "bigquery", "snowflake", "databricks", "hadoop", "spark",
        "hive", "redshift", "clickhouse", "influxdb", "memcached", "firestore", "realm",
        // Cloud & DevOps
        "aws", "azure", "gcp", "docker", "kubernetes", "terraform", "ansible",
        "ci/cd", "jenkins", "airflow", "bash", "shell", "powershell", "vagrant",
        "openshift", "cloudformation", "circleci", "travis", "github actions",
        "gitlab ci", "argo", "helm", "prometheus", "grafana", "datadog",
        "new relic", "splunk", "puppet", "chef", "saltstack", "consul", "nomad",
        // Tools
        "git", "github", "gitlab", "bitbucket", "jira", "notion", "slack",
        "excel", "powerbi", "tableau", "looker", "matplotlib", "seaborn",
        "plotly", "bokeh", "d3.js", "highcharts", "sqlalchemy",
        "beautifulsoup", "scrapy", "requests", "httpx", "aiohttp",
        "pytest", "unittest", "mocha", "jest", "junit", "selenium", "cypress",
        "playwright", "postman", "swagger", "openapi", "soapui",
        // LLM & AI
        "prompt engineering", "langchain", "openai", "llm", "llms",
        "huggingface", "transformers", "bert", "gpt", "llama", "gemini", "claude",
        // Soft/Process
        "agile", "scrum", "kanban", "linux", "waterfall", "etl",
        "project management", "leadership", "communication", "testing",
        "unit testing", "tdd", "bdd", "oop", "soa", "design patterns",
        "system design", "ux", "ui", "a11y", "i18n", "l10n"
    );

    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]");

    private String normalize(String s) {
        return NON_ALNUM.matcher(s.toLowerCase()).replaceAll("");
    }

    public List<String> extractSkills(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();

        String textNorm = normalize(text);
        Set<String> found = new LinkedHashSet<>();

        for (String skill : COMMON_SKILLS) {
            String skillNorm = normalize(skill);
            if (textNorm.contains(skillNorm)) {
                found.add(skill);
            }
        }

        return found.stream().sorted().collect(Collectors.toList());
    }

    public Map<String, String> getLearningResources(Set<String> skills) {
        Map<String, String> all = getAllResources();
        Map<String, String> result = new LinkedHashMap<>();
        for (String skill : skills) {
            String url = all.get(skill.toLowerCase());
            if (url != null) {
                result.put(skill, url);
            }
        }
        return result;
    }

    private Map<String, String> getAllResources() {
        Map<String, String> m = new LinkedHashMap<>();
        // Programming Languages
        m.put("python", "https://www.coursera.org/specializations/python");
        m.put("java", "https://www.coursera.org/specializations/java-programming");
        m.put("javascript", "https://www.codecademy.com/learn/introduction-to-javascript");
        m.put("typescript", "https://www.typescriptlang.org/docs/");
        m.put("c++", "https://www.coursera.org/specializations/c-plus-plus-modern-development");
        m.put("c#", "https://www.codecademy.com/learn/learn-c-sharp");
        m.put("go", "https://tour.golang.org/");
        m.put("rust", "https://doc.rust-lang.org/book/");
        m.put("swift", "https://developer.apple.com/swift/resources/");
        m.put("kotlin", "https://kotlinlang.org/docs/getting-started.html");
        m.put("ruby", "https://www.codecademy.com/learn/learn-ruby");
        m.put("php", "https://www.codecademy.com/learn/learn-php");
        m.put("r", "https://www.coursera.org/learn/r-programming");
        m.put("scala", "https://www.coursera.org/learn/scala-programming");
        // Web
        m.put("html", "https://www.codecademy.com/learn/learn-html");
        m.put("html5", "https://www.codecademy.com/learn/learn-html");
        m.put("css", "https://www.codecademy.com/learn/learn-css");
        m.put("css3", "https://www.codecademy.com/learn/learn-css");
        m.put("react", "https://www.codecademy.com/learn/react-101");
        m.put("reactjs", "https://www.codecademy.com/learn/react-101");
        m.put("react native", "https://reactnative.dev/docs/getting-started");
        m.put("angular", "https://angular.io/tutorial");
        m.put("vue", "https://vuejs.org/guide/");
        m.put("vuejs", "https://vuejs.org/guide/");
        m.put("node", "https://www.codecademy.com/learn/learn-node-js");
        m.put("nodejs", "https://www.codecademy.com/learn/learn-node-js");
        m.put("express", "https://expressjs.com/en/starter/installing.html");
        m.put("next.js", "https://nextjs.org/learn");
        m.put("bootstrap", "https://getbootstrap.com/docs/5.3/getting-started/introduction/");
        m.put("tailwind", "https://tailwindcss.com/docs/installation");
        m.put("jquery", "https://www.codecademy.com/learn/learn-jquery");
        // Backend
        m.put("flask", "https://flask.palletsprojects.com/en/2.3.x/tutorial/");
        m.put("django", "https://docs.djangoproject.com/en/4.2/intro/tutorial01/");
        m.put("fastapi", "https://fastapi.tiangolo.com/tutorial/");
        m.put("spring", "https://spring.io/guides");
        m.put("spring boot", "https://spring.io/guides/gs/spring-boot/");
        m.put("laravel", "https://laravel.com/docs/10.x");
        m.put("rails", "https://guides.rubyonrails.org/");
        m.put("ruby on rails", "https://guides.rubyonrails.org/");
        m.put("asp.net", "https://docs.microsoft.com/en-us/aspnet/");
        m.put("rest", "https://www.udemy.com/course/rest-api/");
        m.put("api", "https://www.codecademy.com/learn/paths/designing-apis-with-swagger-and-openapi");
        m.put("graphql", "https://graphql.org/learn/");
        m.put("microservices", "https://www.coursera.org/learn/microservices");
        // ML/Data
        m.put("machine learning", "https://www.coursera.org/learn/machine-learning");
        m.put("deep learning", "https://www.deeplearning.ai/deep-learning-specialization/");
        m.put("data science", "https://www.coursera.org/specializations/jhu-data-science");
        m.put("data analysis", "https://www.coursera.org/learn/data-analysis-with-python");
        m.put("pandas", "https://www.coursera.org/learn/data-analysis-with-python");
        m.put("numpy", "https://www.datacamp.com/courses/intro-to-python-for-data-science");
        m.put("scikit-learn", "https://scikit-learn.org/stable/tutorial/index.html");
        m.put("tensorflow", "https://www.coursera.org/professional-certificates/tensorflow-in-practice");
        m.put("pytorch", "https://pytorch.org/tutorials/");
        m.put("keras", "https://keras.io/guides/");
        m.put("mlops", "https://www.coursera.org/specializations/machine-learning-engineering-for-production-mlops");
        m.put("nlp", "https://www.coursera.org/learn/language-processing");
        m.put("computer vision", "https://www.coursera.org/learn/convolutional-neural-networks");
        // Databases
        m.put("sql", "https://www.coursera.org/learn/sql-for-data-science");
        m.put("mysql", "https://www.mysql.com/products/workbench/");
        m.put("postgresql", "https://www.postgresql.org/docs/current/tutorial.html");
        m.put("mongodb", "https://university.mongodb.com/");
        m.put("redis", "https://redis.io/docs/getting-started/");
        m.put("elasticsearch", "https://www.elastic.co/guide/en/elasticsearch/reference/current/getting-started.html");
        m.put("cassandra", "https://cassandra.apache.org/doc/latest/cassandra/getting_started/");
        m.put("dynamodb", "https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStartedDynamoDB.html");
        m.put("bigquery", "https://cloud.google.com/bigquery/docs/quickstarts");
        m.put("snowflake", "https://docs.snowflake.com/en/user-guide-getting-started.html");
        // Cloud & DevOps
        m.put("aws", "https://www.coursera.org/specializations/aws-fundamentals");
        m.put("azure", "https://docs.microsoft.com/en-us/learn/azure/");
        m.put("gcp", "https://cloud.google.com/training");
        m.put("docker", "https://www.coursera.org/learn/docker");
        m.put("kubernetes", "https://www.coursera.org/learn/google-kubernetes-engine");
        m.put("terraform", "https://learn.hashicorp.com/terraform");
        m.put("ansible", "https://docs.ansible.com/ansible/latest/user_guide/index.html");
        m.put("jenkins", "https://www.jenkins.io/doc/tutorials/");
        m.put("ci/cd", "https://www.coursera.org/learn/devops-culture-and-mindset");
        m.put("github actions", "https://docs.github.com/en/actions/learn-github-actions");
        m.put("gitlab ci", "https://docs.gitlab.com/ee/ci/quick_start/");
        m.put("bash", "https://www.codecademy.com/learn/learn-the-command-line");
        m.put("shell", "https://www.codecademy.com/learn/learn-the-command-line");
        m.put("linux", "https://www.coursera.org/learn/linux-command-line");
        // Tools
        m.put("git", "https://www.codecademy.com/learn/learn-git");
        m.put("github", "https://docs.github.com/en/get-started/quickstart/hello-world");
        m.put("jira", "https://www.udemy.com/course/jira-tutorial-a-comprehensive-guide-for-jira/");
        m.put("excel", "https://www.coursera.org/professional-certificates/microsoft-excel-skills");
        m.put("powerbi", "https://docs.microsoft.com/en-us/power-bi/fundamentals/");
        m.put("tableau", "https://www.tableau.com/learn/training");
        m.put("selenium", "https://www.selenium.dev/documentation/");
        m.put("pytest", "https://docs.pytest.org/en/stable/");
        m.put("junit", "https://junit.org/junit5/docs/current/user-guide/");
        m.put("jest", "https://jestjs.io/docs/getting-started");
        m.put("postman", "https://learning.postman.com/docs/getting-started/introduction/");
        m.put("swagger", "https://swagger.io/docs/specification/about/");
        // AI
        m.put("prompt engineering", "https://www.deeplearning.ai/short-courses/chatgpt-prompt-engineering-for-developers/");
        m.put("langchain", "https://python.langchain.com/docs/get_started/introduction");
        m.put("huggingface", "https://huggingface.co/docs");
        m.put("llm", "https://www.coursera.org/learn/generative-ai-with-llms");
        // Process
        m.put("agile", "https://www.coursera.org/learn/agile-development");
        m.put("scrum", "https://www.scrum.org/resources/scrum-guide");
        m.put("system design", "https://www.educative.io/courses/grokking-the-system-design-interview");
        m.put("design patterns", "https://refactoring.guru/design-patterns");
        return m;
    }
}
