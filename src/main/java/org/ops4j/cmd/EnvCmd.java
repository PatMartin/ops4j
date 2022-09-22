package org.ops4j.cmd;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.ops4j.InputSource;
import org.ops4j.NodeOp;
import org.ops4j.Op;
import org.ops4j.Ops4J;
import org.ops4j.OutputDestination;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "env", mixinStandardHelpOptions = true,
    description = "Output environmental information.")
public class EnvCmd extends SubCmd implements Callable<Integer>
{
  @Option(names = { "-nodeop-ns", "-node-op-namespace" }, required = false,
      description = "An optional namespace to be prepended to operation "
          + "aliases to prevent any collisions with existing OS utilities "
          + "or other ops4j aliases.")
  private @Getter @Setter String nodeOpNamespace = "";

  @Option(names = { "-op-ns", "-op-namespace" }, required = false,
      description = "An optional namespace which will be prepended to node "
          + "operation aliases to prevent collisions with existing OS "
          + "utilities or other ops4j aliases.")
  private @Getter @Setter String opNamespace     = "";

  
  @Option(names = { "-is-ns", "-input-source-namespace" }, required = false,
      description = "An optional namespace which will be prepended to input "
          + "source aliases to prevent collisions with existing OS "
          + "utilities or other ops4j aliases.")
  private @Getter @Setter String isNamespace     = "";
  
  @Option(names = { "-os-ns", "-output-dest-namespace" }, required = false,
      description = "An optional namespace which will be prepended to output "
          + "destination aliases to prevent collisions with existing OS "
          + "utilities or other ops4j aliases.")
  private @Getter @Setter String osNamespace     = "";
  
  public EnvCmd()
  {
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
      return 0;
    }
    System.out.println("####################");
    System.out.println("## Operation aliases");
    System.out.println("####################");
    Map<String, Op<?>> ops = new TreeMap<String, Op<?>>(
        Ops4J.locator().getOps());
    for (String name : ops.keySet())
    {
      System.out.println("alias " + getOpNamespace() + name + "=\"${JAVA} "
          + ops.get(name).getClass().getName() + "\"");
    }

    System.out.println("\n########################");
    System.out.println("# Node operation aliases");
    System.out.println("########################");
    Map<String, NodeOp<?>> nodeOps = new TreeMap<String, NodeOp<?>>(
        Ops4J.locator().getNodeOps());
    for (String name : nodeOps.keySet())
    {
      System.out.println("alias " + getNodeOpNamespace() + name + "=\"${JAVA} "
          + nodeOps.get(name).getClass().getName() + "\"");
    }
    
    System.out.println("\n########################");
    System.out.println("# Input source aliases");
    System.out.println("########################");
    Map<String, InputSource<?>> sources = new TreeMap<String, InputSource<?>>(
        Ops4J.locator().getSources());
    for (String name : sources.keySet())
    {
      System.out.println("alias " + getIsNamespace() + name + "=\"${JAVA} "
          + sources.get(name).getClass().getName() + "\"");
    }
    
    System.out.println("\n############################");
    System.out.println("# Output destination aliases");
    System.out.println("############################");
    Map<String, OutputDestination<?>> destinations = new TreeMap<String, OutputDestination<?>>(
        Ops4J.locator().getDestinations());
    for (String name : destinations.keySet())
    {
      System.out.println("alias " + getOsNamespace() + name + "=\"${JAVA} "
          + sources.get(name).getClass().getName() + "\"");
    }
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new EnvCmd());
    cli.execute(args);
  }
}
