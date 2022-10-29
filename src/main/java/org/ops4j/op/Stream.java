package org.ops4j.op;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.util.CircularIterator;
import org.ops4j.util.JsonNodeIterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "stream", description = "Stream data.")
public class Stream extends BaseOp<Stream> implements JsonSource
{
  @Parameters(index = "0", arity = "1", description = "The input file.")
  private @Getter @Setter String input;

  @Option(names = { "--size" }, description = "The maximum number of items"
      + " to cache when using a circular stream.  Default = 10000")
  private int                    cacheSize = 10000;

  @Option(names = { "--circular" }, description = "Read the file in a "
      + "circular fashion; over and over again.")
  private boolean                circular  = false;

  @Option(names = { "--limit" }, description = "Limits the number of "
      + "records to stream. (Default = 0 = unlimited)")
  private @Getter @Setter long   limit     = 0;

  private Iterator<JsonNode>     it        = null;

  public Stream()
  {
    super("stream");
  }

  public Stream open() throws OpsException
  {
    try
    {
      debug("streaming: '", getInput(), "'");
      if (circular)
      {
        it = new CircularIterator<JsonNode>(
            JsonNodeIterator.fromPath(getInput()), getLimit());
      }
      else
      {
        it = JsonNodeIterator.fromPath(getInput());
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
    return new OpData(it.next()).asList();
  }
  
  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Stream(), args);
  }

  @Override
  public Iterator<JsonNode> getIterator()
  {
    return it;
  }
}
