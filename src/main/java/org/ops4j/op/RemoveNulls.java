package org.ops4j.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class)
@Command(name = "remove-nulls", description = "Remove null nodes from a document.")
public class RemoveNulls extends BaseOp<RemoveNulls>
{
  public RemoveNulls()
  {
    super("remove-nulls");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    JacksonUtil.removeNulls(input.getJson());
    return input.asList();
  }
  
  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new RemoveNulls(), args);
  }
}
