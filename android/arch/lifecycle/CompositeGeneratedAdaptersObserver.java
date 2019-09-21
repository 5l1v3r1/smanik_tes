package android.arch.lifecycle;

import android.support.annotation.RestrictTo;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class CompositeGeneratedAdaptersObserver implements GenericLifecycleObserver {
  private final GeneratedAdapter[] mGeneratedAdapters;
  
  CompositeGeneratedAdaptersObserver(GeneratedAdapter[] paramArrayOfGeneratedAdapter) { this.mGeneratedAdapters = paramArrayOfGeneratedAdapter; }
  
  public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent) {
    MethodCallsLogger methodCallsLogger = new MethodCallsLogger();
    GeneratedAdapter[] arrayOfGeneratedAdapter = this.mGeneratedAdapters;
    int i = arrayOfGeneratedAdapter.length;
    byte b2 = 0;
    byte b1;
    for (b1 = 0; b1 < i; b1++)
      arrayOfGeneratedAdapter[b1].callMethods(paramLifecycleOwner, paramEvent, false, methodCallsLogger); 
    arrayOfGeneratedAdapter = this.mGeneratedAdapters;
    i = arrayOfGeneratedAdapter.length;
    for (b1 = b2; b1 < i; b1++)
      arrayOfGeneratedAdapter[b1].callMethods(paramLifecycleOwner, paramEvent, true, methodCallsLogger); 
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/lifecycle/CompositeGeneratedAdaptersObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */