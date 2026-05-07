package org.ops4j.groovy.op;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import groovy.lang.Writable;
import groovy.text.StreamingTemplateEngine;
import groovy.text.Template;
import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class)
@Command(name = "groovy-template", description = "Render a Groovy template.")
public class GroovyTemplate extends BaseOp<GroovyTemplate>
{
  @Option(names = { "-t", "--template" }, description = "The template.")
  private @Getter @Setter String  templatePath = null;

  private List<OpData>            data         = new ArrayList<OpData>();

  private StreamingTemplateEngine engine;
  private Template                template;

  public GroovyTemplate()
  {
    super("groovy-template");
  }

  public GroovyTemplate open() throws OpsException
  {
    engine = new StreamingTemplateEngine();
    try
    {
      template = engine.createTemplate(new File(getTemplatePath()));
    }
    catch(CompilationFailedException | ClassNotFoundException | IOException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    data.add(input);
    return input.asList();
  }

  private void render() throws OpsException {
    Map<String, Object> binding = new HashMap<String, Object>();
    binding.put("data", data);
    Writable w = template.make(binding);
    StringWriter sw = new StringWriter();
    try
    {
      w.writeTo(sw);
      System.out.println(sw.toString());
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
  }
  
  public List<OpData> close() throws OpsException
  {
    render();
    return OpData.emptyList();
  }

  public GroovyTemplate templatePath(String templatePath)
  {
    setTemplatePath(templatePath);
    return this;
  }

  public String templatePath()
  {
    return getTemplatePath();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new GroovyTemplate(), args);
  }
}
