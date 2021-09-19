package servlet;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

@WebFilter("/calc/*")
public class ServletFilter implements javax.servlet.Filter {
    private FilterConfig config = null;
    private boolean active = false;

    @Override
    public void init(FilterConfig config) {
        this.config = config;
        String act = config.getInitParameter("active");
        if (act != null) {
            active = (act.equalsIgnoreCase("true"));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        PrintWriter pw = servletResponse.getWriter();
        if (active) {
            if (isAllowToWork()) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                pw.print("Server isn't working now...");
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    private boolean isAllowToWork() {
        return LocalDateTime.now()
                .isBefore(LocalDateTime.of(2021, 10, 1, 10, 0, 0));
    }

    @Override
    public void destroy() {
        config = null;
    }
}