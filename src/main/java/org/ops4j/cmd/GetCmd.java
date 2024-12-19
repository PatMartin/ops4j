package org.ops4j.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.ops4j.Ops4J;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import com.typesafe.config.ConfigValueType;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "get", mixinStandardHelpOptions = true,
    description = "Get one or more configuration options.")
public class GetCmd extends SubCmd implements Callable<Integer>
{
  public GetCmd()
  {
    super("get");
  }

  @Parameters(index = "0", arity = "0..1",
      description = "A path to a specfic option.")
  private @Getter @Setter String path;

  @Override
  public Integer call() throws Exception
  {
    if (isHelp())
    {
      help(this);
    }
    else
    {
      Config config = Ops4J.config();

      String json = "";

      if (path == null)
      {
        json = config.root()
            .render(ConfigRenderOptions.concise().setJson(true));
        json = JacksonUtil.prettyMapper().readTree(json).toPrettyString();
      }
      else
      {
        json = getJson(config, path);
      }
      System.out.println(json);
    }

    return 0;

  }

  public String getJson(Config config, String path)
      throws JsonMappingException, JsonProcessingException
  {
    String json = "";
    if (config.hasPath(path))
    {
      ConfigValue value = config.getValue(path);
      ConfigValueType type = value.valueType();
      switch (type)
      {
        case LIST:
        {
          // TODO: HANDLE LISTS.
          break;
        }
        case OBJECT:
        {
          json = config.getConfig(path).root()
              .render(ConfigRenderOptions.concise().setJson(true));
          json = JacksonUtil.prettyMapper().readTree(json).toPrettyString();
          break;
        }
        case STRING:
        {
          json = config.getString(path);
          break;
        }
        case BOOLEAN:
        {
          json = "" + config.getBoolean(path);
          break;
        }
        case NUMBER:
        {
          json = "" + config.getNumber(path);
          break;
        }
        case NULL:
        {
          json = "null";
        }
      }
    }
    return json;
  }

  public static void main(String args[])
  {
    CommandLine cli = new CommandLine(new GetCmd());
    cli.execute(args);
  }
}
