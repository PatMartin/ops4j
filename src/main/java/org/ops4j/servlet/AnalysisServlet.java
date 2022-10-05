package org.ops4j.servlet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.ops4j.inf.Fallback;
import org.ops4j.op.http.HttpServer;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AnalysisServlet extends HttpServlet implements Fallback
{
  private static final long serialVersionUID = 7032785581338862391L;

  public AnalysisServlet()
  {
    super();
  }

  private String template  = "/dexjs.gt";
  private String chartType = null;

  @Override
  public void init(final ServletConfig config) throws ServletException
  {
    super.init(config);
    template = config.getInitParameter("template");
    chartType = config.getInitParameter("chartType");
  }

  @Override
  protected void doGet(final HttpServletRequest req,
      final HttpServletResponse resp) throws ServletException, IOException
  {
    PrintWriter writer = resp.getWriter();
    SimpleTemplateEngine engine = new SimpleTemplateEngine();
    Template t = engine.createTemplate(
        new InputStreamReader(this.getClass().getResourceAsStream(template)));
    Map<String, Object> binding = new HashMap<String, Object>();
    binding.put("DATA", HttpServer.DATA);
    binding.put("chartType", chartType);
    writer.write(t.make(binding).toString());
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