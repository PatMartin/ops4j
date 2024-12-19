package org.ops4j.op;

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.it.StringOfLinesIterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "stream-lines", description = "Stream a text file as lines.")
public class StreamLines extends BaseOp<StreamLines> implements JsonSource
{
  @Parameters(index = "0", arity = "1", description = "The input file.")
  private @Getter @Setter String input;

  private Iterator<JsonNode>     it;

  public StreamLines()
  {
    super("stream-lines");
  }

  public StreamLines open() throws OpsException
  {
    try
    {
      //it = ArrayOfLinesIterator.from(getInput());
      it = StringOfLinesIterator.from(getInput());
    }
    catch(FileNotFoundException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    if (it.hasNext())
    {
      try
      {
        return OpData.from(it.next()).asList();
      }
      catch(JsonProcessingException ex)
      {
        throw new OpsException(ex);
      }
    }
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new StreamLines(), args);
  }

  @Override
  public Iterator<JsonNode> getIterator()
  {
    return it;
  }
}
