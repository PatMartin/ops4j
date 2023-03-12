package org.ops4j.op.sys;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpModule;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.node.ArrayNode;
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

  private ObjectNode             info;

  public SysInfo() throws OpsException
  {
    super("sys:info");
    if (getTarget() == null) {
      // Initialize only mode
      provides(PhaseType.INITIALIZE);
    }
  }

  public SysInfo initialize() throws OpsException
  {
    info = JacksonUtil.createObjectNode();
    ArrayNode modulesInfo = JacksonUtil.createArrayNode();
    Map<String, OpModule<?>> modules = Ops4J.locator().getModules();
    for (Entry<String, OpModule<?>> module : modules.entrySet())
    {
      ObjectNode moduleInfo = JacksonUtil.createObjectNode();
      moduleInfo.put("name", module.getValue().getName());
      moduleInfo.put("namespace", module.getValue().getNamespace());
      modulesInfo.add(moduleInfo);
    }
    info.set("modules", modulesInfo);

    ArrayNode opsInfo = JacksonUtil.createArrayNode();
    Map<String, Op<?>> ops = Ops4J.locator().getOps();
    for (Entry<String, Op<?>> op : ops.entrySet())
    {
      ObjectNode opInfo = JacksonUtil.createObjectNode();
      opInfo.put("name", op.getValue().getName());
      opInfo.put("class-name", op.getValue().getClass().getName());
      opsInfo.add(opInfo);
    }
    info.set("ops", opsInfo);
    // Map<String, NodeOp<?>> nodeOps = Ops4J.locator().getNodeOps();

    // Map<String, OpRepo> repos = Ops4J.locator().getRepos();
    // Map<String, OutputDestination<?>> destinations = Ops4J.locator()
    // .getDestinations();
    // Map<String, InputSource<?>> sources = Ops4J.locator().getSources();
    if (getTarget() == null)
    {
      System.out.println(JacksonUtil.toPrettyString(info));
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    if (getTarget() != null)
    {
      JacksonUtil.put(getTarget(), input.getJson(), info);
    }
    return input.asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new SysInfo(), args);
  }
}
