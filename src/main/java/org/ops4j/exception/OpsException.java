package org.ops4j.exception;

import java.io.PrintStream;

public class OpsException extends Exception
{
  // generated
  private static final long serialVersionUID = -5240903664743533452L;
  private Exception ex = null;

  public OpsException()
  {
    super();
  }

  public OpsException(String errorMessage)
  {
    super(errorMessage);
  }

  public OpsException(String errorMessage, Exception ex)
  {
    super(errorMessage);
    this.ex = ex;
  }

  public OpsException(Exception ex)
  {
    super();
    this.ex = ex;
  }

  public boolean containsException()
  {
    return ex != null;
  }

  public Exception getException()
  {
    return ex;
  }

  public void printStackTrace()
  {
    printStackTrace(System.out);
  }

  public void printStackTrace(PrintStream out)
  {
    super.printStackTrace(out);
    if (containsException())
    {
      out.println("This exception contains another exception:");
      out.println("------------------------------------------");
      getException().printStackTrace(out);
    }
  }
}