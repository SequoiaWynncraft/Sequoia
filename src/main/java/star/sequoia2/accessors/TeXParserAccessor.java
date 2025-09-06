package star.sequoia2.accessors;

import star.sequoia2.client.SeqClient;
import star.sequoia2.utils.text.parser.TeXParser;

public interface TeXParserAccessor {
    default TeXParser teXParser() {
        return SeqClient.getTeXParser();
    }
}
