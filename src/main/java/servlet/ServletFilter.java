package servlet;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.GregorianCalendar;

@WebFilter("/calc/*")
public class ServletFilter implements javax.servlet.Filter {

    @Override
    public void init(FilterConfig config) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        PrintWriter pw = servletResponse.getWriter();
        Calendar realTime = new GregorianCalendar();
        Calendar endPointTime = new GregorianCalendar();
        endPointTime.set(2021, Calendar.OCTOBER, 1);
        if (realTime.before(endPointTime)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            pw.print("Server isn't working now...");
        }
    }

    @Override
    public void destroy() {
    }
}