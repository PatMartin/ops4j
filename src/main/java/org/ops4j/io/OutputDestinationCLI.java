package org.ops4j.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.concurrent.Callable;

import org.ops4j.InputSource;
import org.ops4j.Ops4J;
import org.ops4j.OutputDestination;
import org.ops4j.exception.OpsException;
import org.ops4j.util.CountdownIterator;
import org.ops4j.util.JsonNodeIterator;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

@Command(name = "Output Destination CLI Options:",
    mixinStandardHelpOptions = false,
    description = "This CLI provides an interface for working with output "
        + "destinatons.")
public class OutputDestinationCLI implements Callable<Integer>
{
  enum OutputType {
    NONE, ALL
  }

  @Option(names = { "-D", "--data-source" }, required = false,
      description = "The datasource.")
  private @Getter @Setter String     dataSource = null;

  @Option(names = { "-O", "--output" }, required = false,
      description = "The output format for " + "this operation.")
  private @Getter @Setter OutputType outputType = OutputType.ALL;

  @Option(names = { "-b", "-buffer-size" }, required = false,
      description = "Set the size of the input buffer.")
  private @Getter @Setter int        bufferSize = 1024;

  public OutputDestinationCLI()
  {
  }

  @Option(names = { "-h", "--help" }, description = "Get help.")
  private @Getter @Setter boolean usage = false;

  @Option(names = { "-H", "--HELP" }, description = "Get detailed help.")
  private @Getter @Setter boolean help  = false;

  public static int cli(OutputDestination<?> dest, String[] args)
      throws OpsException
  {
    int currentCount = 0;
    int count = 0;
    CommandLine cmd = new CommandLine(dest);
    try
    {
      OutputDestinationCLI cli = new OutputDestinationCLI();
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
        System.out.println("Class: " + dest.getClass().getName());
        return 0;
      }

      dest.configure((String[]) result.unmatched().toArray(new String[0]));
      // Required, to synchronize backing logger log-level to op's -L log level.
      dest.setLogLevel(dest.getLogLevel());

      OutputStream os = dest.stream();

      byte[] buffer = new byte[cli.getBufferSize()];

      // Open up a stream
      Iterator<JsonNode> jnIt;

      if (cli.getDataSource() != null)
      {
        InputSource<?> is = null;
        try
        {
          is = Ops4J.locator().resolveSource(cli.getDataSource());
          count = Integer.parseInt(cli.getDataSource());
        }
        catch(OpsException opsEx)
        {

        }
        if (is != null)
        {
          jnIt = JsonNodeIterator.fromInputStream(is.stream());
        }
        else if (count > 0)
        {
          jnIt = new CountdownIterator(count);
        }
        else
        {
          throw new OpsException("Invalid data source.");
        }
      }
      else
      {
        jnIt = JsonNodeIterator.fromInputStream(System.in);
      }

      while (jnIt.hasNext()
          && (count == 0 || (count > 0 && currentCount < count)))
      {
        currentCount++;
        JsonNode node = jnIt.next();

        switch (cli.getOutputType())
        {
          case NONE:
          {
            break;
          }
          default:
          {
            os.write(node.toString().getBytes());
          }
        }
      }
      os.close();
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
