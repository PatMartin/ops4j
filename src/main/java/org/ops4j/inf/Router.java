package org.ops4j.inf;

import java.util.List;

import org.ops4j.OpData;
import org.ops4j.exception.OpsException;

public interface Router
{
  public List<OpData> route(OpData input) throws OpsException;
  public List<OpData> close() throws OpsException;
}
