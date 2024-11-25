package ru.otus.java.pro.jee_webserver;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@WebFilter(filterName = "LogFilterServlet", urlPatterns = "/*")
public class LogFilterServlet implements Filter {
    private static final Logger logger = LogManager.getLogger(LogFilterServlet.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        logger.info("Request URL: {}", httpRequest.getRequestURL());
        logger.info("Request Method: {}", httpRequest.getMethod());
        logger.info("Request Parameters: {}", httpRequest.getParameterMap());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
