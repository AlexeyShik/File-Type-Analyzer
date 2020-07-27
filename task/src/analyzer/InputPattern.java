package analyzer;

public class InputPattern {
    char[] patternChars;
    String typeIfFound;
    int priority;

    public InputPattern() {
        patternChars = null;
        typeIfFound = null;
        priority = 0;
    }

    public InputPattern(char[] patternChars, int priority, String typeIfFound) {
        this.patternChars = patternChars;
        this.priority = priority;
        this.typeIfFound = typeIfFound;
    }
}