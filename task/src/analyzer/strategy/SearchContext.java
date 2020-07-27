package analyzer.strategy;

import java.util.List;

public class SearchContext {

    SearchAlgorithm algorithm;

    public void setAlgorithm(SearchAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public List<Integer> search(SearchSource source) {
        return algorithm.search(source);
    }
}