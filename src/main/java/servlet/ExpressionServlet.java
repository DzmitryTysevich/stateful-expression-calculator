package servlet;

import lexemanalyzer.Analyzer;

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

import static data.Repository.SINGLETON_REP;

@WebServlet("/calc/*")
public class ExpressionServlet extends HttpServlet {
    private static final String EXPRESSION = "expression";
    private static final String EMPTY_STRING = "";

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        SINGLETON_REP.getParametersData().get(req.getSession().getId()).remove(getNameParameter(req));
        resp.setStatus(204);//status code for delete
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
        addParametersToRepository(req, paramValue, resp);
    }

    private void addParametersToRepository(HttpServletRequest req, String paramValue, HttpServletResponse resp) {
        if (!SINGLETON_REP.getParametersData().containsKey(req.getSession().getId())) {
            SINGLETON_REP.getParametersData().put(req.getSession().getId(), new ConcurrentHashMap<>());
        }
        setStatusCode(req, resp, paramValue);
        SINGLETON_REP.getParametersData().get(req.getSession().getId()).put(getNameParameter(req), paramValue);
    }

    private void setStatusCode(HttpServletRequest req, HttpServletResponse resp, String paramValue) {
        if (getValueFromRep(req) == null) {
            resp.setStatus(201); //new expression
        } else if (!getValueFromRep(req).equals(paramValue) && !isBadFormat(paramValue, req)) {
            resp.setStatus(200); //expression changed
        } else if (isBadFormat(paramValue, req)) {
            resp.setStatus(400); //expression bad format
        }
    }

    private String getValueFromRep(HttpServletRequest req) {
        return SINGLETON_REP.getParametersData().get(req.getSession().getId()).get(getNameParameter(req));
    }

    private boolean isBadFormat(String paramValue, HttpServletRequest req) {
        return getNameParameter(req).equals("expression") &&
                !(paramValue.contains("+") || paramValue.contains("-") ||
                        paramValue.contains("/") || paramValue.contains("*"));
    }

    private String getNameParameter(HttpServletRequest req) {
        return req.getPathInfo().substring(1);
    }

    private int getExprResult(HttpServletRequest req) {
        String[] expression = getExpression(req.getSession().getId());
        String expressionWithValue = getExpressionWithValue(expression, req.getSession().getId());
        return new Analyzer().getExpressionResult(expressionWithValue, req, SINGLETON_REP);
    }

    private String[] getExpression(String sessionID) {
        return SINGLETON_REP.getParametersData().get(sessionID).get(EXPRESSION).split(EMPTY_STRING);
    }

    private String getExpressionWithValue(String[] expression, String sessionID) {
        List<String> expressionList = new ArrayList<>(List.of(expression));
        IntStream.range(0, expressionList.size())
                .filter(i -> SINGLETON_REP.getParametersData().get(sessionID).containsKey(expressionList.get(i)))
                .forEach(i -> expressionList.set(i, SINGLETON_REP.getParametersData().get(sessionID).get(expressionList.get(i))));
        return String.join(EMPTY_STRING, expressionList);
    }
}