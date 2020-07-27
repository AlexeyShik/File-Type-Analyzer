package analyzer;

import org.jetbrains.annotations.NotNull;

// parse patterns in format: priority;"pattern";"typeIfFound" (example: 1;"%PDF-";"PDF document")
public class PatternParser {
    private static final char DELIMITER = ';';
    private static final char STRING_IDENTIFIER = '\"';
    private int pos;
    private final String pattern;

    public PatternParser(String pattern) {
        this.pattern = pattern;
        pos = 0;
    }

    private boolean test(char expected) {
        return getChar() == expected;
    }

    private void expect(char expected) {
        if (!test(expected)) {
            System.out.println("Expected: " + expected + ", found: " + getChar());
            throw new RuntimeException("Parsing Exception");
        }
        nextChar();
    }

    private char getChar() {
        if (pos == pattern.length()) {
            System.out.println("End of pattern found");
            throw new RuntimeException("Parsing Exception");
        }
        return pattern.charAt(pos);
    }

    private void nextChar() {
        ++pos;
    }

    private void skipTo(char signalChar) {
        while (!test(signalChar)) {
            nextChar();
        }
    }

    @NotNull
    private String parseString() {
        expect(STRING_IDENTIFIER);
        int begin = pos;
        skipTo(STRING_IDENTIFIER);
        expect(STRING_IDENTIFIER);
        return pattern.substring(begin, pos - 1);
    }

    /**
     *
     * @return needed for searching information about pattern
     */
    public InputPattern parse() {
        InputPattern result = new InputPattern();
        while (!test(DELIMITER)) {
            result.priority = result.priority * 10 + Character.digit(getChar(), 10);
            nextChar();
        }
        expect(DELIMITER);
        result.patternChars = parseString().toCharArray();
        expect(DELIMITER);
        result.typeIfFound = parseString();
        return result;
    }

}
