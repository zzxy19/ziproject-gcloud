package com.ziproject.zibackend;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DummyServlet extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    resp.setContentType("text/plain");
    String method = req.getMethod();
    String item = req.getQueryString();
    PrintWriter responseWriter = resp.getWriter();
    responseWriter.println(method);
    responseWriter.println(item);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
    resp.setContentType("text/plain");
    String method = req.getMethod();
    String item = req.getQueryString();
    PrintWriter responseWriter = resp.getWriter();
    responseWriter.println(method);
    responseWriter.println(item);
  }
}
