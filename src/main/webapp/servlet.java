import lexemanalyzer.Analyzer;
import lexemanalyzer.ExpressionData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@WebServlet("/calc/*")
public class servlet extends HttpServlet {
    private final ExpressionData expressionData = new ExpressionData();

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter printWriter = resp.getWriter();
        printWriter.printf(String.valueOf(getResult()));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(req.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        expressionData.put(getNameParameter(req), bufferedReader.readLine());
    }

    private String getNameParameter(HttpServletRequest req) {
        return req.getPathInfo().substring(1);
    }

    private int getResult() {
        String[] expression = getExpression();
        String expressionWithValue = getExpressionWithValue(expression);
        Analyzer analyzer = new Analyzer();
        return analyzer.getExpressionResult(expressionWithValue, expressionData.getData());
    }

    private String[] getExpression() {
        return expressionData.getData().get("expression").split("");
    }

    private String getExpressionWithValue(String[] expression) {
        List<String> expressionList = new ArrayList<>(List.of(expression));
        Map<String, String> map = expressionData.getData();
        IntStream.range(0, expressionList.size())
                .filter(i -> map.containsKey(expressionList.get(i)))
                .forEach(i -> expressionList.set(i, map.get(expressionList.get(i))));
        return String.join("", expressionList);
    }
}