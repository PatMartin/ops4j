package org.ops4j.op;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(Op.class)
@Command(name = "filter", description = "Filter records.")
public class Filter extends BaseOp<Filter>
{
  @Option(names = { "-i", "--includes" }, description = "The includes.")
  private @Getter @Setter List<String> includes;
  @Option(names = { "-x", "--excludes" }, description = "The excludes.")
  private @Getter @Setter List<String> excludes;

  public Filter()
  {
    super("filter");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    // No args means filter all
    if ((getIncludes() == null || getIncludes().size() == 0)
        && (getExcludes() == null || getExcludes().size() == 0))
    {
      return OpData.emptyList();
    }

    if (getIncludes() != null)
    {
      for (String expr : getIncludes())
      {
        JsonNode result = Ops4J.locator().execute(expr, input.toJson());
        if (result == null || !result.asBoolean())
        {
          DEBUG("EXCLUDING due to: '", expr, "'");
          return OpData.emptyList();
        }
      }
    }

    if (getExcludes() != null)
    {
      for (String expr : getExcludes())
      {
        JsonNode result = Ops4J.locator().execute(expr, input.toJson());
        if (result != null && result.asBoolean())
        {
          // Filter
          DEBUG("EXCLUDING DUE TO: '", expr, "'");
          return OpData.emptyList();
        }
      }
    }

    return input.asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Filter(), args);
  }
}
