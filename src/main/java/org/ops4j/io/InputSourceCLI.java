package org.ops4j.io;

import java.io.InputStream;
import java.util.concurrent.Callable;

import org.ops4j.InputSource;
import org.ops4j.exception.OpsException;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParseResult;

@Command(name = "Input Source CLI Options:", mixinStandardHelpOptions = false,
    description = "This CLI provides an interface for working with input "
        + "sources.")
public class InputSourceCLI implements Callable<Integer>
{
  enum OutputType {
    NONE, ALL
  }

  @Option(names = { "-O", "--output" }, required = false,
      description = "The output format for " + "this operation.")
  private @Getter @Setter OutputType outputType = OutputType.ALL;

  @Option(names = { "-b", "-buffer-size" }, required = false,
      description = "Set the size of the input buffer.")
  private @Getter @Setter int bufferSize = 1024;

  public InputSourceCLI()
  {
  }

  @Option(names = { "-h", "--help" }, description = "Get help.")
  private @Getter @Setter boolean usage = false;

  @Option(names = { "-H", "--HELP" }, description = "Get detailed help.")
  private @Getter @Setter boolean help  = false;

  public static int cli(InputSource<?> source, String[] args)
      throws OpsException
  {
    CommandLine cmd = new CommandLine(source);
    try
    {
      InputSourceCLI cli = new InputSourceCLI();
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
        System.out.println("Class: " + source.getClass().getName());
        return 0;
      }

      source.configure((String[]) result.unmatched().toArray(new String[0]));
      // Required, to synchronize backing logger log-level to op's -L log level.
      source.setLogLevel(source.getLogLevel());

      InputStream is = source.stream();

      byte[] buffer = new byte[cli.getBufferSize()];

      for (int numRead; (numRead = is.read(buffer, 0, buffer.length)) > 0;)
      {
        if (cli.getOutputType() != OutputType.NONE)
        {
          System.out.write(buffer, 0, numRead);
        }
      }
      is.close();
    }
    catch(Exception ex)
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
