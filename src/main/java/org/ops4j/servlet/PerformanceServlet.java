package org.ops4j.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PerformanceServlet extends HttpServlet
{
  private static final long serialVersionUID = 7032785581338862391L;

  public PerformanceServlet()
  {
    super();
  }

  public static final String TEMPLATE = "dexjs.gt";
  private String             template;

  @Override
  public void init(final ServletConfig config) throws ServletException
  {
    super.init(config);
    template = config.getInitParameter(TEMPLATE);
  }

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException
  {
    PrintWriter writer = resp.getWriter();
    SimpleTemplateEngine engine = new SimpleTemplateEngine();
    Template t = engine.createTemplate(
        new InputStreamReader(this.getClass().getResourceAsStream(TEMPLATE)));
    // .make(bindMap);
    // Grab the data.
    // Feed the data to a groovy template
    writer.close();
  }

  @Override
  protected void doPost(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException
  {
    doGet(req, resp);
  }
}