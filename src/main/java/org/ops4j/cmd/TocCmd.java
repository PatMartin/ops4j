package org.ops4j.cmd;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ops4j.Ops4J;
import org.ops4j.buddies.StringBuddy;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpModule;
import org.ops4j.io.InputSource;
import org.ops4j.io.OutputDestination;
import org.ops4j.util.StringUtil;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "toc", description = "Table of contents.")
public class TocCmd extends SubCmd implements Callable<Integer>
{
  private enum TocTypes {
    OP, NODEOP, ALL, SRC, DST, MODULE
  };

  @Parameters(index = "0", arity = "0..1", paramLabel = "<pattern>",
      description = "A search pattern.")
  private @Getter @Setter String   pattern = null;

  @Option(names = { "-t", "--type" }, description = "The type.")
  private @Getter @Setter TocTypes type    = TocTypes.ALL;

  private class OpModuleComparator implements Comparator<OpModule>
  {
    @Override
    public int compare(OpModule module1, OpModule module2)
    {
      if (module1 == null)
      {
        if (module2 == null)
        {
          return 0;
        }
        return -1;
      }
      if (module2 == null)
      {
        return 1;
      }

      return (module1.getName().compareTo(module2.getName()));
    }
  }

  private class OpComparator implements Comparator<Op>
  {
    @Override
    public int compare(Op op1, Op op2)
    {
      if (op1 == null)
      {
        if (op2 == null)
        {
          return 0;
        }
        return -1;
      }
      if (op2 == null)
      {
        return 1;
      }

      return (op1.getName().compareTo(op2.getName()));
    }
  }

  private class NodeOpComparator implements Comparator<NodeOp>
  {
    @Override
    public int compare(NodeOp op1, NodeOp op2)
    {
      if (op1 == null)
      {
        if (op2 == null)
        {
          return 0;
        }
        return -1;
      }
      if (op2 == null)
      {
        return 1;
      }

      return (op1.getName().compareTo(op2.getName()));
    }
  }

  public TocCmd()
  {
    super("toc");
  }

  private void modulesToc() throws OpsException
  {
    Map<String, OpModule<?>> modules = Ops4J.locator().getModules();
    System.out.println(StringBuddy.from("MODULES").banner("-", 60));

    System.out.println(StringUtil.align(modules.values().stream()
        .sorted(new OpModuleComparator()).map(OpModule::getName)
        .filter(name -> getPattern() == null || name.indexOf(getPattern()) > -1)
        .collect(Collectors.toList())) + "\n");
  }

  private void opsToc() throws OpsException
  {
    Map<String, Op<?>> ops = Ops4J.locator().getOps();
    System.out.println(StringBuddy.from("OPERATIONS").banner("-", 60));

    System.out.println(StringUtil
        .align(ops.values().stream().sorted(new OpComparator()).map(Op::getName)
            .filter(
                name -> getPattern() == null || name.indexOf(getPattern()) > -1)
            .collect(Collectors.toList()))
        + "\n");
  }

  private void nodeopsToc() throws OpsException
  {
    Map<String, NodeOp<?>> nodeOps = Ops4J.locator().getNodeOps();
    System.out
        .println(StringBuddy.from("NODE-OPERATIONS").banner("-", 60) + "");
    System.out.println(StringUtil.align(nodeOps.values().stream()
        .sorted(new NodeOpComparator()).map(NodeOp::getName)
        .filter(name -> getPattern() == null || name.indexOf(getPattern()) > -1)
        .collect(Collectors.toList())) + "\n");
  }

  private void srcToc() throws OpsException
  {
    Map<String, InputSource<?>> sources = Ops4J.locator().getSources();
    System.out.println(StringBuddy.from("INPUT-SOURCES").banner("-", 60));
    System.out.println(
        StringUtil.align(sources.values().stream().map(InputSource::getName)
            .filter(
                name -> getPattern() == null || name.indexOf(getPattern()) > -1)
            .collect(Collectors.toList())) + "\n");
  }

  private void dstToc() throws OpsException
  {
    Map<String, OutputDestination<?>> destinations = Ops4J.locator()
        .getDestinations();
    System.out.println(StringBuddy.from("OUTPUT-DESTINATIONS").banner("-", 60));
    System.out.println(StringUtil
        .align(destinations.values().stream().map(OutputDestination::getName)
            .filter(
                name -> getPattern() == null || name.indexOf(getPattern()) > -1)
            .collect(Collectors.toList()))
        + "\n");
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
      return 0;
    }

    if (getType() == TocTypes.ALL || getType() == TocTypes.OP)
    {
      opsToc();
    }
    if (getType() == TocTypes.ALL || getType() == TocTypes.NODEOP)
    {
      nodeopsToc();
    }
    if (getType() == TocTypes.ALL || getType() == TocTypes.SRC)
    {
      srcToc();
    }
    if (getType() == TocTypes.ALL || getType() == TocTypes.DST)
    {
      dstToc();
    }
    if (getType() == TocTypes.ALL || getType() == TocTypes.MODULE)
    {
      modulesToc();
    }
    
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new TocCmd());
    cli.execute(args);
  }
}
