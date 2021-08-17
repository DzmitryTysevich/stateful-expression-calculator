package lexemanalyzer;

import java.util.HashMap;
import java.util.Map;

public class ExpressionData {
    private final Map<String, String> data = new HashMap<>();

    public Map<String, String> getData() {
        return data;
    }

    public void put(String parameterName, String value) {
        data.put(parameterName, value);
    }
}