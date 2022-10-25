package org.ops4j.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.inf.Op.PhaseType;
import org.ops4j.io.InputSource;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLogger.LogLevel;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.log.OpLogging;
import org.ops4j.util.CountdownIterator;
import org.ops4j.util.JacksonUtil;
import org.ops4j.util.JsonNodeIterator;

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
    NONE, JSON, YAML, XML, CBOR
  }

  enum SerializationType {
    JSON, YAML, XML, CBOR
  }

  @Option(names = { "-O", "--output" },
      description = "The output format for this operation.")
  private @Getter
  @Setter OutputType                            outputType        = OutputType.JSON;

  @Option(names = { "-S", "--serialize" },
      description = "When supplied, serialize the operation in the given "
          + "output format.")
  private @Getter @Setter SerializationType     serializationType = null;

  @Option(names = { "-P", "--pretty" }, description = "Pretty print output.")
  private @Getter @Setter boolean               pretty            = false;

  @Option(names = { "-h", "--help" }, description = "Get help.")
  private @Getter @Setter boolean               usage             = false;

  @Option(names = { "-H", "--HELP" }, description = "Get detailed help.")
  private @Getter @Setter boolean               help              = false;

  @Option(names = { "-D", "--data-source" }, required = false,
      description = "The datasource.")
  private @Getter @Setter String                dataSource        = null;

  @Option(names = { "-LL" }, required = false,
      description = "Set subsystem loggers.")

  private @Getter
  @Setter Map<String, LogLevel>                 logLevels         = new HashMap<>();

  public static int cli(Op<?> op, String[] args) throws OpsException
  {
    CommandLine cmd = new CommandLine(op);
    int currentCount = 0;
    int count = 0;
    OpLogger logger;

    try
    {
      OpCLI cli = new OpCLI();
      CommandLine cliCmd = new CommandLine(cli);
      cliCmd.setCaseInsensitiveEnumValuesAllowed(true).getCommandSpec().parser()
          .collectErrors(true);
      ParseResult parseResult = cliCmd.parseArgs(args);

      OpLoggerFactory.setLogLevels(cli.getLogLevels());
      logger = OpLoggerFactory.getLogger("ops.cli");
      logger.DEBUG("Running: op=", op.getName());

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

      op.configure(parseResult.unmatched());
      // Required, to synchronize backing logger log-level to op's -L log level.
      op.setLogLevel(op.getLogLevel());

      if (op.provides(PhaseType.INITIALIZE))
      {
        logger.DEBUG("Initializing: ", op.getName());
        op.initialize();
        logger.DEBUG("Initialized : ", op.getName());
      }

      if (cli.getSerializationType() == SerializationType.JSON)
      {
        if (cli.isPretty())
        {
          System.out.println(JacksonUtil.toPrettyString(op));
        }
        else
        {
          System.out.println(JacksonUtil.toString(op));
        }
        return 0;
      }
      if (cli.getSerializationType() == SerializationType.XML)
      {
        if (cli.isPretty())
        {
          System.out
              .println(JacksonUtil.toString(JacksonUtil.prettyXmlMapper(), op));
        }
        else
        {
          System.out.println(JacksonUtil.toXmlString(op));
        }
        return 0;
      }

      if (cli.getSerializationType() == SerializationType.CBOR)
      {
        System.out.write(JacksonUtil.toCborString(op));
        return 0;
      }

      if (op.provides(PhaseType.OPEN))
      {
        logger.DEBUG("Opening: ", op.getName());
        op.open();
        logger.DEBUG("Opened : ", op.getName());
      }

      if (op.provides(PhaseType.EXECUTE))
      {
        // Open up a stream
        Iterator<JsonNode> jnIt = null;
        if (op instanceof JsonSource)
        {
          logger.DEBUG("Datasource Detected: ", op.getName());
          jnIt = ((JsonSource) op).getIterator();
        }
        else if (cli.getDataSource() != null)
        {
          logger.DEBUG("Resolving datasource: '", cli.getDataSource(), "'");
          InputSource<?> is = null;
          try
          {
            count = Integer.parseInt(cli.getDataSource());
            logger.DEBUG("Artificial datasource of ", count, " empty records.");
            jnIt = new CountdownIterator(count);
          }
          catch(Exception ex)
          {
            try
            {
              logger.DEBUG("Resolving via Locator: '", cli.getDataSource(),
                  "'");
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
          logger.DEBUG("Reading from standard input");
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
          for (OpData result : results)
          {
            output(cli, result);
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
      logger.DEBUG("Closing ", op.getName());
      op.close();
      logger.DEBUG("Closed ", op.getName());
    }
    if (op.provides(PhaseType.CLEANUP))
    {
      logger.DEBUG("Cleanup ", op.getName());
      op.cleanup();
      logger.DEBUG("Cleaned up ", op.getName());
    }
    return 0;
  }

  public static void output(OpCLI cli, OpData result)
      throws OpsException, IOException
  {
    switch (cli.getOutputType())
    {
      case NONE:
      {
        break;
      }
      case XML:
      {
        if (cli.isPretty())
        {
          System.out.println(result.toPrettyXml());
        }
        else
        {
          System.out.println(result.toXml());
        }
        break;
      }
      case YAML:
      {
        System.out.println(result.toYaml());
        break;
      }
      case CBOR:
      {
        System.out.write(result.toCbor());
        break;
      }
      default:
      {
        if (result == null)
        {
          System.err.println("result is null");
        }
        else if (cli.isPretty())
        {
          System.out.println(result.toPrettyString());
        }
        else
        {
          System.out.println(result.toString());
        }
      }
    }
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
