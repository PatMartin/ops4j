package org.ops4j.cli;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.ops4j.Op;
import org.ops4j.Op.PhaseType;
import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.io.InputSource;
import org.ops4j.util.CountdownIterator;
import org.ops4j.util.JsonNodeIterator;
import org.ops4j.util.JsonSource;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

@Command(name = "Additional CLI Options:", mixinStandardHelpOptions = false,
    description = "OpCli description")
public class OpCLI implements Callable<Integer>
{
  public OpCLI()
  {
  }

  enum OutputType {
    NONE, JSON, YAML, XML
  }

  @Option(names = { "-O", "--output" },
      description = "The output format for " + "this operation.")
  private @Getter @Setter OutputType outputType = OutputType.JSON;

  @Option(names = { "-P", "--pretty" }, description = "Pretty print output.")
  private @Getter @Setter boolean    pretty     = false;

  @Option(names = { "-h", "--help" }, description = "Get help.")
  private @Getter @Setter boolean    usage      = false;

  @Option(names = { "-H", "--HELP" }, description = "Get detailed help.")
  private @Getter @Setter boolean    help       = false;

  @Option(names = { "-D", "--data-source" }, required = false,
      description = "The datasource.")
  private @Getter @Setter String     dataSource = null;

  public static int cli(Op<?> op, String[] args) throws OpsException
  {
    CommandLine cmd = new CommandLine(op);
    int currentCount = 0;
    int count = 0;

    try
    {
      OpCLI cli = new OpCLI();
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

      op.configure(result.unmatched());
      // Required, to synchronize backing logger log-level to op's -L log level.
      op.setLogLevel(op.getLogLevel());

      if (op.provides(PhaseType.INITIALIZE))
      {
        op.initialize();
      }

      if (op.provides(PhaseType.OPEN))
      {
        op.open();
      }

      if (op.provides(PhaseType.EXECUTE))
      {
        // Open up a stream
        Iterator<JsonNode> jnIt = null;
        if (op instanceof JsonSource)
        {
          jnIt = ((JsonSource) op).getIterator();
        }
        else if (cli.getDataSource() != null)
        {
          InputSource<?> is = null;
          try
          {
            count = Integer.parseInt(cli.getDataSource());
            jnIt = new CountdownIterator(count);
          }
          catch(Exception ex)
          {
            ex.printStackTrace();
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

          JsonNode node = jnIt.next();
          // System.err.println(op.getName() + ": " +
          // JacksonUtil.toString(node));
          OpData data = new OpData(node);
          // System.err.println(op.getName() + ": " + data);
          List<OpData> results = op.execute(data);
          for (OpData out : results)
          {
            switch (cli.getOutputType())
            {
              case NONE:
              {
                break;
              }
              default:
              {
                if (cli.isPretty())
                {
                  System.out.println(out.toPrettyString());
                }
                else
                {
                  System.out.println(out.toString());
                }
              }
            }
          }
        }
      }
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }

    if (op.provides(PhaseType.CLOSE))
    {
      op.close();
    }
    if (op.provides(PhaseType.CLEANUP))
    {
      op.cleanup();
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
