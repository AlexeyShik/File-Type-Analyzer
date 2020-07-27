package analyzer.strategy;

import java.io.BufferedReader;
import java.util.List;

//  Common source for all algorithms for text and pattern
//  I believe that the text can be huge, so I don't read it all, when it is possible
public class SearchSource {
    final BufferedReader textReader;
    final List<char[]> patterns;

    public SearchSource(BufferedReader textReader, List<char[]> patterns) {
        this.textReader = textReader;
        this.patterns = patterns;
    }
}