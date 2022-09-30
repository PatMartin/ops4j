package org.ops4j.cmd;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.ops4j.NodeOp;
import org.ops4j.Op;
import org.ops4j.Ops4J;
import org.ops4j.buddies.StringBuddy;
import org.ops4j.io.InputSource;
import org.ops4j.io.OutputDestination;
import org.ops4j.util.StringUtil;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "toc", description = "Table of contents.")
public class TocCmd extends SubCmd implements Callable<Integer>
{
  public TocCmd()
  {
  }

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
    }
    Map<String, Op<?>> ops = Ops4J.locator().getOps();
    Map<String, NodeOp<?>> nodeOps = Ops4J.locator().getNodeOps();
    Map<String, InputSource<?>> sources = Ops4J.locator().getSources();
    Map<String, OutputDestination<?>> destinations = Ops4J.locator()
        .getDestinations();
    System.out.println(StringBuddy.from("OPERATIONS").banner("-", 40));

    System.out.println(StringUtil.align(
        ops.values().stream().map(Op::getName).collect(Collectors.toList()))
        + "\n");

    System.out
        .println(StringBuddy.from("NODE-OPERATIONS").banner("-", 40) + "");
    System.out.println(StringUtil.align(nodeOps.values().stream()
        .map(NodeOp::getName).collect(Collectors.toList())) + "\n");
    System.out.println(StringBuddy.from("INPUT-SOURCES").banner("-", 40));
    System.out.println(StringUtil.align(sources.values().stream()
        .map(InputSource::getName).collect(Collectors.toList())) + "\n");
    System.out.println(StringBuddy.from("OUTPUT-DESTINATIONS").banner("-", 40));
    System.out
        .println(StringUtil
            .align(destinations.values().stream()
                .map(OutputDestination::getName).collect(Collectors.toList()))
            + "\n");
    return 0;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new TocCmd());
    cli.execute(args);
  }
}
