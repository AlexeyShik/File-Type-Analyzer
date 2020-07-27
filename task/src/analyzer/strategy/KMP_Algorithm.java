package analyzer.strategy;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//  Searches in O(|text| + |pattern|) time, using O(|text| + |pattern|) memory
//  Realization of KMP algorithm
public class KMP_Algorithm implements SearchAlgorithm {
    /**
     * @param chars array for calculating prefix function
     * @return prefix function for input array
     */
    @NotNull
    @Contract(pure = true)
    private static int[] prefixFunction(@NotNull char[] chars) {
        final int[] prefixFunc = new int[chars.length];
        for (int i = 1; i < chars.length; i++) {
            int j = prefixFunc[i - 1];
            while (j > 0 && chars[i] != chars[j]) {
                j = prefixFunc[j - 1];
            }
            if (chars[i] == chars[j]) {
                ++j;
            }
            prefixFunc[i] = j;
        }
        return prefixFunc;
    }

    /**
     * @param text       chars of searching destination
     * @param length     size of searching destination
     * @param pattern    pattern that is being searching for
     * @param prefixFunc prefix function for pattern
     * @param positions  sizes of current longest border
     * @param index      current index in positions
     * @return true, if text exists in pattern
     */
    @Contract(pure = true)
    private boolean KMPSearch(char[] text, int length, char[] pattern, int[] prefixFunc, int[] positions, int index) {
        for (int i = 0; i < length; ++i) {
            while (positions[index] > 0 && text[i] != pattern[positions[index]]) {
                positions[index] = prefixFunc[positions[index] - 1];
            }
            if (text[i] == pattern[positions[index]]) {
                ++positions[index];
            }
            if (positions[index] == pattern.length) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Contract(pure = true)
    public List<Integer> search(@NotNull SearchSource source) {
        final int BUFFER_SIZE = 2 * 1024;  //  sizeof page = 4096 bytes = BUFFER_SIZE * sizeof(char)
        final char[] currentChars = new char[BUFFER_SIZE];
        final List<Integer> occurrences = new ArrayList<>();
        try {
            int charsRead = source.textReader.read(currentChars);

            final List<int[]> prefixFunc = source.patterns.stream()
                    .map(KMP_Algorithm::prefixFunction).collect(Collectors.toList());
            final int[] positions = new int[source.patterns.size()];

            final boolean[] isFound = new boolean[source.patterns.size()];
            while (charsRead != -1) {
                for (int i = 0; i < source.patterns.size(); ++i) {
                    if (!isFound[i] && KMPSearch(currentChars, charsRead,
                            source.patterns.get(i), prefixFunc.get(i), positions, i)) {
                        isFound[i] = true;
                        occurrences.add(i);
                    }
                }
                charsRead = source.textReader.read(currentChars);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return occurrences;
    }
}