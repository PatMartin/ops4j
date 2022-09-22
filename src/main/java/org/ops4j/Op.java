package org.ops4j;

import java.util.List;
import java.util.Map;

import org.ops4j.OpLogger.LogLevel;
import org.ops4j.exception.OpsException;

public interface Op<T extends Op<T>> extends LocalOpLogger
{
  public enum PhaseType {
    INITIALIZE, OPEN, EXECUTE, EOS, FLUSH, CLEANUP, CLOSE
  }

  public Map<PhaseType, Boolean> getPhases();

  public default boolean hasPhase(PhaseType phase)
  {
    return getPhases().containsKey(phase);
  }

  public T initialize() throws OpsException;

  public T open() throws OpsException;

  public List<OpData> execute(OpData input) throws OpsException;

  public List<OpData> flush() throws OpsException;

  public T close() throws OpsException;

  public T cleanup() throws OpsException;

  public String getName();

  public void setName(String name);

  public void configure(String args) throws OpsException;

  public void configure(String args[]) throws OpsException;

  public void configure(List<String> args) throws OpsException;

  public void setLogLevel(LogLevel logLevel);

  public LogLevel getLogLevel();

  public Op<T> create();
}
