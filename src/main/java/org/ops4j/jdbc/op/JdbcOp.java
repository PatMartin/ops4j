package org.ops4j.jdbc.op;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.exception.OpsException;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "jdbc:op",
    description = "Insert documents into a mongo collection.")
public abstract class JdbcOp<T extends JdbcOp<?>> extends BaseOp<JdbcOp<T>>
{
  @Option(names = { "--driver" }, required = false,
      description = "The driver class.")
  private @Getter @Setter String     driverClass;

  @Option(names = { "--url" }, required = false,
      description = "The connection url.")
  private @Getter @Setter String     url;

  @Option(names = { "--db" }, description = "The database name.")
  private @Getter @Setter String     db;

  @Option(names = { "--user" }, required = false, description = "The username.")
  private @Getter @Setter String     username;

  @Option(names = { "--schema" }, required = false,
      description = "The db schema.")
  private @Getter @Setter String     schema;

  @Option(names = { "-p", "--password" }, required = false,
      description = "The password.")
  private @Getter @Setter String     password;

  private @Getter @Setter Connection connection;

  public JdbcOp(String name)
  {
    super(name);
    defaultView("DEFAULT.JDBC");
  }

  public JdbcOp<T> initialize() throws OpsException
  {
    info("JDBC-CONFIG: ", config());
    return this;
  }

  public JdbcOp<T> open() throws OpsException
  {
    super.open();
    try
    {
      // Class.forName("org.hsqldb.jdbcDriver");
      // connection = DriverManager.getConnection(
      // "jdbc:hsqldb:file:C:/ws/ops4j-all/test/db/testdb;shutdown=true", "SA",
      // "");
      Class
          .forName(fallback(getDriverClass(), getConfig().getString("driver")));
      connection = DriverManager.getConnection(
          fallback(getUrl(), getConfig().getString("url")),
          fallback(getUsername(), getConfig().getString("username")),
          fallback(getPassword(), getConfig().getString("password")));
    }
    catch(SQLException | ClassNotFoundException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData>close() throws OpsException
  {
    try
    {
      connection.close();
    }
    catch(SQLException ex)
    {
      throw new OpsException(ex);
    }
    super.close();
    return OpData.emptyList();
  }
}
