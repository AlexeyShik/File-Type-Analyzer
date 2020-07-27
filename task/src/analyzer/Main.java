package analyzer;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import analyzer.strategy.*;

public class Main {
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final String IF_SEARCH_FAILED = "Unknown file type";

    /**
     * @param inputPatterns needed information about patterns to perform searching algorithm
     * @param text          file, that contains the text to search in
     * @param context       needed information about algorithm
     * @return task for ExecutorService
     */
    @NotNull
    private static Callable<List<Integer>> checkFiles(@NotNull List<InputPattern> inputPatterns, File text, SearchContext context) {
        return () -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(text)))) {
                final SearchSource searchSource = new SearchSource(reader, inputPatterns.stream()
                        .map(x -> x.patternChars).collect(Collectors.toList()));
                return context.search(searchSource);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    /**
     * @param directory directory for searching files
     * @return list of tasks. Each task processes a single file
     */
    @NotNull
    private static List<File> checkDirectory(@NotNull File directory) {
        final List<File> result = new ArrayList<>();
        for (File child : Objects.requireNonNull(directory.listFiles())) {
            if (child.isFile()) {
                result.add(child);
            } else if (child.isDirectory()) {
                result.addAll(checkDirectory(child));
            }
        }
        return result;
    }

    /**
     * @param args: [0] - file, that contains all patterns
     *              [1] - file or directory, that contain all files for searching.
     *              or alternative
     * @param args: [0] - input file directory,
     *              [1] - pattern that is being searching for,
     *              [2] - type of file, if it contains pattern.
     */
    public static void main(@NotNull String[] args) {
        if (args.length != 2 && args.length != 3) {
            System.err.println("Please, follow the input data format:");
            System.out.println("1) [file, that contains all patterns] " +
                    "[file or directory, that contain all files for searching]");
            System.err.println("or 2) [input file directory] [pattern that is being searching for]" +
                    " [type of file, if it contains pattern]");
            return;
        }

        final SearchContext searchContext = new SearchContext();
        searchContext.setAlgorithm(new RabinKarpAlgorithm()); // all 3 algorithms works correctly

        final File filePath = new File(args[0]);

        final List<InputPattern> patterns = new ArrayList<>();
        if (args.length == 2) {
            final File patternsDirectory = new File(args[1]);

            if (patternsDirectory.isFile()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(patternsDirectory)))) {
                    while (reader.ready()) {
                        patterns.add(new PatternParser(reader.readLine()).parse());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("First argument should be existing file (also not a directory)");
                return;
            }
        } else {
            patterns.add(new InputPattern(args[1].toCharArray(), 1, args[2]));
        }

        final List<File> files = new ArrayList<>();
        final boolean isOnceFile;
        if (filePath.isFile()) {
            files.add(filePath);
            isOnceFile = true;
        } else if (filePath.isDirectory()) {
            files.addAll(checkDirectory(filePath));
            isOnceFile = false;
        } else {
            System.err.println("Second argument should be existing file or directory");
            return;
        }

        final List<Callable<List<Integer>>> callables = files.stream()
                .map(x -> checkFiles(patterns, x, searchContext)).collect(Collectors.toList());

        List<List<Integer>> results = new ArrayList<>();
        try {
            var futures = executor.invokeAll(callables);
            results = futures.stream().map(x -> {
                try {
                    return x.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        for (int i = 0; i < results.size(); ++i) {
            int maxPriority = -1;
            String result = IF_SEARCH_FAILED;
            if (results.get(i) != null) {
                for (Integer index : results.get(i)) {
                    if (patterns.get(index).priority > maxPriority) {
                        maxPriority = patterns.get(index).priority;
                        result = patterns.get(index).typeIfFound;
                    }
                }
            }
            System.out.println((isOnceFile ? "" : files.get(i).getName() + ": ") + result);
        }
    }
}