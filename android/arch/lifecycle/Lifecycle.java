package android.arch.lifecycle;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

public abstract class Lifecycle {
  @MainThread
  public abstract void addObserver(@NonNull LifecycleObserver paramLifecycleObserver);
  
  @MainThread
  @NonNull
  public abstract State getCurrentState();
  
  @MainThread
  public abstract void removeObserver(@NonNull LifecycleObserver paramLifecycleObserver);
  
  public enum Event {
    ON_ANY, ON_CREATE, ON_DESTROY, ON_PAUSE, ON_RESUME, ON_START, ON_STOP;
    
    static  {
      ON_RESUME = new Event("ON_RESUME", 2);
      ON_PAUSE = new Event("ON_PAUSE", 3);
      ON_STOP = new Event("ON_STOP", 4);
      ON_DESTROY = new Event("ON_DESTROY", 5);
      ON_ANY = new Event("ON_ANY", 6);
      $VALUES = new Event[] { ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY, ON_ANY };
    }
  }
  
  public enum State {
    DESTROYED, CREATED, INITIALIZED, RESUMED, STARTED;
    
    static  {
      CREATED = new State("CREATED", 2);
      STARTED = new State("STARTED", 3);
      RESUMED = new State("RESUMED", 4);
      $VALUES = new State[] { DESTROYED, INITIALIZED, CREATED, STARTED, RESUMED };
    }
    
    public boolean isAtLeast(@NonNull State param1State) { return (compareTo(param1State) >= 0); }
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/lifecycle/Lifecycle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */