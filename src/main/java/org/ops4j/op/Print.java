package org.ops4j.op;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "print",
    description = "Print the specified message to the specified output "
        + "channel.")
public class Print extends BaseOp<Print>
{
  @Parameters(index = "0", arity = "0..+", description = "The messages to print.")
  private @Getter @Setter List<String> messages = new ArrayList<>();

  public Print()
  {
    super("print");
  }

  public List<OpData> execute(OpData input)
  {
    for (String message : getMessages())
    {
      System.err.println(JacksonUtil.interpolate(message, input.getJson()));
    }
    return input.asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Print(), args);
  }
}
