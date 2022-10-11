package org.ops4j.cmd;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.ops4j.Ops4J;
import org.ops4j.inf.NodeOp;
import org.ops4j.inf.Op;
import org.ops4j.io.InputSource;
import org.ops4j.io.OutputDestination;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "env", mixinStandardHelpOptions = true,
    description = "Output environmental information.")
public class EnvCmd extends SubCmd implements Callable<Integer>
{
  @Option(names = { "--unix" }, required = false,
      description = "When true, emit unix line separators.")
  private @Getter @Setter boolean unix            = false;

  @Option(names = { "--launcher" }, required = false,
      description = "An optional namespace to be prepended to operation "
          + "aliases to prevent any collisions with existing OS utilities "
          + "or other ops4j aliases.")
  private @Getter @Setter String  launcher        = "java";

  @Option(names = { "-nodeop-ns", "-node-op-namespace" }, required = false,
      description = "An optional namespace to be prepended to operation "
          + "aliases to prevent any collisions with existing OS utilities "
          + "or other ops4j aliases.")
  private @Getter @Setter String  nodeOpNamespace = "";

  @Option(names = { "-op-ns", "--op-namespace" }, required = false,
      description = "An optional namespace which will be prepended to node "
          + "operation aliases to prevent collisions with existing OS "
          + "utilities or other ops4j aliases.")
  private @Getter @Setter String  opNamespace     = "";

  @Option(names = { "-in-ns", "-input-source-namespace" }, required = false,
      description = "An optional namespace which will be prepended to input "
          + "source aliases to prevent collisions with existing OS "
          + "utilities or other ops4j aliases.")
  private @Getter @Setter String  isNamespace     = "";

  @Option(names = { "-out-ns", "--output-dest-namespace" }, required = false,
      description = "An optional namespace which will be prepended to output "
          + "destination aliases to prevent collisions with existing OS "
          + "utilities or other ops4j aliases.")
  private @Getter @Setter String  osNamespace     = "";

  public EnvCmd()
  {
  }

  @Override
  public Integer call() throws Exception
  {
    String lineSep = (isUnix()) ? "\n" : "\n\r";
    if (isHelp())
    {
      help(this);
      return 0;
    }
    System.out.print("####################" + lineSep);
    System.out.print("## Operation aliases" + lineSep);
    System.out.print("####################" + lineSep);
    Map<String, Op<?>> ops = new TreeMap<String, Op<?>>(
        Ops4J.locator().getOps());
    for (String name : ops.keySet())
    {
      System.out
          .print("alias " + getOpNamespace() + name + "=\"" + getLauncher()
              + " " + ops.get(name).getClass().getName() + "\"" + lineSep);
    }

    System.out.print("\n########################" + lineSep);
    System.out.print("# Node operation aliases" + lineSep);
    System.out.print("########################" + lineSep);
    Map<String, NodeOp<?>> nodeOps = new TreeMap<String, NodeOp<?>>(
        Ops4J.locator().getNodeOps());
    for (String name : nodeOps.keySet())
    {
      System.out
          .print("alias " + getNodeOpNamespace() + name + "=\"" + getLauncher()
              + " " + nodeOps.get(name).getClass().getName() + "\"" + lineSep);
    }

    System.out.print("\n########################" + lineSep);
    System.out.print("# Input source aliases" + lineSep);
    System.out.print("########################" + lineSep);
    Map<String, InputSource<?>> sources = new TreeMap<String, InputSource<?>>(
        Ops4J.locator().getSources());
    for (String name : sources.keySet())
    {
      System.out
          .print("alias " + getIsNamespace() + name + "=\"" + getLauncher()
              + " " + sources.get(name).getClass().getName() + "\"" + lineSep);
    }

    System.out.print("\n############################" + lineSep);
    System.out.print("# Output destination aliases" + lineSep);
    System.out.print("############################" + lineSep);
    Map<String, OutputDestination<?>> destinations = new TreeMap<String, OutputDestination<?>>(
        Ops4J.locator().getDestinations());
    for (String name : destinations.keySet())
    {
      System.out
          .print("alias " + getOsNamespace() + name + "=\"" + getLauncher()
              + " " + sources.get(name).getClass().getName() + "\"" + lineSep);
    }
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new EnvCmd());
    cli.execute(args);
  }
}
