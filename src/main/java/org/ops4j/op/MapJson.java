package org.ops4j.op;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JsonMapper;

import com.google.auto.service.AutoService;

import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "map", description = "This operation is used to map JSON "
    + "documents to alternate forms.")
public class MapJson extends BaseOp<MapJson>
{
  @Parameters(index = "0", arity = "0..*",
      description = "A mapping of the form <dest>=<source> indicating the "
          + "source to destination mapping.%n%nExamples:%n%n"
          + "# map top level field 'src' to 'dest'"
          + "%n/dest=/src%n# set time to current time%n/time=now:%n"
          + "/yesterday='now(-offset=-86400000)'")
  private @Setter Map<String, String> mapping;

  private JsonMapper                  mapper;

  public MapJson()
  {
    super("map");
  }

  public MapJson initialize() throws OpsException
  {
    mapper = new JsonMapper(getMapping(), Ops4J.locator());
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    // syserr("MAP-INPUT: ", input.toString());
    return new OpData(mapper.map(input.getJson())).asList();
  }

  public Map<String, String> getMapping()
  {
    if (mapping == null || mapping.size() == 0)
    {
      mapping = new HashMap<String, String>();
      mapping.put("/", "/");
    }
    return mapping;
  }

  public static void main(String args[]) throws OpsException
  {
    MapJson map = new MapJson();
    OpCLI.cli(new MapJson(), args);
  }
}
