package org.ops4j.exception;

public class ConfigurationException extends OpsException
{

  /**
   * 
   */
  private static final long serialVersionUID = 7052074775228322060L;

  public ConfigurationException(Exception ex)
  {
    super(ex);
  }

  public ConfigurationException(String msg)
  {
    super(msg);
  }
}