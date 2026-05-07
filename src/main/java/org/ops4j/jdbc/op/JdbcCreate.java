package org.ops4j.jdbc.op;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.jdbc.util.JdbcUtil;
import org.ops4j.util.TypeGuesser;
import org.ops4j.util.TypeGuesser.InferredType;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "jdbc-create",
    description = "Stream data into a jdbc table which will be created "
        + "dynamically.")
public class JdbcCreate extends JdbcOp<JdbcCreate>
{
  @Parameters(index = "0", arity = "0..1",
      description = "The table to create.   DEFAULT='${DEFAULT-VALUE}'")
  public @Getter @Setter String     target    = "OPS_TEMP";

  @Option(names = { "--threshold" },
      description = "The commit threshold.  This represents the number of "
          + "records to be used in schema inference.  "
          + "DEFAULT='${DEFAULT-VALUE}'")
  public @Getter @Setter int        threshold = 100;

  private List<ObjectNode>          data      = new ArrayList<ObjectNode>();
  private PreparedStatement         pstmt     = null;
  private boolean                   CREATED   = false;
  private Map<String, InferredType> types     = null;

  public JdbcCreate()
  {
    super("jdbc-create");
  }

  public JdbcCreate open() throws OpsException
  {
    super.open();
    try
    {
      Statement stmt = getConnection().createStatement();
      stmt.executeUpdate("DROP TABLE " + getTarget());
      stmt.close();
    }
    catch(SQLException ex)
    {
      // Best effort, ignore.
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    if (CREATED)
    {
      DEBUG("APPENDING...");
      try
      {
        insert(pstmt, types, input.getJson());
      }
      catch(SQLException ex)
      {
        throw new OpsException(ex);
      }
    }
    else if (data.size() < getThreshold())
    {
      DEBUG("ADDING[", data.size(), "]");
      data.add(input.getJson());
    }
    else if (data.size() >= getThreshold())
    {
      CREATED = true;
      try
      {
        createTable();
      }
      catch(SQLException ex)
      {
        throw new OpsException(ex);
      }
      // DEBUG("COMMITTING: " + JacksonUtil.toPrettyString(types, "ERROR"));
    }

    return input.asList();
  }

  public void createTable() throws SQLException
  {
    types = TypeGuesser.guessTypes(data);
    List<String> expressions = new ArrayList<String>();

    for (Entry<String, InferredType> entry : types.entrySet())
    {
      String jdbcName = JdbcUtil.toJdbcName(entry.getKey());
      switch (entry.getValue())
      {
        case DOUBLE:
        {
          expressions.add(jdbcName + " FLOAT");
          break;
        }
        case INTEGER:
        {
          expressions.add(jdbcName + " INTEGER");
          break;
        }
        case STRING:
        {
          expressions.add(jdbcName + " VARCHAR(2000)");
          break;
        }
        default:
        {
          expressions.add(jdbcName + " VARCHAR(2000)");
          break;
        }
      }
    }
    String createSql = "CREATE TABLE " + JdbcUtil.toJdbcName(getTarget()) + "("
        + StringUtils.join(expressions, ", ") + ")";
    DEBUG("CREATE: ", createSql);
    DEBUG("INSERT: ");

    Statement stmt = getConnection().createStatement();
    stmt.executeUpdate(createSql);
    stmt.close();
    DEBUG("SETTING CREATED...");
    CREATED = true;

    String insertSql = "INSERT INTO " + JdbcUtil.toJdbcName(getTarget()) + "("
        + StringUtils.join(types.keySet().stream()
            .map(s -> JdbcUtil.toJdbcName(s)).collect(Collectors.toList()), ",")
        + ") VALUES("
        + StringUtils.join(
            types.keySet().stream().map(s -> "?").collect(Collectors.toList()),
            ",")
        + ")";

    DEBUG("INSERT: ", insertSql);
    pstmt = getConnection().prepareStatement(insertSql);

    for (ObjectNode json : data)
    {
      insert(pstmt, types, json);
    }
    data.clear();

  }

  public void insert(PreparedStatement pstmt, Map<String, InferredType> types,
      ObjectNode json) throws SQLException
  {
    int pi = 1;
    for (String name : types.keySet())
    {
      switch (types.get(name))
      {
        case DOUBLE:
        {
          pstmt.setDouble(pi, json.get(name).asDouble());
          break;
        }
        case INTEGER:
        {
          pstmt.setInt(pi, json.get(name).asInt());
          break;
        }
        case STRING:
        {
          pstmt.setString(pi, json.get(name).asText());
          break;
        }
        default:
        {

        }
      }
      pi++;
    }
    pstmt.execute();
  }

  public List<OpData> close() throws OpsException
  {
    try
    {
      if (CREATED == false)
      {
        createTable();
      }
      pstmt.close();
    }
    catch(SQLException ex)
    {
      // Best effort, ignore.
    }
    super.close();
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new JdbcCreate(), args);
  }
}
