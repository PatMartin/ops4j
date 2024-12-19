package org.ops4j.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ops4j.exception.OpsException;
import org.ops4j.util.JacksonUtil;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@AutoService(InputSource.class) @Command(name = "csv",
    mixinStandardHelpOptions = false, description = "Streams a csv.")
public class CsvSource extends BaseSource<CsvSource>
{
  @Parameters(index = "0", arity = "1", paramLabel = "<input-location>",
      description = "The location of the input.")
  private @Getter @Setter String location = null;

  enum CsvType {
    DEFAULT, EXCEL, RFC4180, MONGODB_CSV, MONGODB_TSV, INFORMIX_UNLOAD,
    INFORMIX_UNLOAD_CSV, MYSQL, ORACLE, POSTGRESQL_CSV, POSTGRESQL_TEXT, TDF
  }

  @Option(names = { "-f", "--format" },
      description = "The format.  DEFAULT='${DEFAULT-VALUE}'"
      + "%nVALID VALUES: ${COMPLETION-CANDIDATES})")
  private @Getter @Setter CsvType format = CsvType.DEFAULT;

  public CsvSource()
  {
    super("csv");
  }

  public CsvSource create()
  {
    return new CsvSource();
  }

  public InputStream stream() throws OpsException
  {
    try
    {
      StringWriter sw = new StringWriter();
      getLogger().debug("LOCATION=", getLocation());
      InputStream is = new BufferedInputStream(
          new FileInputStream(new File(getLocation())), 131072);

      CSVFormat fmt;

      switch (getFormat())
      {
        case DEFAULT:
        {
          fmt = CSVFormat.DEFAULT;
          break;
        }
        case EXCEL:
        {
          fmt = CSVFormat.EXCEL;
          break;
        }
        case INFORMIX_UNLOAD:
        {
          fmt = CSVFormat.INFORMIX_UNLOAD;
          break;
        }
        case INFORMIX_UNLOAD_CSV:
        {
          fmt = CSVFormat.INFORMIX_UNLOAD_CSV;
          break;
        }
        case MONGODB_CSV:
        {
          fmt = CSVFormat.MONGODB_CSV;
          break;
        }
        case MONGODB_TSV:
        {
          fmt = CSVFormat.MONGODB_TSV;
          break;
        }
        case MYSQL:
        {
          fmt = CSVFormat.MYSQL;
          break;
        }
        case ORACLE:
        {
          fmt = CSVFormat.ORACLE;
          break;
        }
        case POSTGRESQL_CSV:
        {
          fmt = CSVFormat.POSTGRESQL_CSV;
          break;
        }
        case POSTGRESQL_TEXT:
        {
          fmt = CSVFormat.POSTGRESQL_TEXT;
          break;
        }
        case RFC4180:
        {
          fmt = CSVFormat.RFC4180;
          break;
        }
        case TDF:
        {
          fmt = CSVFormat.TDF;
          break;
        }
        default:
        {
          fmt = CSVFormat.DEFAULT;
        }
      }

      CSVParser parser = fmt.builder().setHeader().setSkipHeaderRecord(true)
          .build().parse(new InputStreamReader(is));
      List<String> headers = parser.getHeaderNames();
      getLogger().debug("HEADERS.length=" + headers.size());
      for (CSVRecord record : parser)
      {
        ObjectNode json = JacksonUtil.createObjectNode();
        for (String header : headers)
        {
          getLogger().debug("PUT: '", header, "'='", record.get(header), "'");
          json.put(header, record.get(header));
        }
        getLogger().debug("WRITING JSON: '" + json + "'");
        sw.write(json.toString());
      }
      is.close();
      return new ByteArrayInputStream(sw.toString().getBytes());
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static void main(String args[]) throws OpsException
  {
    InputSourceCLI.cli(new CsvSource(), args);
  }
}
