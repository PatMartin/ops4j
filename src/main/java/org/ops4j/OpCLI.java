package org.ops4j;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import org.ops4j.exception.OpsException;
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

      op.initialize().open();

      // Open up a stream
      Iterator<JsonNode> jnIt;
      if (op instanceof JsonSource)
      {
        jnIt = ((JsonSource) op).getIterator();
      }
      else if (cli.getDataSource() != null)
      {
        InputSource<?> is = null;
        try
        {
          is = Ops4J.locator().resolveSource(cli.getDataSource());
        }
        catch(OpsException opsEx)
        {

        }
        try
        {
          count = Integer.parseInt(cli.getDataSource());
        }
        catch(Exception ex)
        {

        }

        if (is != null)
        {
          jnIt = JsonNodeIterator.fromInputStream(is.stream());
        }
        else if (count > 0)
        {
          jnIt = new CountdownIterator(Integer.parseInt(cli.getDataSource()));
        }
        else
        {
          throw new OpsException("Invalid data source.");
        }
      }
      else if (count > 0)
      {
        jnIt = new CountdownIterator(count);
      }
      else
      {
        jnIt = JsonNodeIterator.fromInputStream(System.in);
      }

      while (jnIt.hasNext() && (count == 0 || (currentCount < count)))
      {
        currentCount++;

        JsonNode node = jnIt.next();
        // System.err.println(op.getName() + ": " + JacksonUtil.toString(node));
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
              System.out.println(out.toString());
            }
          }
        }
      }
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }

    op.close().cleanup();
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
