package org.ops4j.op.shell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.exception.OpsException;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "shell:op", description = "The base shell operation.")
public abstract class ShellOp<T extends ShellOp<?>> extends BaseOp<ShellOp<T>>
{
  @Parameters(index = "0", arity = "1..*",
      description = "The command to execute.")
  private @Getter @Setter List<String> commands = new ArrayList<String>();

  protected Process                      proc;
  
  public ShellOp(String name)
  {
    super(name);
  }

  public ShellOp<T> initialize() throws OpsException
  {
    return this;
  }

  public ShellOp<T> open() throws OpsException
  {
    super.open();
    try
    {
      DEBUG("COMMANDS: ", getCommands());
      proc = Runtime.getRuntime().exec(getCommands().toArray(new String[0]));
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
    return this;
  }

  public List<OpData> close() throws OpsException
  {
    return super.close();
  }
}