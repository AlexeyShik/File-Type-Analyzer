package analyzer.strategy;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

//  Searches in O(|text| + |pattern|) time, using O(|text| + |pattern|) memory
//  Realization of Rabin-Karp algorithm
public class RabinKarpAlgorithm implements SearchAlgorithm {
    private static final long MOD = 1_000_000_000 + 7;
    private static final long P = 37;

    private static long add(long a, long b) {
        return (a + b) % MOD;
    }

    private static long sub(long a, long b) {
        return (a - b + MOD) % MOD;
    }

    private static long mul(long a, long b) {
        return a * b % MOD;
    }

    private static long getHash(@NotNull char[] pattern) {
        long hash = 0;
        long pow = 1;
        for (char c : pattern) {
            hash = add(hash, mul(c, pow));
            pow = mul(pow, P);
        }
        return hash;
    }

    @NotNull
    private List<Integer> check(@NotNull char[] chars, List<char[]> patterns, Map<Long, Integer> patternHashes, int len) {
        if (chars.length < len) {
            return new ArrayList<>();
        }
        long hash = 0;
        long pow = 1;
        for (int i = chars.length - len; i < chars.length; ++i) {
            hash = add(hash, mul(chars[i], pow));
            if (i != chars.length - 1) {
                pow = mul(pow, P);
            }
        }
        final List<Integer> occurrences = new ArrayList<>();
        if (patternHashes.containsKey(hash) && Arrays.equals(patterns.get(patternHashes.get(hash)),
                Arrays.copyOfRange(chars, chars.length - len, chars.length))) {
            occurrences.add(patternHashes.get(hash));
        }
        for (int i = chars.length - len - 1; i >= 0; --i) {
            hash = sub(hash, mul(pow, chars[i + len]));
            hash = add(mul(hash, P), chars[i]);
            if (patternHashes.containsKey(hash) && Arrays.equals(patterns.get(patternHashes.get(hash)),
                    Arrays.copyOfRange(chars, i, i + len))) {
                occurrences.add(patternHashes.get(hash));
            }
        }
        return occurrences;
    }

    @Override
    public List<Integer> search(@NotNull SearchSource source) {
        final Map<Long, Integer> patternHashes = new HashMap<>();
        for (int i = 0; i < source.patterns.size(); ++i) {
            patternHashes.put(getHash(source.patterns.get(i)), i);
        }
        final Set<Integer> sizesForChecking = new TreeSet<>();
        for (char[] pattern : source.patterns) {
            sizesForChecking.add(pattern.length);
        }
        char[] chars = new char[0];
        try {
            chars = source.textReader.readLine().toCharArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Integer> occurrences = new ArrayList<>();
        for (int len : sizesForChecking) {
            occurrences.addAll(check(chars, source.patterns, patternHashes, len));
        }
        return occurrences;
    }
}
