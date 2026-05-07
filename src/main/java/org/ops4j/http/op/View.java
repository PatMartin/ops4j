package org.ops4j.http.op;

import java.io.IOException;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "vw", description = "View a web asset.")
public class View extends BaseOp<View>
{
  @Parameters(index="0", arity="0..+", description="Run a process")
  private @Getter @Setter List<String> args;
  
  private Process               process = null;

  public View()
  {
    super("vw");
  }

  public View initialize(OpData input) throws OpsException
  {
    try
    {
      DEBUG("ARGS", getArgs());
      ProcessBuilder pb = new ProcessBuilder("nodepad.exe");
      process = pb.start();
      DEBUG("PROCESS: ", pb.toString());
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  @Override
  public List<OpData> execute(OpData input) throws OpsException
  {
    return input.asList();
  }

  @Override
  public List<OpData> close() throws OpsException
  {
    process.destroy();
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new View(), args);
  }
}
