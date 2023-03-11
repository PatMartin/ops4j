package org.ops4j.repo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ops4j.base.BaseOp;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.inf.OpRepo;
import org.ops4j.op.NoOp;
import org.ops4j.op.Pipeline;
import org.ops4j.util.Debugger;
import org.ops4j.util.JacksonUtil;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@AutoService(OpRepo.class)
@Command(name = "fs-repo", mixinStandardHelpOptions = false)
public class FilesystemOpRepo extends BaseOpRepo<FilesystemOpRepo>
{
  @Option(names = { "-p", "--path" }, required = true,
      description = "The path to the directory which will store "
          + "the operations.")
  public @Getter @Setter String path = null;

  public FilesystemOpRepo()
  {
    super();
  }

  @Override
  public List<String> names() throws OpsException
  {
    List<String> names = new ArrayList<>();
    File f = new File(getPath());
    if (f.exists() && f.isDirectory())
    {
      for (String name : f.list())
      {
        if (name.endsWith(".op"))
        {
          names.add(name);
        }
      }
    }

    return names;
  }

  @Override
  public Op<?> load(String name) throws OpsException
  {
    File f = new File(getPath() + File.separator + name + ".op");
    try
    {
      // This is the reason that we have to support create() in the Op interface.
      return JacksonUtil.mapper().readValue(f, BaseOp.class);
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
  }

  @Override
  public void store(String name, Op<?> value) throws OpsException
  {
    File f = new File(getPath() + File.separator + name + ".op");
    System.out.println("PATH: " + getPath() + File.separator + name + ".op");
    try
    {
      JacksonUtil.mapper().writeValue(f, value);
    }
    catch(IOException ex)
    {
      throw new OpsException(ex);
    }
  }

  public static void main(String args[]) throws OpsException
  {
    FilesystemOpRepo repo = new FilesystemOpRepo();
    repo.setPath("C:/ops4j/repos/dev1");
    Debugger.sysout(repo.names());
    NoOp noop = new NoOp();
    repo.store("noop1", noop);
    Op<?> noop2 = repo.load("noop1");
    System.out.println("Read: " + noop2.getName());
    Pipeline p = new Pipeline().name("P").add(new NoOp());
    repo.store("pipeline", p);
    Op<?> p2 = repo.load("pipeline");
  }
}
