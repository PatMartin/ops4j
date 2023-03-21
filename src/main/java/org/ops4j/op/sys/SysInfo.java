package org.ops4j.op.sys;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "sys:info", description = "Retrieve system information.")
public class SysInfo extends BaseOp<SysInfo>
{
  @Parameters(index = "0", arity = "0..1",
      description = "The number of milliseconds to pause.")
  private @Getter @Setter String target = null;

  public SysInfo() throws OpsException
  {
    super("sys:info");
    if (getTarget() == null)
    {
      // Initialize only mode
      provides(PhaseType.INITIALIZE);
    }
  }

  public SysInfo initialize() throws OpsException
  {
    if (getTarget() == null)
    {
      System.out.println(JacksonUtil.toPrettyString(Ops4J.getInfo()));
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    if (getTarget() != null)
    {
      JacksonUtil.put(getTarget(), input.getJson(), Ops4J.getInfo());
    }
    return input.asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new SysInfo(), args);
  }
}
