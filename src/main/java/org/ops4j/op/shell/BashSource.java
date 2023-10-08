package org.ops4j.op.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.util.JsonNodeIterator;
import org.ops4j.util.ReaderBackedIterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class) @Command(name = "bash:source",
    description = "Use the output from a bash process as input.")
public class BashSource extends ShellOp<BashSource> implements JsonSource
{
  private InputStream      stdout;
  private JsonNodeIterator jnit;
  private BufferedReader   reader;

  public BashSource()
  {
    super("bash:source");
    lifecycle().willProvide(PhaseType.OPEN, PhaseType.EXECUTE, PhaseType.CLOSE);
  }

  public BashSource open() throws OpsException
  {
    try
    {
      getCommands().add(0, "-c");
      getCommands().add(0, "bash");
      super.open();

      stdout = proc.getInputStream();
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
    DEBUG("EXECUTE");
    List<OpData> output = new ArrayList<>();

    try
    {
      if (reader.ready() && jnit.hasNext())
      {
        JsonNode json = jnit.next();
        DEBUG("STDOUT='", json);
        return OpData.from(json).asList();
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
      while (jnit.hasNext())
      {
        output.add(OpData.from(jnit.next()));
      }
    }
    catch(IOException ex)
    {
      ex.printStackTrace();
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

  @Override
  public Iterator<JsonNode> getIterator()
  {
    return new ReaderBackedIterator<>(reader, jnit);
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new BashSource(), args);
  }
}
