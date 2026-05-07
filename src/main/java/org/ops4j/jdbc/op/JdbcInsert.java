package org.ops4j.jdbc.op;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.util.JacksonUtil;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "jdbc-insert",
    description = "Insert recoreds into a JDBC table.")
public class JdbcInsert extends JdbcOp<JdbcInsert>
{
  @Parameters(index = "0", arity = "1", description = "The sql.")
  private @Getter @Setter String sql = null;

  private Statement              statement;
  private ResultSet              rs;

  public JdbcInsert()
  {
    super("jdbc-insert");
  }

  public JdbcInsert open() throws OpsException
  {
    super.open();
    try
    {
      statement = getConnection().createStatement();
    }
    catch(SQLException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    DEBUG("Inserting...: ", getSql());
    String isql = JacksonUtil.interpolate(getSql(), input.getJson());
    DEBUG("Interpolated: ", isql);
    try
    {
      statement.executeUpdate(isql);
    }
    catch(SQLException ex)
    {
      throw new OpsException(ex);
    }
    return input.asList();
  }

  public List<OpData> close() throws OpsException
  {
    try
    {
      statement.close();
    }
    catch(SQLException ex)
    {
      throw new OpsException(ex);
    }
    super.close();
    return OpData.emptyList();
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new JdbcInsert(), args);
  }
}
