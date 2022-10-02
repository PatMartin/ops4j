package org.ops4j;

import java.util.HashMap;
import java.util.Map;

import org.ops4j.inf.Op.PhaseType;

import lombok.Getter;
import lombok.Setter;

public class Lifecycle
{
  private @Getter @Setter Map<PhaseType, Boolean> phases;

  public Lifecycle()
  {
    willProvide(PhaseType.INITIALIZE, PhaseType.OPEN, PhaseType.EXECUTE,
        PhaseType.CLOSE, PhaseType.CLEANUP);
  }

  public Lifecycle willProvide(PhaseType... supports)
  {
    Map<PhaseType, Boolean> phases = new HashMap<PhaseType, Boolean>();
    for (PhaseType type : supports)
    {
      phases.put(type, true);
    }
    setPhases(phases);
    return this;
  }

  public boolean provides(PhaseType type)
  {
    return phases.containsKey(type);
  }
}
