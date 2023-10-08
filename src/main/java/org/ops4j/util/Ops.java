package org.ops4j.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpRepo;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;
import org.ops4j.op.Pipeline;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import picocli.CommandLine;

public class Ops
{
  private static OpLogger logger = OpLoggerFactory.getLogger("ops.create");

  enum PartType {
    CMD, STRING
  }

  public static List<Op<?>> parseCommands(@NonNull List<String> commands)
      throws OpsException
  {
    List<Op<?>> ops = new ArrayList<>();
    for (String cmd : commands)
    {
      logger.DEBUG("CMD: ", cmd);
      List<Op<?>> cmdOps = parseCommands(cmd);
      if (cmdOps.size() > 1)
      {
        ops.add(Pipeline.of(cmdOps));
      }
      else
      {
        ops.add(cmdOps.get(0));
      }
    }
    return ops;
  }

  public static List<Op<?>> parseCommands(String command) throws OpsException
  {
    logger.DEBUG("PARSING COMMAND: '", command, "'");
    class PartInfo
    {
      private @Getter @Setter PartType type;
      private @Getter @Setter String   value;
      private @Getter @Setter String   token;

      public PartInfo(String value, String token)
      {
        type = (value.startsWith("'") || value.startsWith("\""))
            ? PartType.STRING
            : PartType.CMD;
        setValue(value);
        setToken(token);
      }
    }

    // First pass, tokenize on quotes
    // Second pass, identify which is which
    // Third pass, replace them with sanitized tokens
    // Fourth pass, split on '|'
    // Fifth pass, interpolate the across each element.
    // Sixth pass, instantiate each operation

    // Find tokenize the quotes:
    List<String> parts = StringUtil.splitNestedQuotes(command);
    List<PartInfo> pass2 = new ArrayList<>();
    List<String> pass3 = new ArrayList<>();
    String[] pass4;
    List<String> pass5 = new ArrayList<>();
    List<Op<?>> ops = new ArrayList<>();

    int tokenNum = 1;

    for (String part : parts)
    {
      logger.DEBUG("PASS 1: part=", part);
      pass2.add(new PartInfo(part.trim(), "TOKEN" + tokenNum++));
    }

    for (PartInfo part : pass2)
    {
      logger.DEBUG("PASS 2: part=", part.value);
      if (part.value.startsWith("'") || part.value.startsWith("\""))
      {
        logger.DEBUG("PASS 3: part=", part.value, " => ", part.token);
        pass3.add(part.token);
      }
      else
      {
        logger.DEBUG("PASS 3: part=", part.value);
        pass3.add(part.value);
      }
    }

    // Fourth pass: split on '|'
    logger.DEBUG("PASS-3 RESULT: ", StringUtils.join(pass3, " "));

    pass4 = StringUtils.split(StringUtils.join(pass3, " "), '|');

    for (String part : pass4)
    {
      logger.DEBUG("PASS 4: part=", part);
    }

    // Interpolate:
    for (String part : pass4)
    {
      for (PartInfo pi : pass2)
      {
        if (pi.type == PartType.STRING)
        {
          part = part.replace(pi.token, pi.value);
        }
      }
      logger.DEBUG("PASS 5: part=", part.trim());
      pass5.add(part.trim());
    }

    // Pass 6: Instantiate
    for (String part : pass5)
    {
      Map<String, Op<?>> opmap = Ops4J.locator().getOps();

      int firstSpace = part.indexOf(" ");
      String name;
      String args[];
      if (firstSpace < 0)
      {
        name = part;
        args = new String[0];
      }
      else
      {
        name = part.substring(0, firstSpace);
        String rest = part.substring(firstSpace + 1);
        List<String> opArgs = StringUtil.splitNestedQuotes(rest);
        args = opArgs.toArray(new String[0]);
      }

      logger.DEBUG("NAME:", name);
      if (name.startsWith("@"))
      {
        String fn = name.substring(1);
        Op<?> op = Ops4J.repo().load(fn);
        ops.add(op);
      }
      else if (opmap.containsKey(name))
      {
        Op<?> ctor = opmap.get(name);
        Op<?> op = ctor.create();
        // CommandSpec spec = CommandSpec.create();
        logger.DEBUG("Ops.parseCommands(", op.getName(), "([",
            StringUtils.join(args, ","),
            "]))=" + JacksonUtil.toString(op, "N/A"));
        new CommandLine(op).parseArgs(args);

        ops.add(op);
      }
      else
      {
        logger.WARN("Invalid Command: '", name, "'");
      }
    }
    return ops;
  }

  public static void main(String args[]) throws OpsException
  {
    parseCommands("pipeline 'noop \"foo | bar\" | noop' | benchmark");
  }
}
