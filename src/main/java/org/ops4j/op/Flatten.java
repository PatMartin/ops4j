package org.ops4j.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.buddies.JsonBuddy;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class)
@Command(name = "flatten", description = "Flatten a nested JSON.")
public class Flatten extends BaseOp<Flatten>
{
  public Flatten()
  {
    super("flatten");
    lifecycle().willProvide(PhaseType.EXECUTE);
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    return new OpData(new JsonBuddy(input.getJson()).flatten().json()).asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Flatten(), args);
  }
}
