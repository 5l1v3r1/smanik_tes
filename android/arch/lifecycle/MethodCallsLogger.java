package android.arch.lifecycle;

import android.support.annotation.RestrictTo;
import java.util.HashMap;
import java.util.Map;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class MethodCallsLogger {
  private Map<String, Integer> mCalledMethods = new HashMap();
  
  @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
  public boolean approveCall(String paramString, int paramInt) {
    int i;
    Integer integer = (Integer)this.mCalledMethods.get(paramString);
    boolean bool = false;
    if (integer != null) {
      i = integer.intValue();
    } else {
      i = 0;
    } 
    if ((i & paramInt) != 0)
      bool = true; 
    this.mCalledMethods.put(paramString, Integer.valueOf(paramInt | i));
    return bool ^ true;
  }
}


/* Location:              /home/ardzz/smaniktes/SMAN1kTes (1)-dex2jar.jar!/android/arch/lifecycle/MethodCallsLogger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.7
 */