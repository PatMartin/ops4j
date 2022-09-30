package org.ops4j.op;

import java.util.List;
import java.util.Map;

import org.ops4j.BaseOp;
import org.ops4j.OpData;
import org.ops4j.Ops4J;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JsonMapper;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "map", description = "This operation is used to map JSON "
    + "documents to alternate forms.")
public class MapJson extends BaseOp<MapJson>
{
  @Parameters(index = "0", arity = "0..*", description = "<dest>=<source>")
  private @Getter @Setter Map<String, String> mapping;

  private JsonMapper                          mapper;

  public MapJson()
  {
    super("map");
  }

  public MapJson initialize() throws OpsException
  {
    mapper = new JsonMapper(getMapping(), Ops4J.getLocator());
    mapper.setLogLevel(getOpLogger().getLogLevel());
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    return new OpData(mapper.map(input.getJson())).asList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new MapJson(), args);
  }
}
