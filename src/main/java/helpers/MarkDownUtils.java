package helpers;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;

/**
 * Utility class for converting Markdown to HTML.
 */
public class MarkDownUtils {
    private static final Parser PARSER = Parser.builder().build();
    private static final HtmlRenderer HTML_RENDERER = HtmlRenderer.builder().build();

    /**
     * Converts a Markdown string to HTML.
     *
     * @param markdown the Markdown string to convert
     * @return the converted HTML string
     */
    public static String markdownToHtml(String markdown) {
        return HTML_RENDERER.render(PARSER.parse(markdown));
    }

}

