package ru.otus.java.pro.jee_webserver;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

import static java.lang.Double.parseDouble;

@WebServlet(name = "MultiplyServlet", urlPatterns = "/multiply")
public class MultiplyServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(MultiplyServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try (PrintWriter out = response.getWriter()) {
            if (request == null) {
                logger.error("Something went wrong. HttpServletRequest request is null");
                response.sendError(400);
                return;
            }
            logger.info("Multiply servlet executed");
            response.setContentType("text/html");
            try {
                double n1 = parseDouble(request.getParameter("n1"));
                double n2 = parseDouble(request.getParameter("n2"));
                out.write("Result: " + (n1 * n2));
            } catch (NumberFormatException e) {
                logger.error("Error occurs when trying convert string to number.", e);
                response.sendError(400, "Error occurs when trying convert string to number. " +
                        "The string doesn't have the appropriate format.");
            }
        } catch (IOException e) {
            logger.error("Error occurs when trying to write response. " +
                    "Failed or interrupted I/ O operations occurs", e);
        }
    }
}