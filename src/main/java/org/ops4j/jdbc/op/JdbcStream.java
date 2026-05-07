package org.ops4j.jdbc.op;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.JsonSource;
import org.ops4j.inf.Op;
import org.ops4j.jdbc.util.ResultSetIterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@AutoService(Op.class) @Command(name = "jdbc-stream",
    description = "Stream documents to and from a jdbc source.")
public class JdbcStream extends JdbcOp<JdbcStream> implements JsonSource
{
  private @Getter ResultSetIterator it  = null;
  private Statement                 statement;
  private ResultSet                 rs;

  @Parameters(index = "0", arity = "0..1", description = "The database sql.")
  private @Getter @Setter String    sql = null;

  public JdbcStream()
  {
    super("jdbc-stream");
  }

  public JdbcStream initialize() throws OpsException
  {
    super.initialize();
    return this;
  }

  public JdbcStream open() throws OpsException
  {
    super.open();
    try
    {
      statement = getConnection().createStatement();
      rs = statement
          .executeQuery(fallback(getSql(), getConfig().getString("sql")));
      it = new ResultSetIterator(rs);
    }
    catch(SQLException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> execute(OpData input)
  {
    JsonNode node = it.next();
    return new OpData(node).asList();
  }

  public List<OpData> close() throws OpsException
  {
    try
    {
      rs.close();
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
    OpCLI.cli(new JdbcStream(), args);
  }

  public Iterator<JsonNode> getIterator()
  {
    return it;
  }
}
