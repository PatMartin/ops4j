package org.ops4j.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.ops4j.NodeOp;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.io.InputSource;
import org.ops4j.util.CountdownIterator;
import org.ops4j.util.JacksonUtil;
import org.ops4j.util.JsonNodeIterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

@Command(name = "Additional CLI Options:", mixinStandardHelpOptions = false,
    description = "OpCli description")
public class NodeOpCLI implements Callable<Integer>
{
  public NodeOpCLI()
  {
  }

  enum OutputType {
    NONE, JSON, YAML, XML
  }

  @Parameters(index = "0", arity = "0..*",
      description = "Zero or more targets "
          + "to nodes contained in the JSON.  Default = /%n%nExamples:%n"
          + "/          = The root node.%n"
          + "/account   = A child node of the root named 'account'")
  private @Getter @Setter List<String> targets    = new ArrayList<String>();

  @Option(names = { "-O", "--output" },
      description = "The output format for " + "this operation.")
  private @Getter @Setter OutputType   outputType = OutputType.JSON;

  @Option(names = { "-h", "--help" }, description = "Get help.")
  private @Getter @Setter boolean      usage      = false;

  @Option(names = { "-H", "--HELP" }, description = "Get detailed help.")
  private @Getter @Setter boolean      help       = false;

  @Option(names = { "-D", "--data-source" }, required = false,
      description = "The datasource.")
  private @Getter @Setter String       dataSource = null;

  public static int cli(NodeOp<?> op, String[] args) throws OpsException
  {
    int currentCount = 0;
    CommandLine cmd = new CommandLine(op);
    try
    {
      NodeOpCLI cli = new NodeOpCLI();
      CommandLine cliCmd = new CommandLine(cli);
      cliCmd.getCommandSpec().parser().collectErrors(true);
      ParseResult result = cliCmd.parseArgs(args);

      if (cli.isUsage())
      {
        Help opHelp = new Help(cmd.getCommandSpec());
        System.out.println(opHelp.fullSynopsis());
        System.out.println(opHelp.description());
        Help cliHelp = new Help(cliCmd.getCommandSpec());
        System.out.println(cliHelp.synopsis(1));
        return 0;
      }
      else if (cli.isHelp())
      {
        Help opHelp = new Help(cmd.getCommandSpec());
        System.out.println(opHelp.fullSynopsis());
        System.out.println(opHelp.description());
        if (opHelp.parameterList().trim().length() > 0)
        {
          System.out.println(opHelp.parameterList());
        }
        if (opHelp.optionList().trim().length() > 0)
        {
          System.out.println(opHelp.optionList());
        }
        Help cliHelp = new Help(cliCmd.getCommandSpec());
        System.out.println(cliHelp.synopsis(1));
        System.out.println(cliHelp.optionList());
        System.out.println("Class: " + op.getClass().getName());
        return 0;
      }

      op.configure((String[]) result.unmatched().toArray(new String[0]));
      // Required, to synchronize backing logger log-level to op's -L log level.
      op.setLogLevel(op.getLogLevel());

      // Open up a stream
      Iterator<JsonNode> jnIt;
      if (cli.getDataSource() != null)
      {
        InputSource<?> is = null;
        try
        {
          jnIt = new CountdownIterator(Integer.parseInt(cli.getDataSource()));
        }
        catch(Exception ex)
        {
          try
          {
            is = Ops4J.locator().resolveSource(cli.getDataSource());
            jnIt = JsonNodeIterator.fromInputStream(is.stream());
          }
          catch(OpsException opsEx)
          {
            opsEx.printStackTrace();
            throw new OpsException("Invalid data source.", opsEx);
          }
        }
      }
      else
      {
        jnIt = JsonNodeIterator.fromInputStream(System.in);
      }

      while (jnIt.hasNext())
      {
        currentCount++;

        ObjectNode results = JacksonUtil.createObjectNode();
        JsonNode data = jnIt.next();

        List<String> targets;
        if (cli.getTargets().size() > 0)
        {
          targets = cli.getTargets();
        }
        else
        {
          targets = new ArrayList<String>(1);
          targets.add("/output");
        }
        JsonNode output = data;
        for (String target : targets)
        {
          // OpsLogger.syserr("RESULT: ", op.execute(data));
          output = JacksonUtil.put(target, data, op.execute(output));
        }
        switch (cli.getOutputType())
        {
          case NONE:
          {
            break;
          }
          default:
          {
            System.out.println(output.toString());
          }
        }
      }
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }

    return 0;
  }

  @Override
  public Integer call() throws Exception
  {
    System.out.println("CALLED");
    return 0;
  }

  public static void main(String args[])
  {
    // CommandLine commandLine = new CommandLine(new Ops4j())
    // .addSubcommand("status", new GitStatus())
    // .addSubcommand("commit", new GitCommit())
    // .addSubcommand("add", new GitAdd())
    // .addSubcommand("branch", new GitBranch())
  }
}
