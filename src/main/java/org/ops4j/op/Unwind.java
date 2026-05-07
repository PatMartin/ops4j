package org.ops4j.op;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "unwind", description = "Unwind an array.")
public class Unwind extends BaseOp<Unwind>
{
  @Parameters(index = "0", arity = "0..*",
      description = "The number of milliseconds to pause.")
  private @Getter @Setter List<String> targets;

  public Unwind()
  {
    super("unwind");
  }

  // TODO: Finish coding this. Allow for multiple unwinds in single statement.
  // echo {f1:[A,B], f2:[1, 2]} | \
  // unwind /f1 /f2 => {f1:A, f2:1},{f1:B, f2:1}{f1:B,f2:1}{f1:B,f2:2}
  public List<OpData> execute(OpData input) throws OpsException
  {
    List<JsonNode> unwound = JacksonUtil.unwind(targets, input.getJson());
    List<OpData> results = new ArrayList<>();
    unwound.stream().forEach(node -> results.add(new OpData(node)));
    return results;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Unwind(), args);
  }
}
