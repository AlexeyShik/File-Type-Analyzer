package analyzer.strategy;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Searches in O(|text| * |pattern|) time, using O(|text| + |pattern|) memory
public class NaiveAlgorithm implements SearchAlgorithm {
    /**
     * @param firstTextChars  the first chars of searching destination
     * @param secondTextChars the last chars of searching destination
     * @param patternChars    pattern that is being searching for
     * @return true, if pattern exists in concatenation of firstTextChars and secondTextChars
     */
    @Contract(pure = true)
    private static boolean findPattern(@NotNull char[] firstTextChars, @NotNull char[] secondTextChars, @NotNull char[] patternChars) {
        for (int begin = 0; begin < firstTextChars.length && begin + patternChars.length < firstTextChars.length + secondTextChars.length; ++begin) {
            final int charsTakenFromFirst = firstTextChars.length - begin;
            final int end = Math.max(0, patternChars.length - charsTakenFromFirst);
            boolean exists = true;
            for (int i = 0; begin + i < firstTextChars.length; ++i) {
                if (firstTextChars[begin + i] != patternChars[i]) {
                    exists = false;
                    break;
                }
            }
            for (int i = 0; i < end; ++i) {
                if (secondTextChars[i] != patternChars[charsTakenFromFirst + i]) {
                    exists = false;
                    break;
                }
            }
            if (exists) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param textChars    chars of searching destination
     * @param fileSize     size of searching destination
     * @param patternChars pattern that is being searching for
     * @return true, if patternChars exists in textChars
     */
    @Contract(pure = true)
    private static boolean findPatternInSmallFile(@NotNull char[] textChars, int fileSize, @NotNull char[] patternChars) {
        for (int shift = 0; shift + patternChars.length <= fileSize; ++shift) {
            boolean exists = true;
            for (int i = 0; i < patternChars.length; ++i) {
                if (textChars[shift + i] != patternChars[i]) {
                    exists = false;
                    break;
                }
            }
            if (exists) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param reader text source
     * @param chars  text destination
     * @return number of read chars
     */
    private int checkedRead(@NotNull BufferedReader reader, final char[] chars) {
        try {
            return reader.read(chars);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<Integer> search(@NotNull SearchSource source) {
        int maxLen = -1;
        for (var elem : source.patterns) {
            maxLen = Math.max(maxLen, elem.length);
        }
        final int BUFFER_SIZE = Math.max(2 * 1024, maxLen);
        char[] lastChars = new char[BUFFER_SIZE];

        int charsRead = checkedRead(source.textReader, lastChars);

        final char[] currentChars = new char[BUFFER_SIZE];

        List<Integer> occurrences = new ArrayList<>();
        boolean[] isFound = new boolean[source.patterns.size()];

        int currentCharsRead;
        while ((currentCharsRead = checkedRead(source.textReader, currentChars)) != -1) {
            for (int i = 0; i < source.patterns.size(); ++i) {
                if (!isFound[i] && findPattern(lastChars, currentChars, source.patterns.get(i))) {
                    occurrences.add(i);
                    isFound[i] = true;
                }
            }
            lastChars = Arrays.copyOf(currentChars, BUFFER_SIZE);
            Arrays.fill(currentChars, (char) 0);
            charsRead = currentCharsRead;
        }

        for (int i = 0; i < source.patterns.size(); ++i) {
            if (!isFound[i] && findPatternInSmallFile(lastChars, charsRead, source.patterns.get(i))) {
                occurrences.add(i);
            }
        }
        return occurrences;
    }
}