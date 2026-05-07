package org.ops4j.op.shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JsonNodeIterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class) @Command(name = "bash-filter",
    description = "Filter a stream through a bash process.")
public class BashFilter extends ShellOp<BashFilter>
{
  private OutputStream     stdin;
  private InputStream      stdout;
  private JsonNodeIterator jnit;
  private BufferedWriter   writer;
  private BufferedReader   reader;

  public BashFilter()
  {
    super("bash-filter");
    lifecycle().willProvide(PhaseType.OPEN, PhaseType.EXECUTE, PhaseType.CLOSE);
  }

  public BashFilter open() throws OpsException
  {
    try
    {
      getCommands().add(0, "-c");
      getCommands().add(0, "bash");
      super.open();

      stdin = getProcess().getOutputStream();
      writer = new BufferedWriter(new OutputStreamWriter(stdin));

      stdout = getProcess().getInputStream();
      reader = new BufferedReader(new InputStreamReader(stdout));
      jnit = JsonNodeIterator.fromReader(reader);
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    List<OpData> output = new ArrayList<>();

    try
    {
      DEBUG("STDIN='", input.toString(), "'");

      try
      {
        writer.write(input.toString());
        writer.newLine();
        writer.flush();
      }
      catch(IOException ex)
      {
        // Ignore
      }

      while (reader.ready() && jnit.hasNext())
      {
        JsonNode json = jnit.next();
        DEBUG("STDOUT='", json);
        output.add(OpData.from(json));
      }
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return output;
  }

  public List<OpData> close() throws OpsException
  {
    List<OpData> output = new ArrayList<>();

    try
    {
      writer.newLine();
      writer.flush();
      writer.close();
    }
    catch(IOException ex)
    {
      ex.printStackTrace();
    }

    while (jnit.hasNext())
    {
      try
      {
        output.add(OpData.from(jnit.next()));
      }
      catch(IOException ex)
      {
        ex.printStackTrace();
      }
    }

    try
    {
      reader.close();
    }
    catch(IOException ex)
    {
      ex.printStackTrace();
    }
    return output;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new BashFilter(), args);
  }
}
