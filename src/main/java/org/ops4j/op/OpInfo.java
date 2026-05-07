package org.ops4j.op;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class)
@Command(name = "op-info", description = "Retrieve system information.")
public class OpInfo extends BaseOp<OpInfo> implements JsonSource
{
  @Option(names = { "-t", "--target" },
      description = "An optional target node.")
  private @Getter @Setter String target = null;

  private Iterator<JsonNode>     it;
  private List<JsonNode>         list   = new ArrayList<>();

  public OpInfo() throws OpsException
  {
    super("op-info");
  }

  public OpInfo initialize() throws OpsException
  {
    // System.out.println(JacksonUtil.toPrettyString(Ops4J.getInfo()));
    try
    {
      ArrayNode info = Ops4J.getInfo();
      it = info.iterator();
    }
    catch(Exception ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    if (it.hasNext())
    {
      JsonNode data = it.next();
      try
      {
        if (getTarget() != null)
        {
          JsonNode json = input.toJson();
          JacksonUtil.put(getTarget(), json, data);
          return OpData.from(json).asList();
        }
        else
        {
          return OpData.from(data).asList();
        }
      }
      catch(Exception ex)
      {
        throw new OpsException(ex);
      }
    }
    return null;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new OpInfo(), args);
  }

  @Override
  public Iterator<JsonNode> getIterator()
  {
    return it;
  }
}
