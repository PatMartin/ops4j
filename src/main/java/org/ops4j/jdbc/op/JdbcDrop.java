package org.ops4j.jdbc.op;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.ops4j.Lifecycle;
import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class)
@Command(name = "jdbc-drop", description = "Drop a table.")
public class JdbcDrop extends JdbcOp<JdbcDrop>
{
  @Parameters(index = "0", arity = "0..1", description = "The table to drop."
      + "  DEFAULT='${DEFAULT-VALUE}'")
  private @Getter @Setter String table = "OPS.OPS_TEMP";

  private Statement              statement;

  public JdbcDrop()
  {
    super("jdbc-drop");
    setLifecycle(Lifecycle.withPhases(PhaseType.OPEN, PhaseType.CLOSE));
  }

  public JdbcDrop open() throws OpsException
  {
    super.open();
    try
    {
      statement = getConnection().createStatement();
      statement.executeUpdate("DROP TABLE " + getTable());
    }
    catch(SQLException ex)
    {
      throw new OpsException(ex);
    }
    return this;
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
    OpCLI.cli(new JdbcDrop(), args);
  }
}
