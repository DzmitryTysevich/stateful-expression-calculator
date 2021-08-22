package lexemanalyzer;

import java.util.concurrent.ConcurrentHashMap;

public class Repository {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, String>> repos = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, ConcurrentHashMap<String, String>> getParametersData() {
        return repos;
    }
}