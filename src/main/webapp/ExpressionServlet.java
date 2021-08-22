import lexemanalyzer.Analyzer;
import lexemanalyzer.Repository;

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
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

@WebServlet("/calc/*")
public class ExpressionServlet extends HttpServlet {
    private final Repository repository = new Repository();
    private static final String EXPRESSION = "expression";
    private static final String EMPTY_STRING = "";

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter printWriter = resp.getWriter();
        printWriter.printf(String.valueOf(getExprResult(req)));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(req.getInputStream());
        BufferedReader bufferedReader = new BufferedReader(streamReader);
        String paramValue = bufferedReader.readLine();
        addParametersToRepository(req, paramValue);
    }

    private void addParametersToRepository(HttpServletRequest req, String paramValue) {
        if (!repository.getParametersData().containsKey(req.getSession().getId())) {
            repository.getParametersData().put(req.getSession().getId(), new ConcurrentHashMap<>());
        }
        repository.getParametersData().get(req.getSession().getId()).put(getNameParameter(req), paramValue);
    }

    private String getNameParameter(HttpServletRequest req) {
        return req.getPathInfo().substring(1);
    }

    private int getExprResult(HttpServletRequest req) {
        String[] expression = getExpression(req.getSession().getId());
        String expressionWithValue = getExpressionWithValue(expression, req.getSession().getId());
        return new Analyzer().getExpressionResult(expressionWithValue, req, repository);
    }

    private String[] getExpression(String sessionID) {
        return repository.getParametersData().get(sessionID).get(EXPRESSION).split(EMPTY_STRING);
    }

    private String getExpressionWithValue(String[] expression, String sessionID) {
        List<String> expressionList = new ArrayList<>(List.of(expression));
        IntStream.range(0, expressionList.size())
                .filter(i -> repository.getParametersData().get(sessionID).containsKey(expressionList.get(i)))
                .forEach(i -> expressionList.set(i, repository.getParametersData().get(sessionID).get(expressionList.get(i))));
        return String.join(EMPTY_STRING, expressionList);
    }
}