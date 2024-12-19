package org.ops4j.op;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.util.JsonNodeIterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "shell", description = "Execute a shell script.")
public class Shell extends BaseOp<Shell> implements JsonSource
{
  private OutputStream stdin;

  private enum ShellType {
    EXECUTE, FILTER, SOURCE
  }

  @Parameters(index = "0", arity = "1..*",
      description = "The command to execute.")
  private @Getter @Setter List<String> commands  = new ArrayList<String>();

  @Option(names = { "--type" },
      description = "The type of shell.  Default=FILTER.%n%n"
          + "EXECUTE = Execute only.%n"
          + "FILTER = STDIN is the JSON input stream, STDOUT is the JSON%n"
          + "         output stream.%n"
          + "SOURCE = There is no STDIN.  STDOUT is the JSON output stream.")
  public @Getter @Setter ShellType     shellType = ShellType.FILTER;

  private Process                      proc;
  private InputStream                  stdout;
  private JsonNodeIterator             jnit;
  private BufferedWriter               writer;
  private BufferedReader               reader;

  public Shell()
  {
    super("shell");
    lifecycle().willProvide(PhaseType.OPEN, PhaseType.EXECUTE, PhaseType.CLOSE);
  }

  public Shell open() throws OpsException
  {
    try
    {
      getCommands().add(0, "-c");
      getCommands().add(0, "bash");

      DEBUG("COMMANDS: ", getCommands());
      proc = Runtime.getRuntime().exec(getCommands().toArray(new String[0]));

      if (getShellType() == ShellType.FILTER)
      {
        stdin = proc.getOutputStream();
        writer = new BufferedWriter(new OutputStreamWriter(stdin));
      }
      if (getShellType() == ShellType.SOURCE)
      {
        stdin = proc.getOutputStream();
        writer = new BufferedWriter(new OutputStreamWriter(stdin));
      }
      if (getShellType() != ShellType.EXECUTE)
      {
        stdout = proc.getInputStream();
        reader = new BufferedReader(new InputStreamReader(stdout));
        jnit = JsonNodeIterator.fromReader(reader);
      }
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    switch (getShellType())
    {
      case FILTER:
      {
        return filter(input);
      }
      case SOURCE:
      {
        return source(input);
      }
      default:
      {
        return input.asList();
      }
    }
  }

  public List<OpData> filter(OpData input) throws OpsException
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

  public List<OpData> source(OpData input) throws OpsException
  {
    List<OpData> output = new ArrayList<>();

    try
    {
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
    if (getShellType() == ShellType.FILTER)
    {
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
    }

    if (getShellType() == ShellType.EXECUTE)
    {
      return output;
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
    OpCLI.cli(new Shell(), args);
  }

  public boolean isJsonSource()
  {
    return getShellType() == ShellType.SOURCE;
  }

  @Override
  public Iterator<JsonNode> getIterator()
  {
    // TODO Auto-generated method stub
    return null;
  }
}
