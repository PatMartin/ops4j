package org.ops4j.op.test;

import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(Op.class)
@Command(name = "logtest", description = "This operation does nothing.")
public class LogTest extends BaseOp<LogTest>
{
  public LogTest()
  {
    super("logtest");
  }

  public LogTest initialize() throws OpsException
  {
    error("ERROR");
    warn("WARN");
    info("INFO");
    debug("DEBUG");
    trace("TRACE");
    return this;
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new LogTest(), args);
  }
}
