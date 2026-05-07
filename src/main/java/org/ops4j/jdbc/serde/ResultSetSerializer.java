package org.ops4j.jdbc.serde;

import java.io.IOException;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.NonNull;

public class ResultSetSerializer extends StdSerializer<ResultSet>
{
  private static final long serialVersionUID = -8959382940039919588L;
  private SimpleDateFormat  fmt              = new SimpleDateFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  public ResultSetSerializer()
  {
    this(null);
  }

  public ResultSetSerializer(Class<ResultSet> t)
  {
    super(t);
  }

  @Override
  public void serialize(ResultSet rs, JsonGenerator jgen,
      SerializerProvider provider) throws IOException, JsonProcessingException
  {
    @NonNull
    ResultSetMetaData rsmd;
    int ncol;
    try
    {
      rsmd = rs.getMetaData();
      ncol = rsmd.getColumnCount();
    }
    catch(SQLException ex)
    {
      throw new IOException(ex);
    }
    
    jgen.writeStartObject();

    for (int i = 1; i <= ncol; i++)
    {
      try
      {
        switch (JDBCType.valueOf(rsmd.getColumnType(i)))
        {
          case VARCHAR:
          case NVARCHAR:
          {
            jgen.writeStringField(rsmd.getColumnName(i), rs.getString(i));
            break;
          }
          case DECIMAL:
          {
            jgen.writeNumberField(rsmd.getColumnName(i), rs.getDouble(i));
            break;
          }
          case INTEGER:
          {
            jgen.writeNumberField(rsmd.getColumnName(i), rs.getInt(i));
            break;
          }
          case BOOLEAN:
          {
            jgen.writeBooleanField(rsmd.getColumnName(i), rs.getBoolean(i));
            break;
          }
          case DATE:
          {
            try
            {
              jgen.writeStringField(rsmd.getColumnName(i),
                  fmt.format(rs.getDate(i)));
            }
            catch(IOException | SQLException ex)
            {
              try
              {
                jgen.writeNullField(rsmd.getColumnName(i));
              }
              catch(IOException | SQLException e)
              {
              }
            }
            break;
          }
          default:
          {

          }
        }
      }
      catch(IOException | SQLException ex)
      {
        try
        {
          jgen.writeNullField(rsmd.getColumnName(i));
        }
        catch(IOException | SQLException e)
        {
        }
      }
    }

    jgen.writeEndObject();
  }
}