package org.ops4j.jdbc;

import org.ops4j.base.BaseModule;
import org.ops4j.inf.OpModule;

import com.google.auto.service.AutoService;

import picocli.CommandLine.Command;

@AutoService(OpModule.class)
@Command(name = "jdbc", description = "This module provides JDBC support.")
public class JdbcModule extends BaseModule<JdbcModule>
{
  public JdbcModule()
  {
    super("jdbc", "jdbc");
  }
}
