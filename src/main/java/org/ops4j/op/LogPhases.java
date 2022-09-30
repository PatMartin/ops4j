package org.ops4j.op;

import java.util.List;

import org.ops4j.BaseOp;
import org.ops4j.Op;
import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class)
@Command(name = "logphases", description = "This operation logs all phases.")
public class LogPhases extends BaseOp<LogPhases>
{
  public LogPhases()
  {
    super("logphases");
    // lifecycle().willProvide(PhaseType.INITIALIZE, PhaseType.OPEN,
    // PhaseType.CLEANUP, PhaseType.CLOSE);
  }

  public LogPhases initialize() throws OpsException
  {
    info("INITIALIZE");
    return this;
  }

  public LogPhases open() throws OpsException
  {
    info("OPEN");
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    info("EXECUTE");
    return input.asList();
  }

  public LogPhases close() throws OpsException
  {
    info("CLOSE");
    return this;
  }

  public LogPhases cleanup() throws OpsException
  {
    info("CLEANUP");
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new LogPhases(), args);
  }
}
