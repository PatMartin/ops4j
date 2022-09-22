package org.ops4j.exception;

public class AccessibilityException extends OpsException
{
  /**
   * 
   */
  private static final long serialVersionUID = -2674687359596711281L;

  public AccessibilityException(Exception ex)
  {
    super(ex);
  }

  public AccessibilityException(String msg)
  {
    super(msg);
  }
}