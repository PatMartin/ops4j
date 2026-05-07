package org.ops4j.nodeop;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.ops4j.base.BaseNodeOp;
import org.ops4j.cli.NodeOpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.NodeOp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(NodeOp.class)
@Command(name = "now", mixinStandardHelpOptions = false,
    description = "Returns current time as milliseconds " + "since 1/1/1970")
public class Now extends BaseNodeOp<Now>
{
  enum TimeFormat {
    ISO8601, ISO8601C, UNIX, POSIX, SIMPLE, MONTH, YEAR, DAY, LOG, EPOCH
  }

  @Parameters(index = "0", arity = "0..1", description = "The time format.")
  private @Getter @Setter TimeFormat format = TimeFormat.ISO8601;

  @Option(names = { "-o", "-offset" }, required = false,
      description = "An optional offset to be "
          + "applied to the value returned by the now node operation.")
  private @Getter @Setter Long       offset = 0L;

  private SimpleDateFormat           fmt    = null;

  public Now()
  {
    super("now");
  }

  public JsonNode execute(JsonNode input) throws OpsException
  {
    DEBUG("TIME FORMAT: ", getFormat());
    long time = System.currentTimeMillis() + offset;

    switch (getFormat())
    {
      case EPOCH:
      {
        return new LongNode(time);
      }
      case UNIX:
      case POSIX:
      {
        return new LongNode(time/1000);
      }
      case SIMPLE:
      {
        if (fmt == null)
        {
          fmt = new SimpleDateFormat("yyyy-MM-dd");
        }
        return new TextNode(fmt.format(new Date(time)));
      }
      case YEAR:
      {
        if (fmt == null)
        {
          fmt = new SimpleDateFormat("yyyy");
        }
        return new TextNode(fmt.format(new Date(time)));
      }
      case MONTH:
      {
        if (fmt == null)
        {
          fmt = new SimpleDateFormat("MM");
        }
        return new TextNode(fmt.format(new Date(time)));
      }
      case DAY:
      {
        if (fmt == null)
        {
          fmt = new SimpleDateFormat("dd");
        }
        return new TextNode(fmt.format(new Date(time)));
      }
      case LOG:
      {
        if (fmt == null)
        {
          fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return new TextNode(fmt.format(new Date(time)));
      }
      case ISO8601C:
      {
        if (fmt == null)
        {
          fmt = new SimpleDateFormat("yyyyMMddT'HHmmss");
        }
        return new TextNode(fmt.format(new Date(time)));
      }
      case ISO8601:
      default:
      {
        if (fmt == null)
        {
          fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        }
        return new TextNode(fmt.format(new Date(time)));
      }
    }
  }

  public static void main(String args[]) throws OpsException
  {
    NodeOpCLI.cli(new Now(), args);
  }
}
