package org.ops4j.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.Ops4J;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.log.OpLogger;
import org.ops4j.log.OpLoggerFactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import picocli.CommandLine;

public class Ops
{
  private static OpLogger logger;

  // pipeline "noop | benchmark -L DEBUG" -L INFO
  public static OpLogger getLogger()
  {
    if (logger == null)
    {
      logger = OpLoggerFactory.getLogger("ops.create");
    }
    return logger;
  }

  enum PartType {
    CMD, STRING
  }

  public static List<Op<?>> parseCommands(@NonNull String command)
      throws OpsException
  {

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
      getLogger().DEBUG("PASS 1: part=", part);
      pass2.add(new PartInfo(part.trim(), "TOKEN" + tokenNum++));
    }

    for (PartInfo part : pass2)
    {
      getLogger().DEBUG("PASS 2: part=", part.value);
      if (part.value.startsWith("'") || part.value.startsWith("\""))
      {
        getLogger().DEBUG("PASS 3: part=", part.value, " => ", part.token);
        pass3.add(part.token);
      }
      else
      {
        getLogger().DEBUG("PASS 3: part=", part.value);
        pass3.add(part.value);
      }
    }

    // Fourth pass: split on '|'
    getLogger().DEBUG("PASS-3 RESULT: ", StringUtils.join(pass3, " "));

    pass4 = StringUtils.split(StringUtils.join(pass3, " "), '|');

    for (String part : pass4)
    {
      getLogger().DEBUG("PASS 4: part=", part);
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
      getLogger().DEBUG("PASS 5: part=", part.trim());
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

      if (opmap.containsKey(name))
      {
        Op<?> ctor = opmap.get(name);
        Op<?> op = ctor.create();
        // CommandSpec spec = CommandSpec.create();
        getLogger().DEBUG("Ops.parseCommands(", op.getName(), "([",
            StringUtils.join(args, ","),
            "]))=" + JacksonUtil.toString(op, "N/A"));
        new CommandLine(op).parseArgs(args);

        ops.add(op);
      }
    }
    return ops;
  }

  public static void main(String args[]) throws OpsException
  {
    parseCommands("pipeline 'noop \"foo | bar\" | noop' | benchmark");
  }
}
