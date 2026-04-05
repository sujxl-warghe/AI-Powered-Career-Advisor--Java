package com.smartcareer.config;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple Markdown-to-HTML converter for AI response rendering.
 * Handles: **bold**, *italic*, ### headings, - bullets, numbered lists, newlines.
 */
@Component("markdownUtils")
public class MarkdownUtils {

    private static final Pattern BOLD = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern ITALIC = Pattern.compile("\\*(.+?)\\*");
    private static final Pattern H3 = Pattern.compile("(?m)^### (.+)$");
    private static final Pattern H2 = Pattern.compile("(?m)^## (.+)$");
    private static final Pattern H1 = Pattern.compile("(?m)^# (.+)$");
    private static final Pattern BULLET = Pattern.compile("(?m)^[\\-\\*] (.+)$");
    private static final Pattern NUMBERED = Pattern.compile("(?m)^(\\d+)\\. (.+)$");
    private static final Pattern HR = Pattern.compile("(?m)^---+$");
    private static final Pattern CODE_INLINE = Pattern.compile("`(.+?)`");

    public static String toHtml(String markdown) {
        if (markdown == null || markdown.isBlank()) return "";

        String html = escapeHtml(markdown);

        // Headings (after escaping so we work on clean text)
        html = H1.matcher(html).replaceAll("<h4 class=\"md-h1\">$1</h4>");
        html = H2.matcher(html).replaceAll("<h4 class=\"md-h2\">$1</h4>");
        html = H3.matcher(html).replaceAll("<h4 class=\"md-h3\">$1</h4>");

        // HR
        html = HR.matcher(html).replaceAll("<hr class=\"md-hr\"/>");

        // Bold & italic
        html = BOLD.matcher(html).replaceAll("<strong>$1</strong>");
        html = ITALIC.matcher(html).replaceAll("<em>$1</em>");

        // Inline code
        html = CODE_INLINE.matcher(html).replaceAll("<code>$1</code>");

        // Bullet lists — wrap consecutive bullets in <ul>
        html = wrapListItems(html, BULLET, "ul", "<li>$1</li>");

        // Numbered lists
        html = wrapListItems(html, NUMBERED, "ol", "<li>$2</li>");

        // Paragraphs: convert \n\n to </p><p>, then wrap everything
        html = html.replace("\r\n", "\n").replace("\r", "\n");
        html = html.replaceAll("\n{2,}", "</p><p>");
        html = html.replace("\n", "<br/>");
        html = "<p>" + html + "</p>";

        // Clean up empty paragraphs
        html = html.replaceAll("<p>\\s*</p>", "");
        html = html.replaceAll("<p>(<h[1-6])", "$1");
        html = html.replaceAll("(</h[1-6]>)</p>", "$1");
        html = html.replaceAll("<p>(<ul>|<ol>|<hr)", "$1");
        html = html.replaceAll("(</ul>|</ol>)</p>", "$1");

        return html;
    }

    private static String wrapListItems(String html, Pattern pattern, String tag, String replacement) {
        Matcher m = pattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        boolean inList = false;
        int lastEnd = 0;

        while (m.find()) {
            String before = html.substring(lastEnd, m.start());
            if (before.length() > 0) {
                if (inList) {
                    sb.append("</").append(tag).append(">");
                    inList = false;
                }
                sb.append(before);
            }
            if (!inList) {
                sb.append("<").append(tag).append(">");
                inList = true;
            }
            String item = m.group(0);
            sb.append(pattern.matcher(item).replaceAll(replacement));
            lastEnd = m.end();
        }
        if (inList) {
            sb.append("</").append(tag).append(">");
        }
        sb.append(html.substring(lastEnd));
        return sb.toString();
    }

    private static String escapeHtml(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
