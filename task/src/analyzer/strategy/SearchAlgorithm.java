package analyzer.strategy;

import java.util.List;

//  Common interface for all algorithms
//  I decided, that nothrow method is better
interface SearchAlgorithm {
    /**
     * @param source - way to read text and pattern
     * @return true, if text exists in pattern
     */
    List<Integer> search(SearchSource source);
}